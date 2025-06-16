package oncontroldoctor.upc.edu.pe.authentication.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit

class SessionManager(context: Context){
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "onco_session_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(id: Long, username: String, token: String){
        sharedPreferences.edit().apply{
            putLong(KEY_USER_ID, id)
            putString(KEY_USERNAME, username)
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun saveUuid(uuid: String){
        sharedPreferences.edit().apply{
            putString(KEY_UUID, uuid)
            apply()
        }
    }

    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)
    fun getUserId(): Long = sharedPreferences.getLong(KEY_USER_ID, -1L)
    fun getUuid(): String? = sharedPreferences.getString(KEY_UUID, null)
    fun getUsername(): String? = sharedPreferences.getString(KEY_USERNAME, null)
    fun clearSession(){
        sharedPreferences.edit { clear() }
    }

    companion object{
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
        private const val KEY_UUID = "uuid"
    }
}