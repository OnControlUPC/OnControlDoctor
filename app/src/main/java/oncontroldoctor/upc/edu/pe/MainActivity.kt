package oncontroldoctor.upc.edu.pe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import oncontroldoctor.upc.edu.pe.presentation.navigation.Home
import oncontroldoctor.upc.edu.pe.ui.theme.OnControlDoctorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnControlDoctorTheme {
                Home()
            }
        }
    }
}
