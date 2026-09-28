package com.example.db

import kotlinx.coroutines.flow.Flow

class TrackOverrideRepository(private val dao: TrackOverrideDao) {
    val allOverrides: Flow<List<TrackOverride>> = dao.getAllOverrides()

    suspend fun insertOverride(override: TrackOverride) = dao.insertOverride(override)
    suspend fun deleteOverride(trackId: String) = dao.deleteOverride(trackId)
}
