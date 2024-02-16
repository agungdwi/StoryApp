package com.example.myapplication.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")
class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val token = stringPreferencesKey("Token")
    private val uid = stringPreferencesKey("UIDwh")
    private val name = stringPreferencesKey("Name")
    private val email = stringPreferencesKey("Email")

    fun getUserToken(): Flow<String> = dataStore.data.map { it[token] ?: "token" }

    suspend fun saveUserPref(tokenUser:String, uidUser:String,nameUser:String, emailUser:String){
        dataStore.edit { preferences ->
            preferences[token] = tokenUser
            preferences[uid] = uidUser
            preferences[name] = nameUser
            preferences[email] = emailUser

        }
    }

    suspend fun clearUserPref() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}