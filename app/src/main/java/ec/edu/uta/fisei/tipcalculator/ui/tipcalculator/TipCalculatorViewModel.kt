package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import androidx.lifecycle.ViewModel
import ec.edu.uta.fisei.tipcalculator.domain.model.TipCalculation
import ec.edu.uta.fisei.tipcalculator.domain.usecase.CalculateTipUseCase
import ec.edu.uta.fisei.tipcalculator.domain.usecase.ParseBillAmountUseCase
import java.math.BigDecimal

/**
 * Guarda el estado de la pantalla y orquesta los casos de uso.
 *
 * Sobrevive a la recreacion de la Activity (rotacion, cambio de tema o de
 * idioma), a diferencia de los campos de instancia que usa el PDF.
 */
class TipCalculatorViewModel(
    private val parseBillAmount: ParseBillAmountUseCase = ParseBillAmountUseCase(),
    private val calculateTip: CalculateTipUseCase = CalculateTipUseCase()
) : ViewModel() {

    private var billAmount: BigDecimal = BigDecimal.ZERO
    private var tipPercent: Int = PORCENTAJE_INICIAL

    var state: TipCalculation = calculateTip(billAmount, tipPercent)
        private set

    fun onAmountChanged(digits: String) {
        billAmount = parseBillAmount(digits)
        recalcular()
    }

    fun onPercentChanged(percent: Int) {
        tipPercent = percent.coerceIn(
            CalculateTipUseCase.PORCENTAJE_MINIMO,
            CalculateTipUseCase.PORCENTAJE_MAXIMO
        )
        recalcular()
    }

    private fun recalcular() {
        state = calculateTip(billAmount, tipPercent)
    }

    companion object {
        const val PORCENTAJE_INICIAL = 15
    }
}