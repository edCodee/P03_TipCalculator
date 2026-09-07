package ec.edu.uta.fisei.tipcalculator.ui.tipcalculator

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ec.edu.uta.fisei.tipcalculator.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val formatter = MoneyFormatter()

    private fun money(valor: String) = formatter.money(BigDecimal(valor))

    @Before
    fun abrirPantalla() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun alIniciar_muestraCerosYQuincePorCiento() {
        onView(withId(R.id.tipTextView)).check(matches(withText(money("0.00"))))
        onView(withId(R.id.totalTextView)).check(matches(withText(money("0.00"))))
        onView(withId(R.id.percentTextView))
            .check(matches(withText(formatter.percent(15))))
    }

    @Test
    fun alTeclear5632_muestraMontoPropinaYTotalAlQuincePorCiento() {
        onView(withId(R.id.amountEditText))
            .perform(typeText("5632"), closeSoftKeyboard())

        onView(withId(R.id.amountTextView)).check(matches(withText(money("56.32"))))
        onView(withId(R.id.tipTextView)).check(matches(withText(money("8.45"))))
        onView(withId(R.id.totalTextView)).check(matches(withText(money("64.77"))))
    }

    @Test
    fun alBorrarTodo_vuelveACeros() {
        onView(withId(R.id.amountEditText))
            .perform(typeText("5632"), closeSoftKeyboard())
        onView(withId(R.id.amountEditText))
            .perform(clearText(), closeSoftKeyboard())

        onView(withId(R.id.tipTextView)).check(matches(withText(money("0.00"))))
        onView(withId(R.id.totalTextView)).check(matches(withText(money("0.00"))))
    }
    @Test
    fun escenarioDelPdf_5632AlVeinticincoPorCiento() {
        onView(withId(R.id.amountEditText))
            .perform(typeText("5632"), closeSoftKeyboard())

        onView(withId(R.id.percentSeekBar)).perform(setSeekBarProgress(25))

        onView(withId(R.id.percentTextView))
            .check(matches(withText(formatter.percent(25))))
        onView(withId(R.id.tipTextView)).check(matches(withText(money("14.08"))))
        onView(withId(R.id.totalTextView)).check(matches(withText(money("70.40"))))
    }

    /**
     * Mueve el thumb de un SeekBar a un valor concreto.
     *
     * Se define aqui en vez de usar espresso-contrib porque SeekBarActions
     * cambio de ubicacion entre versiones de Espresso.
     */
    private fun setSeekBarProgress(progreso: Int) = object : ViewAction {

        override fun getConstraints(): Matcher<View> =
            isAssignableFrom(SeekBar::class.java)

        override fun getDescription() = "mover el SeekBar a $progreso"

        override fun perform(uiController: UiController, view: View) {
            (view as SeekBar).progress = progreso
            uiController.loopMainThreadUntilIdle()
        }
    }
}