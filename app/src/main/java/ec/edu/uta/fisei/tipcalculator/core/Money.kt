package ec.edu.uta.fisei.tipcalculator.core

import java.math.BigDecimal
import java.math.RoundingMode

/** Escala y modo de redondeo unicos para todos los montos de la app. */
internal const val MONEY_SCALE = 2
internal val MONEY_ROUNDING: RoundingMode = RoundingMode.HALF_UP
internal val HUNDRED: BigDecimal = BigDecimal(100)

/** Normaliza cualquier BigDecimal a la representacion monetaria de la app. */
internal fun BigDecimal.toMoney(): BigDecimal = setScale(MONEY_SCALE, MONEY_ROUNDING)