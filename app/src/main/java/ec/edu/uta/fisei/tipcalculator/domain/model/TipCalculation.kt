package ec.edu.uta.fisei.tipcalculator.domain.model

import java.math.BigDecimal

/** Resultado inmutable de un calculo de propina. Sin dependencias de Android. */
data class TipCalculation(
    val billAmount: BigDecimal,
    val tipPercent: Int,
    val tip: BigDecimal,
    val total: BigDecimal
)