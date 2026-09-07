package ec.edu.uta.fisei.tipcalculator.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CalculateTipUseCaseTest {

    private val calcular = CalculateTipUseCase()

    // --- Escenario exacto del PDF, Fig. 3.1(b) ---

    @Test
    fun `56_32 al 25 por ciento da propina 14_08 y total 70_40`() {
        val r = calcular(BigDecimal("56.32"), 25)

        assertEquals(BigDecimal("14.08"), r.tip)
        assertEquals(BigDecimal("70.40"), r.total)
    }

    @Test
    fun `56_32 al 15 por ciento por defecto da propina 8_45 y total 64_77`() {
        val r = calcular(BigDecimal("56.32"), 15)

        assertEquals(BigDecimal("8.45"), r.tip)
        assertEquals(BigDecimal("64.77"), r.total)
    }

    // --- Límites del rango que permite el SeekBar (0 a 30) ---

    @Test
    fun `al 0 por ciento la propina es cero y el total es el monto`() {
        val r = calcular(BigDecimal("56.32"), 0)

        assertEquals(BigDecimal("0.00"), r.tip)
        assertEquals(BigDecimal("56.32"), r.total)
    }

    @Test
    fun `al 30 por ciento maximo da propina 16_90 y total 73_22`() {
        val r = calcular(BigDecimal("56.32"), 30)

        assertEquals(BigDecimal("16.90"), r.tip)
        assertEquals(BigDecimal("73.22"), r.total)
    }

    @Test
    fun `monto cero da propina y total en cero`() {
        val r = calcular(BigDecimal("0.00"), 15)

        assertEquals(BigDecimal("0.00"), r.tip)
        assertEquals(BigDecimal("0.00"), r.total)
    }

    // --- Redondeo ---

    @Test
    fun `redondea hacia arriba cuando el tercer decimal es 5`() {
        // 3.70 x 15% = 0.555 exacto -> HALF_UP -> 0.56
        val r = calcular(BigDecimal("3.70"), 15)

        assertEquals(BigDecimal("0.56"), r.tip)
        assertEquals(BigDecimal("4.26"), r.total)
    }

    @Test
    fun `el resultado siempre tiene exactamente dos decimales`() {
        val r = calcular(BigDecimal("10"), 15)

        assertEquals(2, r.tip.scale())
        assertEquals(2, r.total.scale())
    }

    @Test
    fun `el total siempre es igual al monto mas la propina`() {
        for (porcentaje in 0..30) {
            val r = calcular(BigDecimal("56.32"), porcentaje)
            assertEquals(
                "Fallo la invariante con $porcentaje%",
                r.total,
                BigDecimal("56.32") + r.tip
            )
        }
    }

    // --- Entradas invalidas ---

    @Test(expected = IllegalArgumentException::class)
    fun `rechaza porcentajes negativos`() {
        calcular(BigDecimal("56.32"), -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rechaza porcentajes mayores a 30`() {
        calcular(BigDecimal("56.32"), 31)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rechaza montos negativos`() {
        calcular(BigDecimal("-1.00"), 15)
    }
}