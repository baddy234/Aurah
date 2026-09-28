package com.example

import java.util.regex.Pattern

data class LyricLine(
    val timeMs: Long,
    val text: String,
    val rawTimestamp: String = ""
)

enum class LyricsViewMode {
    SYNCED,
    PLAIN,
    EDIT
}

enum class SyncedLyricsDisplayStyle {
    LIST,
    SINGLE_LINE,
    FOCUS
}

object LrcParser {
    // Regex matches [mm:ss.xx] or [mm:ss.xxx] or [mm:ss]
    private val TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?\\]")

    /**
     * Parses standard LRC formatted lyrics into an ordered list of LyricLine objects.
     * If lyrics contain no timestamps, returns an empty list or lines with timeMs = -1.
     */
    fun parse(lrcText: String): List<LyricLine> {
        if (lrcText.isBlank()) return emptyList()

        val lines = lrcText.lines()
        val result = mutableListOf<LyricLine>()

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isBlank()) continue

            // Skip metadata tags like [ti:title], [ar:artist], [al:album], [offset:0]
            if (line.matches(Regex("^\\[[a-zA-Z]+:[^\\]]*\\]$"))) {
                continue
            }

            val matcher = TIMESTAMP_PATTERN.matcher(line)
            val timestamps = mutableListOf<Pair<Long, String>>()

            var lastEnd = 0
            while (matcher.find()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0L
                val sec = matcher.group(2)?.toLongOrNull() ?: 0L
                val fracStr = matcher.group(3) ?: "0"
                val frac = when (fracStr.length) {
                    1 -> (fracStr.toLongOrNull() ?: 0L) * 100L
                    2 -> (fracStr.toLongOrNull() ?: 0L) * 10L
                    else -> (fracStr.take(3).toLongOrNull() ?: 0L)
                }

                val timeMs = (min * 60_000L) + (sec * 1000L) + frac
                timestamps.add(Pair(timeMs, matcher.group(0) ?: ""))
                lastEnd = matcher.end()
            }

            val lyricText = if (lastEnd > 0 && lastEnd <= line.length) {
                line.substring(lastEnd).trim()
            } else {
                line
            }

            if (timestamps.isNotEmpty()) {
                for ((timeMs, rawTag) in timestamps) {
                    result.add(LyricLine(timeMs = timeMs, text = lyricText, rawTimestamp = rawTag))
                }
            }
        }

        return result.sortedBy { it.timeMs }
    }

    /**
     * Formats milliseconds into [mm:ss.xx] timestamp string.
     */
    fun formatTimestamp(timeMs: Long): String {
        val totalSecs = (timeMs / 1000L).coerceAtLeast(0L)
        val minutes = totalSecs / 60L
        val seconds = totalSecs % 60L
        val centis = (timeMs % 1000L) / 10L
        return String.format("%02d:%02d.%02d", minutes, seconds, centis)
    }

    /**
     * Converts a timestamp in ms to short display format mm:ss (e.g. 01:24)
     */
    fun formatShortTime(timeMs: Long): String {
        val totalSecs = (timeMs / 1000L).coerceAtLeast(0L)
        val minutes = totalSecs / 60L
        val seconds = totalSecs % 60L
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Serializes LyricLine list back to LRC format text.
     */
    fun toLrcString(lines: List<LyricLine>): String {
        return lines.joinToString("\n") { line ->
            "[${formatTimestamp(line.timeMs)}] ${line.text}"
        }
    }

    /**
     * Strips timestamps from LRC text to produce clean plain lyrics.
     */
    fun toPlainText(lrcText: String): String {
        return lrcText.lines()
            .map { line -> line.replace(Regex("\\[\\d{1,2}:\\d{2}(?:\\.\\d{1,3})?\\]"), "").trim() }
            .filter { it.isNotBlank() && !it.matches(Regex("^\\[[a-zA-Z]+:.*\\]$")) }
            .joinToString("\n")
    }
}

object DefaultLyricsRepository {
    val BUILT_IN_SYNCED_LYRICS = mapOf(
        "1" to """
            [00:03.50] I feel my skin connected with the universe
            [00:10.39] I can turn on the light, the moon or the sun from my heart
            [00:18.55] I feel my body shining for the rest of our hearts
            [00:26.10] From the beginning to end, there's no doubt my mind is free now
            [00:34.20] When we wake up, wake up, wake up into the golden light
            [00:43.00] Can you hear the rhythm pulsing through the quiet night?
            [00:52.15] Breathe in the harmony of distant stars
            [01:00.80] We leave behind the echoes and the old scars
            [01:10.45] Drifting on waves of frequency and deep soul
            [01:19.30] Every vibration makes us complete and whole
            [01:29.00] Electric visions dancing in our waking eyes
            [01:38.20] Rise with the melody into clear blue skies
            [01:48.50] When we wake up, we wake up to the sound
            [01:58.10] The sweetest serenity that we finally found
            [02:10.00] (Instrumental Outro)
        """.trimIndent(),

        "2" to """
            [00:04.20] Silk ribbons floating softly on the bamboo breeze
            [00:12.80] Whispers of Kyoto hidden through the cherry trees
            [00:22.10] Shadows of the lantern dancing by the shrine
            [00:31.50] Counting every second across the streams of time
            [00:41.00] A graceful motion beneath the paper umbrella
            [00:50.40] Tales of thousand seasons that no voice can tell ya
            [01:02.15] Resonating shamisen strings in midnight rain
            [01:12.60] Washing away the sorrow, easing every pain
            [01:23.40] Walk along the river where the koi fish glide
            [01:34.20] Secrets of the sacred mountain by our side
            [01:45.00] (Traditional strings & ambient groove)
        """.trimIndent(),

        "3" to """
            [00:05.00] Rushing waters cascading from the mountain crest
            [00:14.20] In the mist of jade and emerald we find our rest
            [00:24.00] Falling, crystal torrents glistening in the sun
            [00:34.80] Merging with the ocean where the rivers run
            [00:46.00] Listen to the thunderous roaring of the falls
            [00:56.50] Ancient spirit calling through the canyon walls
            [01:08.20] Let the cold droplets wash across your soul
            [01:19.40] In this wild expanse we are alive and whole
            [01:31.00] (Ambient mist cascade synth sequence)
        """.trimIndent(),

        "4" to """
            [00:06.00] Ground control to tower, altitude on track
            [00:15.50] Cleared for departure, no turning back
            [00:25.80] Radar pulses sweeping through the midnight clouds
            [00:36.20] Flying far above the cities and the busy crowds
            [00:47.00] Transponder blinking green at thirty thousand feet
            [00:57.60] Synced up with the synthesizer 4/4 beat
            [01:08.40] Heading out for rendezvous beyond the stratosphere
            [01:19.20] Air traffic control, our trajectory is crystal clear
        """.trimIndent(),

        "5" to """
            [00:05.10] Dim lights glowing on the Rue Saint-Germain
            [00:14.40] Cafe corner melodies wash away the rain
            [00:24.80] Seductive saxophone sliding through the smoke
            [00:35.20] Every blues note is a word that someone spoke
            [00:46.00] Walking along the Seine under moonlit bridges
            [00:56.80] Parisian romance etched on ancient ridges
            [01:08.00] Midnight jazz in Paris, forever in my mind
            [01:19.50] The sweetest retro romance you could ever find
        """.trimIndent(),

        "6" to """
            [00:06.00] Submerging deep into the sapphire blue
            [00:16.80] Floating where the sunlight barely makes it through
            [00:27.50] Bioluminescent coral lighting up the floor
            [00:38.20] Wonders in the depths you've never seen before
            [00:49.00] Pressure fades away, zero gravity embrace
            [01:00.50] Finding peaceful sanctuary in this underwater space
            [01:12.00] Resonance of whale song echoing far and wide
            [01:24.00] Riding on the current of the deep ocean tide
        """.trimIndent(),

        "7" to """
            [00:04.50] Chrome reflection on the dashboard glass
            [00:13.20] Outrun cruisers as the speed limits pass
            [00:23.00] Synthwave bassline driving at one hundred ten
            [00:33.40] Chasing the horizon till the dawn breaks again
            [00:44.20] Purple neon towers rising in the grid
            [00:54.80] Living out the future that the ancients hid
            [01:06.00] Laser city memories burning bright and high
            [01:17.50] Cruising towards tomorrow under cyber sky
        """.trimIndent(),

        "8" to """
            [00:05.40] Golden hour shining on the boulevard
            [00:15.00] Palm tree shadows falling on the boulevard
            [00:25.20] Warm breeze rolling from the Pacific shore
            [00:36.00] Turn the volume up, baby give me more
            [00:47.10] Cruising down the coast with the top laid low
            [00:57.80] Vintage tape cassette playing smooth and slow
            [01:09.00] Sunset Boulevard where dreams come alive
            [01:20.40] Nothing beats the feeling of this evening drive
        """.trimIndent(),

        "9" to """
            [00:06.50] Engines igniting with an ion blast
            [00:16.20] Leaving terrestrial orbit in our past
            [00:27.00] Warp drive engaged towards the Andromeda gate
            [00:38.50] Steering by the constellations, masters of our fate
            [00:50.00] Stardust swirling on the observation bay
            [01:01.80] Ten million light years feels like yesterday
            [01:13.50] Cosmic voyage charting pathways through the dark
            [01:25.00] Guided by eternity's celestial spark
        """.trimIndent()
    )
}
