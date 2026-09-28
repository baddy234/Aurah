package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_overrides")
data class TrackOverride(
    @PrimaryKey val trackId: String,
    val title: String,
    val artist: String,
    val lyrics: String? = null
)
