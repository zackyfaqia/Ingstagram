package com.zackyfaqia.ingstagram.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference constructor(private val dataStore: DataStore<Preferences>) {
    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ID] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[STATE_KEY] ?: false,
                token = preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID] = user.id
            preferences[NAME_KEY] = user.name
            preferences[STATE_KEY] = user.isLogin
            preferences[TOKEN_KEY] = user.token
        }
    }

    suspend fun login(token: String) {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = false
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID = stringPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val STATE_KEY = booleanPreferencesKey("state")
        val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}