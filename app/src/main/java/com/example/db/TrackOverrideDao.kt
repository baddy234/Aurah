package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackOverrideDao {
    @Query("SELECT * FROM track_overrides")
    fun getAllOverrides(): Flow<List<TrackOverride>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverride(override: TrackOverride)

    @Query("DELETE FROM track_overrides WHERE trackId = :trackId")
    suspend fun deleteOverride(trackId: String)
}
