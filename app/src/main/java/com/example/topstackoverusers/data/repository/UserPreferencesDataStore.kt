package com.example.topstackoverusers.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserPreferencesRepository {
    val followState: Flow<Map<Int, Boolean>>
    suspend fun setFollowed(userId: Int, isFollowed: Boolean)
}

class UserPreferencesDataStore(private val dataStore: DataStore<Preferences>) : UserPreferencesRepository{

    override val followState: Flow<Map<Int, Boolean>> = dataStore.data
        .map { prefs ->
            prefs.asMap().mapNotNull { (key, value) ->
                if (key.name.startsWith("followed_") && value is Boolean) {
                    key.name.removePrefix("followed_").toIntOrNull()?.let { it to value }
                } else null
            }.toMap()
        }

    override suspend fun setFollowed(userId: Int, isFollowed: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey("followed_$userId")] = isFollowed
        }
    }
}