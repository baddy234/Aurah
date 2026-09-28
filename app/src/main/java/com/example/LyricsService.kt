package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LyricsCandidate(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val durationSeconds: Double,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?
) {
    val hasSynced: Boolean get() = !syncedLyrics.isNullOrBlank()
    val hasPlain: Boolean get() = !plainLyrics.isNullOrBlank()
    val formattedDuration: String get() {
        val totalSec = durationSeconds.toInt()
        if (totalSec <= 0) return ""
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format("%d:%02d", m, s)
    }
}

sealed class LyricsFetchResult {
    data class Success(
        val syncedLyrics: String?,
        val plainLyrics: String?,
        val trackTitle: String,
        val artist: String,
        val isInstrumental: Boolean,
        val source: String,
        val candidateId: Long? = null
    ) : LyricsFetchResult()

    data class Error(val message: String) : LyricsFetchResult()
}

class LyricsService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()
) {
    fun cleanMetadata(rawTitle: String, rawArtist: String): Pair<String, String> {
        var title = rawTitle.trim()
        var artist = rawArtist.trim()

        title = title.replace(Regex("(?i)\\.(mp3|wav|flac|m4a|aac|ogg|opus|mp4|mkv|webm|avi|mov)$"), "")
        title = title.replace(Regex("^[0-9]{1,3}[\\s\\.\\-_]+"), "")

        if (artist.isBlank() || artist.equals("Device Audio", ignoreCase = true) ||
            artist.equals("Unknown Artist", ignoreCase = true) || artist.equals("Local Audio", ignoreCase = true) ||
            artist.equals("<unknown>", ignoreCase = true)) {
            if (title.contains(" - ")) {
                val parts = title.split(" - ", limit = 2)
                artist = parts[0].trim()
                title = parts[1].trim()
            }
        }

        val bracketNoise = listOf(
            "(?i)\\[official video\\]",
            "(?i)\\[official music video\\]",
            "(?i)\\[official audio\\]",
            "(?i)\\[audio\\]",
            "(?i)\\[music video\\]",
            "(?i)\\[lyrics\\]",
            "(?i)\\[lyric video\\]",
            "(?i)\\[hd\\]",
            "(?i)\\[4k\\]",
            "(?i)\\[hq\\]",
            "(?i)\\[remastered[^\\]]*\\]",
            "(?i)\\[live[^\\]]*\\]",
            "(?i)\\(official video\\)",
            "(?i)\\(official music video\\)",
            "(?i)\\(official audio\\)",
            "(?i)\\(audio\\)",
            "(?i)\\(music video\\)",
            "(?i)\\(lyrics\\)",
            "(?i)\\(lyric video\\)",
            "(?i)\\(hd\\)",
            "(?i)\\(4k\\)",
            "(?i)\\(hq\\)",
            "(?i)\\([0-9]{4}\\s*remaster\\)",
            "(?i)\\([0-9]{4}\\s*remastered\\)",
            "(?i)\\(live[\\s\\-_a-zA-Z0-9]*\\)",
            "(?i)\\|.*$",
            "(?i)320kbps",
            "(?i)128kbps"
        )
        for (pattern in bracketNoise) {
            title = title.replace(Regex(pattern), "")
        }

        val featRegex = Regex("(?i)\\s*\\(?(feat\\.|ft\\.|featuring)\\s+([^\\)]+)\\)?")
        val featMatch = featRegex.find(title)
        if (featMatch != null) {
            val featArtist = featMatch.groupValues[2].trim()
            title = title.replace(featMatch.value, "").trim()
        }

        title = title.trim().replace(Regex("\\s+"), " ")
        artist = artist.trim().replace(Regex("\\s+"), " ")

        return Pair(title, artist)
    }

    suspend fun fetchLyrics(
        title: String,
        artist: String,
        durationSeconds: Int? = null
    ): LyricsFetchResult = withContext(Dispatchers.IO) {
        val (cleanTitle, cleanArtist) = cleanMetadata(title, artist)

        val getResult = tryGetExact(cleanTitle, cleanArtist, durationSeconds)
        if (getResult is LyricsFetchResult.Success) {
            return@withContext getResult
        }

        val searchResult = trySearchFirst(cleanTitle, cleanArtist)
        if (searchResult is LyricsFetchResult.Success) {
            return@withContext searchResult
        }

        if (cleanTitle != title.trim()) {
            val rawSearchResult = trySearchFirst(title.trim(), artist.trim())
            if (rawSearchResult is LyricsFetchResult.Success) {
                return@withContext rawSearchResult
            }
        }

        return@withContext LyricsFetchResult.Error("No synchronized lyrics found online for \"$cleanTitle\"")
    }

    suspend fun searchLyricsCandidates(
        query: String,
        title: String? = null,
        artist: String? = null
    ): List<LyricsCandidate> = withContext(Dispatchers.IO) {
        val results = mutableListOf<LyricsCandidate>()
        try {
            val searchQuery = query.ifBlank {
                val (cT, cA) = cleanMetadata(title ?: "", artist ?: "")
                if (cA.isNotBlank() && !cA.equals("Device Audio", ignoreCase = true) && !cA.equals("Unknown Artist", ignoreCase = true)) {
                    "$cT $cA"
                } else {
                    cT
                }
            }

            val urlBuilder = "https://lrclib.net/api/search".toHttpUrlOrNull()?.newBuilder() ?: return@withContext emptyList()
            urlBuilder.addQueryParameter("q", searchQuery)

            val request = Request.Builder()
                .url(urlBuilder.build())
                .header("User-Agent", "MediaPlayer/2.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@withContext emptyList()
                    val array = JSONArray(body)
                    for (i in 0 until array.length()) {
                        val item = array.optJSONObject(i) ?: continue
                        val id = item.optLong("id", 0L)
                        val trackName = item.optString("trackName").ifBlank { item.optString("name", "Unknown Track") }
                        val artistName = item.optString("artistName", "Unknown Artist")
                        val albumName = item.optString("albumName").takeIf { it.isNotBlank() }
                        val duration = item.optDouble("duration", 0.0)
                        val instrumental = item.optBoolean("instrumental", false)
                        val plain = item.optString("plainLyrics").takeIf { it.isNotBlank() }
                        val synced = item.optString("syncedLyrics").takeIf { it.isNotBlank() }

                        if (synced != null || plain != null || instrumental) {
                            results.add(
                                LyricsCandidate(
                                    id = id,
                                    trackName = trackName,
                                    artistName = artistName,
                                    albumName = albumName,
                                    durationSeconds = duration,
                                    instrumental = instrumental,
                                    plainLyrics = plain,
                                    syncedLyrics = synced
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext results.sortedWith(
            compareByDescending<LyricsCandidate> { it.hasSynced }
                .thenByDescending { it.hasPlain }
        )
    }

    private fun tryGetExact(title: String, artist: String, duration: Int?): LyricsFetchResult? {
        try {
            if (artist.isBlank() || artist.equals("Device Audio", ignoreCase = true) || artist.equals("Unknown Artist", ignoreCase = true)) {
                return null
            }
            val urlBuilder = "https://lrclib.net/api/get".toHttpUrlOrNull()?.newBuilder() ?: return null
            urlBuilder.addQueryParameter("track_name", title)
            urlBuilder.addQueryParameter("artist_name", artist)
            if (duration != null && duration > 0) {
                urlBuilder.addQueryParameter("duration", duration.toString())
            }

            val request = Request.Builder()
                .url(urlBuilder.build())
                .header("User-Agent", "MediaPlayer/2.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val json = JSONObject(body)
                    val synced = json.optString("syncedLyrics").takeIf { it.isNotBlank() }
                    val plain = json.optString("plainLyrics").takeIf { it.isNotBlank() }
                    val isInstrumental = json.optBoolean("instrumental", false)
                    val respTitle = json.optString("trackName").ifBlank { json.optString("name", title) }
                    val respArtist = json.optString("artistName", artist)
                    val id = json.optLong("id", 0L)

                    if (synced != null || plain != null || isInstrumental) {
                        return LyricsFetchResult.Success(
                            syncedLyrics = synced,
                            plainLyrics = plain,
                            trackTitle = respTitle,
                            artist = respArtist,
                            isInstrumental = isInstrumental,
                            source = "LRCLIB (Verified)",
                            candidateId = id
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    private fun trySearchFirst(title: String, artist: String): LyricsFetchResult? {
        try {
            val query = if (artist.isNotBlank() && !artist.equals("Unknown Artist", ignoreCase = true) && !artist.equals("Device Audio", ignoreCase = true)) {
                "$title $artist"
            } else {
                title
            }
            val urlBuilder = "https://lrclib.net/api/search".toHttpUrlOrNull()?.newBuilder() ?: return null
            urlBuilder.addQueryParameter("q", query)

            val request = Request.Builder()
                .url(urlBuilder.build())
                .header("User-Agent", "MediaPlayer/2.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val array = JSONArray(body)
                    var bestWithSynced: JSONObject? = null
                    var firstMatch: JSONObject? = null

                    for (i in 0 until array.length()) {
                        val item = array.optJSONObject(i) ?: continue
                        if (firstMatch == null) firstMatch = item
                        val synced = item.optString("syncedLyrics")
                        if (synced.isNotBlank()) {
                            bestWithSynced = item
                            break
                        }
                    }

                    val chosen = bestWithSynced ?: firstMatch
                    if (chosen != null) {
                        val synced = chosen.optString("syncedLyrics").takeIf { it.isNotBlank() }
                        val plain = chosen.optString("plainLyrics").takeIf { it.isNotBlank() }
                        val isInstrumental = chosen.optBoolean("instrumental", false)
                        val respTitle = chosen.optString("trackName").ifBlank { chosen.optString("name", title) }
                        val respArtist = chosen.optString("artistName", artist)
                        val id = chosen.optLong("id", 0L)

                        if (synced != null || plain != null || isInstrumental) {
                            return LyricsFetchResult.Success(
                                syncedLyrics = synced,
                                plainLyrics = plain,
                                trackTitle = respTitle,
                                artist = respArtist,
                                isInstrumental = isInstrumental,
                                source = if (synced != null) "LRCLIB (Synced)" else "LRCLIB (Plain Text)",
                                candidateId = id
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            return LyricsFetchResult.Error("Network search error: ${e.localizedMessage ?: "Check connection"}")
        }
        return null
    }
}
