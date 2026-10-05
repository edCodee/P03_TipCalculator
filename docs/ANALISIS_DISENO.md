# Tip Calculator — Análisis de diseño (PDF Deitel 2016 → Kotlin / API 36)

Este documento complementa a [COMPATIBILIDAD.md](COMPATIBILIDAD.md):

- **COMPATIBILIDAD.md** responde _qué cambió porque Android cambió_.
- **Este documento** responde _qué cambió por diseño_: qué principios viola el código del PDF, cuáles aplica la migración, qué errores tiene el libro y qué no resuelve el proyecto.

> **Convenciones**
>
> - `PDF 3.6.4`, `Fig. 3.18 l. 97`: sección, figura y línea del libro (cap. 3).
> - Las rutas son relativas a `app/src/main/java/ec/edu/uta/fisei/tipcalculator/`.
> - `COMPAT #n` remite a una fila de COMPATIBILIDAD.md.
> - **Código congelado desde la fase 9:** lo que falla se documenta aquí y no se corrige.

---

## 1. Resumen

| Métrica                            | PDF          | Proyecto                                                            |
| ---------------------------------- | ------------ | ------------------------------------------------------------------- |
| Archivos de producción             | 1 clase Java | 7 archivos Kotlin en 3 capas con código (`core`, `domain`, `ui`)    |
| Lógica sin dependencias de Android | Ninguna      | `core` + `domain` + `MoneyFormatter` (solo `java.math`/`java.text`) |
| Tipo del dinero                    | `double`     | `BigDecimal`, escala 2, `HALF_UP`                                   |
| Pruebas unitarias                  | 0            | 30 en 4 clases, en verde                                            |
| Pruebas instrumentadas             | 0            | 4, en teléfono y tablet                                             |
| Lint                               | —            | 0 errores                                                           |

El código del PDF funciona, pero concentra en `MainActivity` la lectura del monto, el cálculo, el formato y la actualización de la pantalla; usa `double` para dinero y no se puede probar sin dispositivo. La migración extrae el cálculo a casos de uso puros, garantiza la suma de lo mostrado y cubre todo con pruebas.

---

## 2. ¿Se aplicó arquitectura limpia?

**Sí, en la medida en que el problema lo pide.**

| Capa                | Contenido                                               | ¿Justificada?                                            |
| ------------------- | ------------------------------------------------------- | -------------------------------------------------------- |
| `core`              | `Money.kt`: escala 2, `HALF_UP`, `HUNDRED`, `toMoney()` | Sí: una sola política monetaria para dominio y UI        |
| `domain/model`      | `TipCalculation` (inmutable)                            | Sí                                                       |
| `domain/usecase`    | `ParseBillAmountUseCase`, `CalculateTipUseCase`         | Sí: son las reglas del PDF, y ahora se prueban en la JVM |
| `domain/repository` | Vacía (`.gitkeep`)                                      | Correcto que esté vacía: no hay nada que persistir       |
| `data/repository`   | Vacía (`.gitkeep`)                                      | Igual                                                    |
| `ui`                | Activity, ViewModel, `MoneyFormatter`                   | Sí                                                       |

- Regla de dependencias: `ui → domain → core`, y `ui → core`. `domain` no importa `android.*` (verificable, sección 8).
- `MoneyFormatter` vive en `ui` y no en `domain`: el locale es presentación; el dominio devuelve números.
- Las carpetas vacías siguen la convención común del curso (§7 #11).

---

## 3. Malos olores del PDF (STUPID)

| Letra | Olor                   | ¿En el PDF?   | Corrección                                     |
| ----- | ---------------------- | ------------- | ---------------------------------------------- |
| **S** | Singleton              | **Sí (leve)** | `MoneyFormatter` por instancia                 |
| **T** | Tight coupling         | **Sí**        | Casos de uso sin vistas; ViewModel entre ambos |
| **U** | Untestability          | **Sí**        | 30 pruebas en la JVM                           |
| **P** | Premature optimization | No aplica     | —                                              |
| **I** | Indescriptive naming   | **Sí**        | `tipPercent: Int`, `TipCalculation`            |
| **D** | Duplication            | **Sí**        | Parcial (§7 #1)                                |

### S — Estado estático

`currencyFormat` y `percentFormat` son `private static final` (Fig. 3.14 ll. 20–23). Se crean una vez con el locale del momento en que se carga la clase. Si el usuario cambia el idioma mientras el proceso sigue vivo, la Activity se recrea pero los formateadores estáticos conservan el locale anterior.

**En el proyecto:** `MoneyFormatter` es un campo de instancia de `MainActivity`, así que cada recreación toma el locale vigente. Además, recibe el `Locale` por constructor para que las pruebas sean deterministas.

### T — Tight coupling

`calculate()` (Fig. 3.16) calcula **y** escribe en `percentTextView`, `tipTextView` y `totalTextView`. El cálculo no existe sin las vistas.

**En el proyecto:** `CalculateTipUseCase` devuelve un `TipCalculation`; la Activity solo lo vuelca con `render()`.

### U — Untestability

El cálculo está en un método privado de la Activity y la lectura del monto dentro de un `TextWatcher` anónimo (Fig. 3.18). No hay pruebas.

**En el proyecto:**

| Lógica                   | Clase                    | Cómo se prueba sin dispositivo         |
| ------------------------ | ------------------------ | -------------------------------------- |
| Leer centavos            | `ParseBillAmountUseCase` | Recibe `String`, devuelve `BigDecimal` |
| Propina, total, redondeo | `CalculateTipUseCase`    | Recibe `BigDecimal` e `Int`            |
| Formato por locale       | `MoneyFormatter`         | El `Locale` se inyecta                 |
| Estado de la pantalla    | `TipCalculatorViewModel` | Se instancia directamente              |

### I — Indescriptive naming

- `percent` guarda `0.15`, una **fracción**, no un porcentaje (Fig. 3.14 l. 26; Fig. 3.17 l. 78).
- `calculate()` también **muestra**.
- **En el proyecto:** `tipPercent: Int` guarda 15, y la división entre 100 ocurre dentro del caso de uso.

### D — Duplication

| Dato repetido | En el PDF                                                         | En el proyecto                                                         |
| ------------- | ----------------------------------------------------------------- | ---------------------------------------------------------------------- |
| 15 %          | `progress="15"` (XML), `percent = 0.15` (Java), `"15%"` (strings) | `PORCENTAJE_INICIAL`, pero el XML y `strings.xml` lo conservan (§7 #1) |
| 30 %          | Solo `max="30"` en el XML                                         | `PORCENTAJE_MAXIMO` en el dominio; el XML lo conserva (§7 #1)          |

---

## 4. Principios SOLID

| Principio | PDF                           | Proyecto                                          |
| --------- | ----------------------------- | ------------------------------------------------- |
| **S**     | Violado                       | Aplicado                                          |
| **O**     | Violado en redondeo y formato | Aplicado de forma ligera                          |
| **L**     | No aplica                     | No aplica                                         |
| **I**     | Impuesto por el framework     | Mitigado con `doOnTextChanged`                    |
| **D**     | Violado                       | Inyección por constructor con valores por defecto |

- **S:** en el PDF, `MainActivity` lee, convierte, calcula, formatea y muestra. En el proyecto, cada tarea tiene su clase:
  - leer los centavos → `ParseBillAmountUseCase`;
  - calcular → `CalculateTipUseCase`;
  - formatear → `MoneyFormatter`;
  - guardar el estado → `TipCalculatorViewModel`;
  - mostrar → `MainActivity`.
- **O:** la política monetaria (escala y redondeo) vive en un solo archivo, `core/Money.kt`. Cambiar a `HALF_EVEN` es una línea, sin tocar los casos de uso.
- **I:** `TextWatcher` obliga a implementar tres métodos y `OnSeekBarChangeListener` también (Figs. 3.17–3.18). El proyecto usa `doOnTextChanged`; el SeekBar conserva dos métodos `= Unit`.
- **D:** `TipCalculatorViewModel` recibe sus casos de uso por constructor, con valores por defecto. Así funciona la fábrica por defecto de `viewModels()` y las pruebas podrían sustituirlos. No hay interfaces porque no hay implementaciones alternativas (YAGNI).

---

## 5. Otros principios aplicados

| Principio                       | Evidencia                                                                                                                                      |
| ------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| **Dinero exacto**               | `BigDecimal`, como pide la Obs. 3.3 del propio PDF                                                                                             |
| **Invariante por construcción** | El total se calcula con la propina ya redondeada; `el total siempre es igual al monto mas la propina` lo prueba para 0–30 %                    |
| **Fallar rápido**               | `CalculateTipUseCase` rechaza porcentajes fuera de 0–30 y montos negativos con `require`                                                       |
| **Frontera tolerante**          | El ViewModel acota el porcentaje (`coerceIn`); `ParseBillAmountUseCase` convierte la entrada vacía o inválida en 0.00, como el `catch` del PDF |
| **Excepciones no son flujo**    | El PDF usa `NumberFormatException` para el caso vacío (Fig. 3.18 ll. 100–103); el proyecto valida antes de convertir                           |
| **Objetos inmutables**          | `TipCalculation` es un `data class` de solo lectura                                                                                            |
| **DRY**                         | `core/Money.kt` es la única fuente de la escala, el redondeo y el 100                                                                          |
| **KISS**                        | Estado como propiedad simple y `render()` manual, sin `StateFlow` (§7 #13)                                                                     |
| **YAGNI**                       | Sin repositorio, sin Hilt, sin fábrica de ViewModel                                                                                            |
| **Pruebas deterministas**       | `MoneyFormatter(Locale.US)` en unidad; en Espresso, el valor esperado se formatea con el mismo locale del dispositivo                          |

---

## 6. Errores e inconsistencias del PDF

### 6.1 Errores en el texto

| #   | Ubicación                   | Qué dice el PDF                                                                               | Lo correcto                                                                                                              |
| --- | --------------------------- | --------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| 1   | Fig. 3.13                   | `public class MainActivity extends Activity`, con el pie "subclase de Activity"               | 3.6.2 y la importación (Fig. 3.12 l. 6) dicen `AppCompatActivity`; `Activity` ni siquiera se importa                     |
| 2   | PDF 3.7.2 y 3.7.3           | `application` en "líneas 5–21" y `activity` en "líneas 10–20"                                 | En la Fig. 3.19 son las líneas 5–22 y 11–21                                                                              |
| 3   | PDF 3.7.3                   | `.MainActivity` equivale a `com.deitel.MainActivity`                                          | Equivale a `com.deitel.tipcalculator.MainActivity` (el paquete completo)                                                 |
| 4   | PDF 3.4.4, paso 1           | Al configurar el **EditText** dice que "el **TextView** abarca ambas columnas"                | Se refiere al EditText                                                                                                   |
| 5   | PDF 3.4.4, paso 4           | La fila del total es "para mostrar el monto de la **propina**"                                | Es para el total                                                                                                         |
| 6   | PDF 3.6.2                   | "Toda subclase de Activity **debe** sobrescribir `onCreate`"                                  | Es lo habitual, no una obligación del lenguaje ni del framework                                                          |
| 7   | Obs. 3.3 vs Figs. 3.14–3.18 | Recomienda `BigDecimal` para dinero                                                           | El propio código usa `double` (Fig. 3.14 l. 25; Fig. 3.16 ll. 63–64; Fig. 3.18 l. 97)                                    |
| 8   | Nota del teclado (3.4.4)    | Si se cierra el teclado, "habría que salir de la app y volver" porque el EditText está oculto | El TextView de encima no es clicable, así que el toque pasa al EditText y el teclado reaparece. Comprobación: PRUEBAS §6 |
| 9   | PDF 3.5 y 3.5.1             | `styles.xml` y luego "`style.xml`"; "in the in the app's"                                     | `styles.xml`; errata                                                                                                     |
| 10  | Varias                      | "Seekbar"/"SeekBar"; "The “dark” has" (3.3.9); "features apps running" (3.6.1)                | Erratas                                                                                                                  |

### 6.2 Errores en el código

Solo los que **no** están en COMPATIBILIDAD.md.

| #   | Ubicación              | Problema                                                                               | Efecto                                                                        | En el proyecto                                                                                                             |
| --- | ---------------------- | -------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| A   | Figs. 3.14, 3.16, 3.18 | Dinero en `double`                                                                     | Errores de representación binaria (0.1 no es exacto)                          | `BigDecimal` en todo el dominio                                                                                            |
| B   | Fig. 3.16 ll. 63–68    | Propina y total se calculan sin redondear y se redondean **por separado** al formatear | No se garantiza que monto + propina mostrados sumen el total mostrado         | Total = monto + propina redondeada. Prueba: `el total siempre es igual al monto mas la propina`                            |
| C   | Fig. 3.16              | El redondeo lo decide implícitamente `NumberFormat` (`HALF_EVEN` por defecto)          | Regla de redondeo oculta                                                      | `HALF_UP` explícito en `core/Money.kt`. Prueba: `redondea hacia arriba cuando el tercer decimal es 5` (3.70 × 15 % = 0.56) |
| D   | Fig. 3.14 ll. 20–23    | `NumberFormat` estático                                                                | Locale desactualizado tras cambiar el idioma sin reiniciar el proceso (§3, S) | `MoneyFormatter` por instancia                                                                                             |
| E   | Fig. 3.18 ll. 96–103   | `try/catch NumberFormatException` como validación                                      | Control de flujo con excepciones                                              | `ParseBillAmountUseCase` valida con `isEmpty()` y `all { isDigit() }`                                                      |

---

## 7. Autocrítica: deuda técnica conocida

| #   | Hallazgo                                                                                                                                                       | Riesgo                                                                                                                                                                                             | Acción propuesta                                                  | Estado                                                   |
| --- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- | -------------------------------------------------------- |
| 1   | El 15 y el 30 siguen en el XML (`progress="15"`, `max="30"`) y "15%" es un recurso real (`translatable="false"`), aunque el código los reemplaza en `onCreate` | Dos fuentes de verdad; quien lea el XML cree que ahí se define el rango                                                                                                                            | Quitar `max`/`progress` del XML y usar `tools:text="15%"`         | Pendiente                                                |
| 2   | `#FFAB40` (thumb y barra del SeekBar) sobre `#FDFCFF` ≈ **1,8:1**. El naranja del PDF (`#FF9100`) daría ≈ 2,2:1                                                | No cumple WCAG 1.4.11 (3:1 para componentes) en modo claro; en oscuro sí cumple                                                                                                                    | Un naranja más oscuro para el SeekBar (Orange 800 o similar)      | Pendiente · captura 01                                   |
| 3   | TalkBack anuncia el SeekBar como proporción del rango: 15 de 30 puede leerse como "50 %"                                                                       | El usuario ciego oye un porcentaje distinto del que se aplica                                                                                                                                      | `ViewCompat.setStateDescription(seekBar, formatter.percent(...))` | Sin verificar                                            |
| 4   | `tipTextView` y `totalTextView` sin `accessibilityLiveRegion`                                                                                                  | TalkBack no anuncia la propina ni el total cuando cambian                                                                                                                                          | `android:accessibilityLiveRegion="polite"`                        | Pendiente                                                |
| 5   | El EditText tiene `contentDescription` además de `hint`                                                                                                        | Accessibility Scanner lo marca: en campos editables la etiqueta debe ser el `hint`                                                                                                                 | Quitar `contentDescription`                                       | Pendiente                                                |
| 6   | `statusBarColor` y `navigationBarColor` transparentes en los temas, con `tools:targetApi="l"`                                                                  | Atributos obsoletos en API 35 y redundantes con `enableEdgeToEdge`; `targetApi="l"` no aporta con minSdk 24                                                                                        | Borrarlos                                                         | Pendiente                                                |
| 7   | `values-night/themes.xml` repite los cuatro colores del tema claro                                                                                             | Si cambia la marca, hay que editar dos archivos                                                                                                                                                    | Estilo base común y sobrescribir solo lo que difiere              | Pendiente                                                |
| 8   | Ícono adaptativo de la plantilla; el paso 2.5.2 (al que remite 3.4.2) no se aplicó                                                                             | La app no tiene identidad propia en el lanzador                                                                                                                                                    | Image Asset con el logo del curso                                 | Pendiente · captura 08                                   |
| 9   | `constraintlayout` y `androidx.test.rules` declarados sin uso; `runner` y `rules` con versión literal en el catálogo                                           | Dependencias sin uso (YAGNI) y catálogo inconsistente                                                                                                                                              | Quitarlas y usar `version.ref`                                    | Pendiente                                                |
| 10  | `R.color.black` y `R.color.white` sin uso                                                                                                                      | Advertencia `UnusedResources`                                                                                                                                                                      | Borrarlos                                                         | Pendiente                                                |
| 11  | `data/repository`, `domain/repository` vacías y `.gitkeep` sobrante en `ui/tipcalculator`                                                                      | Sugieren persistencia que no existe                                                                                                                                                                | —                                                                 | Aceptado: convención del curso                           |
| 12  | Constantes públicas en español (`PORCENTAJE_MINIMO`, `PORCENTAJE_MAXIMO`, `PORCENTAJE_INICIAL`)                                                                | Rompe la convención de P05 (API pública en inglés)                                                                                                                                                 | Renombrar a `MIN_PERCENT`, etc.                                   | Pendiente                                                |
| 13  | El estado del ViewModel es una propiedad simple y la Activity llama a `render()` después de cada evento                                                        | Un evento nuevo que olvide `render()` deja la pantalla desactualizada                                                                                                                              | `StateFlow` si la pantalla crece                                  | Aceptado (KISS)                                          |
| 14  | El ViewModel guarda `billAmount` y `tipPercent`, que ya están en `state`                                                                                       | La misma información dos veces                                                                                                                                                                     | Derivar de `state`                                                | Pendiente                                                |
| 15  | El KDoc del ViewModel dice que, a diferencia del PDF, el estado sobrevive a la recreación                                                                      | Es exagerado: en el PDF, el EditText y el SeekBar restauran su propio estado y sus listeners reconstruyen `billAmount` y `percent`. El aporte real del ViewModel es aislar la lógica para probarla | Corregir el KDoc                                                  | Aceptado: aclarado en este documento                     |
| 16  | Teclear "0" o "000" muestra el hint "Enter Amount" aunque el EditText tenga dígitos                                                                            | El PDF mostraría $0.00; el usuario puede creer que no escribió nada                                                                                                                                | Mostrar el hint solo si el texto está vacío                       | Pendiente                                                |
| 17  | El bloqueo vertical en pantallas ≥600dp depende de `PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY`, una exclusión temporal de Android 16                       | Al subir el targetSdk la app deberá funcionar en horizontal en tablets, y el layout no está preparado                                                                                              | Diseño para horizontal (dos columnas o `ScrollView`)              | Aceptado: targetSdk 36 lo fija el enunciado · captura 07 |
| 18  | No hay `ScrollView`                                                                                                                                            | Con fuente grande y el teclado abierto, el total puede quedar oculto                                                                                                                               | Envolver el GridLayout en un `ScrollView`                         | Sin verificar · captura 06                               |
| 19  | `MainActivityTest` lanza `ActivityScenario` en `@Before` sin cerrarlo, y no usa `AccessibilityChecks`                                                          | Actividades acumuladas entre pruebas; la accesibilidad no se verifica automáticamente (P05 sí lo hace)                                                                                             | `ActivityScenarioRule` y `AccessibilityChecks.enable()`           | Pendiente                                                |
| 20  | `unitTests.isReturnDefaultValues = true`                                                                                                                       | Puede ocultar una dependencia accidental de Android                                                                                                                                                | El dominio se verifica con `git grep` (sección 8)                 | Aceptado                                                 |

---

## 8. Cómo verificar este documento

```bat
:: 30 pruebas unitarias y 0 errores de Lint
gradlew test lint

:: 4 pruebas instrumentadas (emulador API 36)
gradlew connectedDebugAndroidTest

:: El dominio y core no importan Android (no debe imprimir nada)
git grep -l "import android" -- app/src/main/java/ec/edu/uta/fisei/tipcalculator/domain app/src/main/java/ec/edu/uta/fisei/tipcalculator/core

:: §7 #1: el rango sigue en el XML
git grep -n "android:max\|android:progress\|tip_percentage" -- app/src/main/res

:: §7 #9: nadie usa ConstraintLayout ni las reglas de prueba (no debe imprimir nada)
git grep -n "ConstraintLayout\|ActivityTestRule\|GrantPermissionRule" -- app/src
```
