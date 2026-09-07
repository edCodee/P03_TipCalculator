package ec.edu.uta.fisei.tipcalculator.domain.usecase

import ec.edu.uta.fisei.tipcalculator.core.HUNDRED
import ec.edu.uta.fisei.tipcalculator.core.toMoney
import ec.edu.uta.fisei.tipcalculator.domain.model.TipCalculation
import java.math.BigDecimal

/**
 * Calcula propina y total a partir de un monto y un porcentaje entero.
 *
 * El total se deriva de la propina YA redondeada, de modo que los tres
 * valores mostrados en pantalla siempre sumen exactamente.
 */
class CalculateTipUseCase {

    operator fun invoke(billAmount: BigDecimal, tipPercent: Int): TipCalculation {
        require(tipPercent in PORCENTAJE_MINIMO..PORCENTAJE_MAXIMO) {
            "El porcentaje debe estar entre $PORCENTAJE_MINIMO y $PORCENTAJE_MAXIMO, fue $tipPercent"
        }
        require(billAmount.signum() >= 0) {
            "El monto no puede ser negativo, fue $billAmount"
        }

        val monto = billAmount.toMoney()
        val propina = (monto * tipPercent.toBigDecimal()).divide(HUNDRED).toMoney()
        val total = (monto + propina).toMoney()

        return TipCalculation(
            billAmount = monto,
            tipPercent = tipPercent,
            tip = propina,
            total = total
        )
    }

    companion object {
        const val PORCENTAJE_MINIMO = 0
        const val PORCENTAJE_MAXIMO = 30
    }
}