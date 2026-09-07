package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import ec.edu.uta.fisei.tipcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // dark() = iconos claros, porque detras va el azul 700
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deja la toolbar lista para el menu de la Practica 4
        setSupportActionBar(binding.topAppBar)

        aplicarInsets()
    }

    /**
     * El inset superior va a la TOOLBAR, no a la raiz: asi su fondo azul se
     * extiende por detras de la barra de estado transparente, que es como se
     * reproduce hoy el colorPrimaryDark del PDF.
     *
     * Los laterales y el inferior (incluido el teclado) van a la raiz.
     */
    private fun aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { root, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            binding.topAppBar.updatePadding(top = barras.top)
            root.updatePadding(
                left = barras.left,
                right = barras.right,
                bottom = barras.bottom
            )
            insets
        }
    }
}