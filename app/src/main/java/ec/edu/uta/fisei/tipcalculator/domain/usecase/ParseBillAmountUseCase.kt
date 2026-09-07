package ec.edu.uta.fisei.tipcalculator.domain.usecase

import ec.edu.uta.fisei.tipcalculator.core.HUNDRED
import ec.edu.uta.fisei.tipcalculator.core.toMoney
import java.math.BigDecimal

/**
 * Convierte la cadena de digitos del EditText en un monto.
 *
 * El usuario teclea el monto como numero entero de centavos (PDF 3.2):
 * "5632" representa 56.32. Cualquier entrada invalida o vacia da 0.00,
 * que es el comportamiento del bloque catch del PDF (lineas 100-103).
 */
class ParseBillAmountUseCase {

    operator fun invoke(digits: String): BigDecimal {
        if (digits.isEmpty() || !digits.all { it.isDigit() }) {
            return BigDecimal.ZERO.toMoney()
        }
        return BigDecimal(digits).divide(HUNDRED).toMoney()
    }
}