package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import ec.edu.uta.fisei.tipcalculator.R
import ec.edu.uta.fisei.tipcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarInsets()
    }

    /**
     * Suma los insets del sistema (barras) y del teclado al padding base del
     * XML, en vez de reemplazarlo. El teclado se incluye porque esta app lo
     * mantiene visible de forma permanente (Fase 6).
     */
    private fun aplicarInsets() {
        val paddingBase = resources.getDimensionPixelSize(R.dimen.screen_padding)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.updatePadding(
                left = paddingBase + barras.left,
                top = paddingBase + barras.top,
                right = paddingBase + barras.right,
                bottom = paddingBase + barras.bottom
            )
            insets
        }
    }
}