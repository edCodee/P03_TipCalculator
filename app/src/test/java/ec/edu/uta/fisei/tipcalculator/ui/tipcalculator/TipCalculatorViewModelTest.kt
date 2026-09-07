package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class TipCalculatorViewModelTest {

    private val viewModel = TipCalculatorViewModel()

    @Test
    fun `el estado inicial es cero al 15 por ciento`() {
        val s = viewModel.state

        assertEquals(BigDecimal("0.00"), s.billAmount)
        assertEquals(15, s.tipPercent)
        assertEquals(BigDecimal("0.00"), s.tip)
        assertEquals(BigDecimal("0.00"), s.total)
    }

    @Test
    fun `tecleando 5632 calcula con el 15 por ciento inicial`() {
        viewModel.onAmountChanged("5632")

        assertEquals(BigDecimal("56.32"), viewModel.state.billAmount)
        assertEquals(BigDecimal("8.45"), viewModel.state.tip)
        assertEquals(BigDecimal("64.77"), viewModel.state.total)
    }

    @Test
    fun `el escenario completo del PDF - 5632 y luego 25 por ciento`() {
        viewModel.onAmountChanged("5632")
        viewModel.onPercentChanged(25)

        assertEquals(BigDecimal("14.08"), viewModel.state.tip)
        assertEquals(BigDecimal("70.40"), viewModel.state.total)
    }

    @Test
    fun `el porcentaje se conserva al cambiar el monto`() {
        viewModel.onPercentChanged(30)
        viewModel.onAmountChanged("1000")

        assertEquals(30, viewModel.state.tipPercent)
        assertEquals(BigDecimal("3.00"), viewModel.state.tip)
    }

    @Test
    fun `borrar todos los digitos vuelve a cero sin perder el porcentaje`() {
        viewModel.onAmountChanged("5632")
        viewModel.onPercentChanged(25)
        viewModel.onAmountChanged("")

        assertEquals(BigDecimal("0.00"), viewModel.state.total)
        assertEquals(25, viewModel.state.tipPercent)
    }

    @Test
    fun `un porcentaje fuera de rango se acota en vez de reventar`() {
        viewModel.onPercentChanged(99)
        assertEquals(30, viewModel.state.tipPercent)

        viewModel.onPercentChanged(-5)
        assertEquals(0, viewModel.state.tipPercent)
    }
}