package ec.edu.uta.fisei.tipcalculator.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class ParseBillAmountUseCaseTest {

    private val parsear = ParseBillAmountUseCase()

    // La secuencia exacta que describe el PDF en la seccion 3.2:
    // al teclear 5, 6, 3, 2 el monto pasa por 0.05, 0.56, 5.63 y 56.32

    @Test
    fun `un digito se interpreta como centavos`() {
        assertEquals(BigDecimal("0.05"), parsear("5"))
    }

    @Test
    fun `dos digitos`() {
        assertEquals(BigDecimal("0.56"), parsear("56"))
    }

    @Test
    fun `tres digitos`() {
        assertEquals(BigDecimal("5.63"), parsear("563"))
    }

    @Test
    fun `cuatro digitos`() {
        assertEquals(BigDecimal("56.32"), parsear("5632"))
    }

    @Test
    fun `seis digitos es el monto maximo que permite la UI`() {
        assertEquals(BigDecimal("9999.99"), parsear("999999"))
    }

    @Test
    fun `entrada vacia devuelve cero`() {
        assertEquals(BigDecimal("0.00"), parsear(""))
    }

    @Test
    fun `entrada no numerica devuelve cero`() {
        assertEquals(BigDecimal("0.00"), parsear("abc"))
    }

    @Test
    fun `ceros a la izquierda no alteran el valor`() {
        assertEquals(BigDecimal("0.05"), parsear("0005"))
    }
}