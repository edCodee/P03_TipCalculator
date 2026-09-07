package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import ec.edu.uta.fisei.tipcalculator.databinding.ActivityMainBinding
import ec.edu.uta.fisei.tipcalculator.domain.usecase.CalculateTipUseCase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TipCalculatorViewModel by viewModels()
    private val formatter = MoneyFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        aplicarInsets()
        configurarSeekBar()
        configurarEditText()
        render()
    }

    /**
     * El rango del SeekBar sale del dominio, no de un numero suelto en el XML.
     * Asi es imposible que el widget y la regla de negocio se desincronicen.
     */
    private fun configurarSeekBar() {
        binding.percentSeekBar.max = CalculateTipUseCase.PORCENTAJE_MAXIMO
        binding.percentSeekBar.progress = viewModel.state.tipPercent

        binding.percentSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    viewModel.onPercentChanged(progress)
                    render()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            }
        )
    }

    private fun configurarEditText() {
        binding.amountEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onAmountChanged(text?.toString().orEmpty())
            render()
        }
    }

    /** Vuelca el estado del ViewModel en las vistas. Sin calculos aqui. */
    private fun render() {
        val estado = viewModel.state

        // Vacio hace que reaparezca el hint "Enter Amount" (PDF 3.2)
        binding.amountTextView.text =
            if (estado.billAmount.signum() == 0) "" else formatter.money(estado.billAmount)

        binding.percentTextView.text = formatter.percent(estado.tipPercent)
        binding.tipTextView.text = formatter.money(estado.tip)
        binding.totalTextView.text = formatter.money(estado.total)
    }

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