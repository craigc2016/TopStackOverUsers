package com.example.topstackoverusers.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserPreferencesRepository {
    val followState: Flow<Set<Int>>
    suspend fun setFollowed(followState: FollowState)
}

class UserPreferencesDataStore(private val dataStore: DataStore<Preferences>) : UserPreferencesRepository{

    override val followState: Flow<Set<Int>> =
        dataStore.data.map { prefs ->
            prefs.asMap()
                .filter { it.key.name.startsWith("followed_") && it.value == true }
                .mapNotNull { it.key.name.removePrefix("followed_").toIntOrNull() }
                .toSet()
        }


    override suspend fun setFollowed(followState: FollowState) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey("followed_${followState.userId}")] = followState.isFollowed
        }
    }
}