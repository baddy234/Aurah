package com.example

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

object CoverImageProvider {
    // High quality curated album covers for genres, aesthetics, and vibes
    private val CURATED_ALBUM_COVERS = listOf(
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80", // Concert lights
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80", // Vinyl record
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80", // DJ turntable
        "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80", // Neon audio wave
        "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80", // Microphone stage
        "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80", // Cassette tape
        "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80", // Acoustic guitar
        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80", // Festival crowd
        "https://images.unsplash.com/photo-1520523839898-507127053e14?w=600&auto=format&fit=crop&q=80", // Piano keys
        "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80", // Studio headphones
        "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?w=600&auto=format&fit=crop&q=80", // Electric synth
        "https://images.unsplash.com/photo-1445985543469-433ecba62447?w=600&auto=format&fit=crop&q=80", // Saxophone jazz
        "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=600&auto=format&fit=crop&q=80", // Listening music
        "https://images.unsplash.com/photo-1485579149621-3123dd979885?w=600&auto=format&fit=crop&q=80", // Retro speaker
        "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80", // Club atmosphere
        "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80"  // Neon cyber aesthetic
    )

    // Curated high quality cinematic 16:9 video template covers
    private val CURATED_VIDEO_COVERS = listOf(
        "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800&auto=format&fit=crop&q=80", // Cinematic film production
        "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&auto=format&fit=crop&q=80", // Video editing monitor
        "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&auto=format&fit=crop&q=80", // Lens flare camera
        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80", // Neon cinematic cyber city
        "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=800&auto=format&fit=crop&q=80", // Live concert performance stage
        "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?w=800&auto=format&fit=crop&q=80", // Studio lighting beam
        "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&auto=format&fit=crop&q=80", // Movie theater cinema
        "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=800&auto=format&fit=crop&q=80", // Golden hour landscape
        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80", // Anime anime futuristic aesthetic
        "https://images.unsplash.com/photo-1506157786151-b8491531f063?w=800&auto=format&fit=crop&q=80", // Music festival laser show
        "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=800&auto=format&fit=crop&q=80", // Abstract dark cosmic motion
        "https://images.unsplash.com/photo-1524712245354-2c4e5e7121c0?w=800&auto=format&fit=crop&q=80"  // Drone mountain vista
    )

    // Curated high quality artist portraits
    private val CURATED_ARTIST_PORTRAITS = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1528892952291-009c663ce843?w=500&auto=format&fit=crop&q=80"
    )

    fun getFallbackCover(title: String, artist: String): String {
        val key = "$title-$artist"
        val index = abs(key.hashCode()) % CURATED_ALBUM_COVERS.size
        return CURATED_ALBUM_COVERS[index]
    }

    fun getVideoFallbackCover(title: String, artist: String = ""): String {
        val key = "$title-$artist-video"
        val index = abs(key.hashCode()) % CURATED_VIDEO_COVERS.size
        return CURATED_VIDEO_COVERS[index]
    }

    fun getArtistCover(artistName: String, firstTrackArtwork: String? = null): String {
        if (!firstTrackArtwork.isNullOrBlank() && !firstTrackArtwork.startsWith("http://") && !firstTrackArtwork.contains("placeholder")) {
            return firstTrackArtwork
        }
        val index = abs(artistName.trim().lowercase().hashCode()) % CURATED_ARTIST_PORTRAITS.size
        return CURATED_ARTIST_PORTRAITS[index]
    }

    /**
     * Attempts to extract embedded album artwork (ID3 APIC frame or video thumbnail)
     * and save it to app cache for instant, reliable loading.
     */
    fun extractEmbeddedArtwork(context: Context, uri: Uri, id: String, isVideo: Boolean): String? {
        val cacheFile = File(context.cacheDir, "art_${id}.jpg")
        if (cacheFile.exists() && cacheFile.length() > 0) {
            return Uri.fromFile(cacheFile).toString()
        }
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(context, uri)
            val pictureBytes = mmr.embeddedPicture
            if (pictureBytes != null && pictureBytes.isNotEmpty()) {
                FileOutputStream(cacheFile).use { out ->
                    out.write(pictureBytes)
                }
                Uri.fromFile(cacheFile).toString()
            } else if (isVideo) {
                val frame = mmr.getFrameAtTime(1000000) // 1 second in
                if (frame != null) {
                    FileOutputStream(cacheFile).use { out ->
                        frame.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    Uri.fromFile(cacheFile).toString()
                } else null
            } else null
        } catch (_: Exception) {
            null
        } finally {
            try { mmr.release() } catch (_: Exception) {}
        }
    }
}
