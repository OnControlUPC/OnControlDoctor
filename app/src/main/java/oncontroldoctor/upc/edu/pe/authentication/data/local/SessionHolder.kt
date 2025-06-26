package oncontroldoctor.upc.edu.pe.authentication.data.local

import android.content.Context

object SessionHolder {

    private var sessionManager: SessionManager? = null

    fun init(context: Context){
        if(sessionManager == null){
            sessionManager = SessionManager(context.applicationContext)
        }
    }

    fun getToken(): String? = sessionManager?.getToken();
    fun getUserId(): Long? = sessionManager?.getUserId();
    fun getUserUuid(): String? = sessionManager?.getUuid();
    fun getUsername(): String? = sessionManager?.getUsername();
    fun getSubscriptionActId(): Long? {
        val id = sessionManager?.getSubscriptionActId() ?: -1
        return if (id > 0) id else null
    }
    fun getPlanId(): Long? {
        val id = sessionManager?.getPlanId() ?: -1
        return if (id > 0) id else null
    }


    fun saveSession(id: Long, username: String, token: String) {
        sessionManager?.saveSession(id, username, token)
    }

    fun saveSubscriptionActId(subscriptionActId: Long) {
        sessionManager?.saveSubscriptionActId(subscriptionActId)
    }
    fun savePlanId(planId: Long) {
        sessionManager?.savePlanId(planId)
    }

    fun saveUuid(uuid: String) {
        sessionManager?.saveUuid(uuid)
    }

    fun clearSession() {
        sessionManager?.clearSession()
    }
}