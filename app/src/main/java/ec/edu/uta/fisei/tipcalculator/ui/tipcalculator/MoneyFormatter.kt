package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import ec.edu.uta.fisei.tipcalculator.core.HUNDRED
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Formatea montos y porcentajes segun el locale del dispositivo (PDF 3.3.6).
 *
 * El locale se recibe por parametro en vez de leerse dentro: asi los tests
 * son deterministas y no dependen de la maquina que los ejecuta.
 */
class MoneyFormatter(locale: Locale = Locale.getDefault()) {

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(locale)
    private val percentFormat: NumberFormat = NumberFormat.getPercentInstance(locale)

    fun money(amount: BigDecimal): String = currencyFormat.format(amount)

    fun percent(tipPercent: Int): String =
        percentFormat.format(tipPercent.toBigDecimal().divide(HUNDRED))
}