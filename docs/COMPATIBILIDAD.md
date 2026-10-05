# Tip Calculator — Correcciones de compatibilidad (PDF Deitel 2016 → Android 16 / API 36)

Proyecto: compileSdk 37, targetSdk 36, minSdk 24. Kotlin integrado en AGP 9.3.2.
Verificado en Pixel 7 y Pixel Tablet, API 36. Pruebas: 30 unitarias + 4 instrumentadas.

Las filas marcadas con **→ §7 #n** tienen una limitación documentada en [ANALISIS_DISENO.md](ANALISIS_DISENO.md), sección 7.

## Proyecto y build

| #   | PDF                                                                    | Problema actual                                                        | Corrección                                                                           |
| --- | ---------------------------------------------------------------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| 1   | Java (`MainActivity.java`)                                             | Kotlin es el lenguaje recomendado                                      | Todo el proyecto en Kotlin; AGP 9 lo compila sin el plugin `kotlin-android`          |
| 2   | `android.support.v7.app.AppCompatActivity` (Fig. 3.12 l. 6)            | Support Library descontinuada                                          | `androidx.appcompat.app.AppCompatActivity`                                           |
| 3   | `package="com.deitel.tipcalculator"` en el manifiesto (Fig. 3.19 l. 3) | Movido al build                                                        | `namespace` y `applicationId` `ec.edu.uta.fisei.tipcalculator` en `build.gradle.kts` |
| 4   | Minimum SDK API 23                                                     | Valores de 2016                                                        | minSdk 24, targetSdk 36 (enunciado; corregido de 37 en `8cf0d51`), compileSdk 37     |
| 5   | Build en Groovy                                                        | Kotlin DSL es el predeterminado                                        | `build.gradle.kts` y catálogo `gradle/libs.versions.toml`                            |
| 6   | `findViewById` + casts (Fig. 3.15 ll. 39–53)                           | Propenso a errores                                                     | ViewBinding (`ActivityMainBinding`)                                                  |
| 7   | Data Binding "en beta" (nota de 3.6.4)                                 | Hoy ViewBinding es estable y cubre este caso sin expresiones en el XML | ViewBinding                                                                          |
| 8   | `GridLayout` de la Support Library para API < 14 (3.3.4)               | Innecesario con minSdk 24                                              | `android.widget.GridLayout` del framework                                            |

## IDE

| #   | PDF                                                     | Problema actual                   | Corrección                                                                          |
| --- | ------------------------------------------------------- | --------------------------------- | ----------------------------------------------------------------------------------- |
| 9   | Theme Editor (3.5.2)                                    | Eliminado de Android Studio       | `themes.xml` editado a mano                                                         |
| 10  | Palette con Plain / Large / Medium / Small Text (3.4.4) | La paleta ofrece un solo TextView | `textAppearance="?attr/textAppearanceBodyLarge"` (escala tipográfica de Material 3) |
| 11  | AVD Nexus 6                                             | Retirado del catálogo             | Pixel 7 y Pixel Tablet, API 36                                                      |

## Tema y pantalla

| #   | PDF                                                          | Problema actual                                                                                                                | Corrección                                                                                                                                                                                                                          |
| --- | ------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 12  | `Theme.AppCompat.Light.DarkActionBar` (3.5.1)                | Material 3 es el sistema de diseño actual                                                                                      | `Theme.Material3.DayNight.NoActionBar` + `MaterialToolbar` con `setSupportActionBar`                                                                                                                                                |
| 13  | `colorPrimary`, `colorPrimaryDark`, `colorAccent` (Fig. 3.6) | Material 3 usa roles: `colorPrimary`, `colorSecondary`, `colorOn*`; `colorPrimaryDark` pierde sentido con barras transparentes | `colorPrimary`/`colorOnPrimary` y `colorSecondary`/`colorOnSecondary`                                                                                                                                                               |
| 14  | Material Blue 500 (`#2196F3`) con título blanco (3.5.2)      | 3,1:1: no cumple WCAG AA para texto normal                                                                                     | `brand_blue` `#1976D2` (Blue 700): 4,6:1 (commit `5bbc328`)                                                                                                                                                                         |
| 15  | Orange accent 400 (`#FF9100`) para el SeekBar (3.5.2)        | Contraste no textual bajo sobre fondo claro                                                                                    | `brand_orange` `#FFAB40`, que tampoco alcanza 3:1 → §7 #2                                                                                                                                                                           |
| 16  | Barra de estado con `colorPrimaryDark`                       | Edge-to-edge obligatorio con targetSdk 35+                                                                                     | `enableEdgeToEdge(SystemBarStyle.dark(...))`; la toolbar recibe el inset superior y la raíz los laterales y el inferior → §7 #6                                                                                                     |
| 17  | Solo modo claro                                              | Con DayNight, el texto del tema sería claro sobre las cajas celeste y naranja                                                  | Texto fijo `on_colored_background` (`#1B1B1B`) en las cajas: más de 12:1 en ambos modos                                                                                                                                             |
| 18  | Teclado visible con `stateAlwaysVisible` (3.7.3)             | Con edge-to-edge el sistema ya no redimensiona la ventana                                                                      | Se conserva `stateAlwaysVisible`; los insets incluyen `ime()`, así que el contenido sube sobre el teclado                                                                                                                           |
| 19  | `screenOrientation="portrait"` (3.7.3)                       | API 36 ignora las restricciones de orientación en pantallas ≥600dp                                                             | Se conserva el atributo, se silencian `DiscouragedApi` y `LockedOrientationActivity`, y se declara `PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY`, una exclusión temporal de Android 16 (commit `03bccb7`, evidencia A/B) → §7 #17 |
| 20  | Ícono con Asset Studio (3.4.2 remite a 2.5.2)                | Desde API 26 el lanzador usa íconos adaptativos                                                                                | **No aplicada**: ícono adaptativo de la plantilla → §7 #8                                                                                                                                                                           |

## Vistas y eventos

| #   | PDF                                                                 | Problema actual                                                                    | Corrección                                                                                         |
| --- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 21  | `TextWatcher` anónimo con dos métodos vacíos (Fig. 3.18)            | Código de relleno                                                                  | `doOnTextChanged` de core-ktx                                                                      |
| 22  | `layout_gravity="center_vertical\|right"` (3.4.5, paso 9)           | Lint `RtlHardcoded` con `supportsRtl="true"`                                       | `center_vertical\|end`                                                                             |
| 23  | SeekBar con `max="30"` y `progress="15"` en el XML (3.4.5, paso 10) | El rango es una regla de negocio                                                   | `max` se asigna desde `CalculateTipUseCase.PORCENTAJE_MAXIMO`; el XML conserva los valores → §7 #1 |
| 24  | Recurso `tip_percentage` = "15%" (Fig. 3.5)                         | El texto se reemplaza en tiempo de ejecución; Lint `MissingTranslation` lo marcaba | `translatable="false"` (commit `8c25741`) → §7 #1                                                  |
| 25  | GridLayout de alto completo                                         | Se estiraban las filas                                                             | `layout_height="wrap_content"` (commit `4af4077`)                                                  |

## Accesibilidad e internacionalización

| #   | PDF                                                        | Problema actual                     | Corrección                                                                                        |
| --- | ---------------------------------------------------------- | ----------------------------------- | ------------------------------------------------------------------------------------------------- |
| 26  | Solo inglés; el capítulo sugiere internacionalizar (3.3.6) | Lint `MissingTranslation` como gate | `values-es` (commit `696110c`)                                                                    |
| 27  | EditText oculto sin etiqueta                               | TalkBack no sabría qué es           | `hint` "Bill amount in cents"; el TextView de encima con `importantForAccessibility="no"` → §7 #5 |
| 28  | SeekBar sin etiqueta                                       | Igual                               | `contentDescription` "Tip percentage" → §7 #3                                                     |
| 29  | Áreas táctiles sin mínimo                                  | Material exige 48dp                 | EditText con `minHeight="48dp"`                                                                   |
| 30  | `Toast`/`findViewById` en las pruebas: el PDF no tiene     | —                                   | Espresso con ids reales y aserciones independientes del locale (commit `e5a1240`)                 |

## Alcance verificado

- 4 pruebas instrumentadas en verde en **Pixel 7** y **Pixel Tablet** (API 36).
- Formato por locale: `en-US` probado en unidad; `es-EC` y `de-DE` solo por captura o por desigualdad (PRUEBAS §7).
- Contrastes calculados con la fórmula de WCAG 2.x (PRUEBAS §5).
