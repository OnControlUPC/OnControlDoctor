package oncontroldoctor.upc.edu.pe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionManager
import oncontroldoctor.upc.edu.pe.shared.presentation.navigation.OncoAppNav
import oncontroldoctor.upc.edu.pe.ui.theme.OnControlDoctorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnControlDoctorTheme(dynamicColor = false) {
                val sessionManager = remember { SessionManager(applicationContext) }
                val hasToken = sessionManager.getToken() != null
                OncoAppNav(startDestination = hasToken)
            }
        }
    }
}