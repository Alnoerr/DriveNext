package dev.alnoer.drivenext

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dev.alnoer.drivenext.core.network.NetworkMonitor

class MainActivity : AppCompatActivity() {
    private val networkMonitor = NetworkMonitor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        fixInsets()
    }

    private fun fixInsets() {
        val navHost = findViewById<View>(R.id.nav_host_fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(navHost) { view, insets ->
            val safeArea = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(
                left = safeArea.left,
                top = safeArea.top,
                right = safeArea.right,
                bottom = safeArea.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(navHost)
    }
}
