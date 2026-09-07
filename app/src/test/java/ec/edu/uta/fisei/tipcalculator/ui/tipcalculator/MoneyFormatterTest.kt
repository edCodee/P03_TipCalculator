package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

class MoneyFormatterTest {

    private val us = MoneyFormatter(Locale.US)

    @Test
    fun `formatea moneda en locale US`() {
        assertEquals("$56.32", us.money(BigDecimal("56.32")))
    }

    @Test
    fun `formatea cero en locale US`() {
        assertEquals("$0.00", us.money(BigDecimal("0.00")))
    }

    @Test
    fun `formatea porcentaje entero en locale US`() {
        assertEquals("15%", us.percent(15))
    }

    @Test
    fun `formatea los extremos del rango`() {
        assertEquals("0%", us.percent(0))
        assertEquals("30%", us.percent(30))
    }

    @Test
    fun `distintos locales producen formatos distintos`() {
        val alemania = MoneyFormatter(Locale.GERMANY)
        assertNotEquals(us.money(BigDecimal("56.32")), alemania.money(BigDecimal("56.32")))
    }
}