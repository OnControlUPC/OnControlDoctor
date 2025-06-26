package oncontroldoctor.upc.edu.pe

import android.app.Application
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder

class OnControlDoctorApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        SessionHolder.init(this)
    }
}