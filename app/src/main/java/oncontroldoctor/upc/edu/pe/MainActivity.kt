package oncontroldoctor.upc.edu.pe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import oncontroldoctor.upc.edu.pe.shared.presentation.navigation.OncoAppNav
import oncontroldoctor.upc.edu.pe.shared.presentation.ui.theme.OnControlDoctorTheme

class MainActivity : ComponentActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("363072497868-mo6fs090473b1nc2nlr36c2bc7ic9ugg.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //enableEdgeToEdge()
        setContent {
            OnControlDoctorTheme(dynamicColor = false) {
                OncoAppNav()
            }
        }
    }
}