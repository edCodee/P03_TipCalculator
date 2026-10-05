# Tip Calculator — Pruebas

Qué se prueba, cómo y qué queda fuera. El PDF no incluye ninguna prueba. Este proyecto tiene 34.

| Tipo           | Clases | Pruebas | Resultado      | Dónde corre                    |
| -------------- | ------ | ------- | -------------- | ------------------------------ |
| Unitarias      | 4      | 30      | 100 % en verde | JVM, sin emulador              |
| Instrumentadas | 1      | 4       | En verde       | Pixel 7 y Pixel Tablet, API 36 |

---

## 1. Cómo ejecutarlas

```bat
gradlew test
:: Reporte: app/build/reports/tests/testDebugUnitTest/index.html

gradlew test --tests "ec.edu.uta.fisei.tipcalculator.domain.usecase.CalculateTipUseCaseTest"

gradlew connectedDebugAndroidTest
:: Reporte: app/build/reports/androidTests/connected/debug/index.html

gradlew lint
```

---

## 2. Estrategia

| Nivel              | Qué verifica                                                           | Por qué en ese nivel                                            |
| ------------------ | ---------------------------------------------------------------------- | --------------------------------------------------------------- |
| **Unitarias**      | Lectura de centavos, cálculo, redondeo, invariante, formato, ViewModel | Rápidas y deterministas; el dominio no usa Android              |
| **Instrumentadas** | Teclear → pantalla; SeekBar → pantalla; borrar                         | Cableado real con vistas infladas                               |
| **Manual**         | Español, modo oscuro, fuente grande, tablet, teclado, ícono            | Configuración del sistema ([sección 6](#6-verificación-manual)) |

El dominio se escribió con TDD (commit `fe9ec15`, 19 pruebas antes de la UI).

---

## 3. Pruebas unitarias

| Clase de prueba              | #   | Qué protege                                                                                                                                                                                                                                | Relación con el PDF           |
| ---------------------------- | --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----------------------------- |
| `CalculateTipUseCaseTest`    | 11  | Escenario de la Fig. 3.1(b) (56.32 al 25 % → 14.08 / 70.40) y al 15 %; extremos 0 % y 30 %; monto cero; `HALF_UP` (3.70 × 15 % → 0.56); escala 2; invariante total = monto + propina para 0–30 %; rechazo de −1 %, 31 % y montos negativos | Fig. 3.1, Fig. 3.16, Obs. 3.3 |
| `ParseBillAmountUseCaseTest` | 8   | Secuencia 5 → 0.05, 56 → 0.56, 563 → 5.63, 5632 → 56.32; máximo 999999 → 9999.99; vacío y no numérico → 0.00; ceros a la izquierda                                                                                                         | PDF 3.2, Fig. 3.18, paso 7    |
| `MoneyFormatterTest`         | 5   | `$56.32`, `$0.00`, `15%`, `0%`/`30%` en `en-US`; locales distintos dan formatos distintos                                                                                                                                                  | PDF 3.3.6                     |
| `TipCalculatorViewModelTest` | 6   | Estado inicial (0 al 15 %); teclear; escenario completo del PDF; el porcentaje se conserva; borrar vuelve a cero; fuera de rango se acota                                                                                                  | PDF 3.2, 3.6                  |

---

## 4. Pruebas instrumentadas

| Clase              | Prueba                                                     | Qué verifica                             |
| ------------------ | ---------------------------------------------------------- | ---------------------------------------- |
| `MainActivityTest` | `alIniciar_muestraCerosYQuincePorCiento`                   | Propina y total en 0, porcentaje en 15 % |
|                    | `alTeclear5632_muestraMontoPropinaYTotalAlQuincePorCiento` | 56.32 → 8.45 / 64.77 en pantalla         |
|                    | `alBorrarTodo_vuelveACeros`                                | Borrar el texto devuelve los ceros       |
|                    | `escenarioDelPdf_5632AlVeinticincoPorCiento`               | SeekBar a 25 → 14.08 / 70.40             |

- **Independientes del locale:** el valor esperado se formatea con `MoneyFormatter()` usando el locale del dispositivo, así que pasan tanto en inglés como en español.
- Se ejecutaron en Pixel 7 y Pixel Tablet (Android 16): 8 ejecuciones, 0 fallos.

---

## 5. Técnicas usadas

| Técnica                                  | Dónde                        | Para qué                                                    |
| ---------------------------------------- | ---------------------------- | ----------------------------------------------------------- |
| Locale inyectado                         | `MoneyFormatterTest`         | Resultados iguales en cualquier máquina                     |
| Prueba de invariante con bucle           | `CalculateTipUseCaseTest`    | Recorrer todo el rango 0–30 %, no solo ejemplos             |
| ViewModel instanciado directamente       | `TipCalculatorViewModelTest` | Sin `ViewModelProvider` ni emulador                         |
| `ViewAction` propia `setSeekBarProgress` | `MainActivityTest`           | Espresso no trae acción para SeekBar sin `espresso-contrib` |
| Nombres con comillas invertidas          | Pruebas unitarias            | El nombre describe el comportamiento en español             |

Contraste calculado con la fórmula de WCAG 2.x:

| Primer plano        | Fondo                  | Contraste | Requisito                    | Cumple                  |
| ------------------- | ---------------------- | --------- | ---------------------------- | ----------------------- |
| Blanco (título)     | `#2196F3` (PDF)        | 3,1:1     | 4,5:1 texto                  | No                      |
| Blanco (título)     | `#1976D2` (proyecto)   | 4,6:1     | 4,5:1 texto                  | Sí                      |
| `#1B1B1B`           | `#BBDEFB` (monto)      | ≈ 12:1    | 4,5:1 texto                  | Sí                      |
| `#1B1B1B`           | `#FFE0B2` (resultados) | ≈ 14:1    | 4,5:1 texto                  | Sí                      |
| `#FFAB40` (SeekBar) | `#FDFCFF` (fondo)      | ≈ 1,8:1   | 3:1 componente (WCAG 1.4.11) | **No** (ANALISIS §7 #2) |

---

## 6. Verificación manual

Capturas en `docs/img/`, tomadas con `adb exec-out screencap -p` desde **cmd**.

| Captura / escenario    | Cómo se prepara                                  | Qué se comprueba                                                                     |
| ---------------------- | ------------------------------------------------ | ------------------------------------------------------------------------------------ |
| `01_inicial.png`       | Pixel 7, arranque en frío                        | Teclado numérico visible (`stateAlwaysVisible`), $0.00 y 15 %; contraste del SeekBar |
| `02_quince.png`        | `adb shell input text 5632`                      | $56.32 → $8.45 / $64.77                                                              |
| `03_veinticinco.png`   | Arrastrar el SeekBar a 25 %                      | $14.08 / $70.40 (Fig. 3.1 b)                                                         |
| `04_espanol.png`       | `cmd locale set-app-locales … --locales es-EC`   | Textos en español y formato de Ecuador (dólar, coma decimal)                         |
| `05_oscuro.png`        | `cmd uimode night yes`                           | Cajas legibles con texto oscuro fijo                                                 |
| `06_fuente_grande.png` | `settings put system font_scale 2.0`             | Si el total queda oculto sin `ScrollView` (ANALISIS §7 #18)                          |
| `07_tablet.png`        | Pixel Tablet                                     | Comportamiento del bloqueo vertical en pantalla grande (ANALISIS §7 #17)             |
| `08_icono.png`         | Cajón de apps                                    | Ícono de plantilla (ANALISIS §7 #8)                                                  |
| `09_pruebas.png`       | Reporte `testDebugUnitTest/index.html`           | 30 pruebas, 100 %                                                                    |
| `10_lint.png`          | `lint-results-debug.html`                        | 0 errores                                                                            |
| Teclado (sin captura)  | Cerrar el teclado con Atrás y tocar la caja azul | El teclado reaparece (ANALISIS §6.1 #8). Resultado: ⚠ COMPLETAR                      |

---

## 7. Límites conocidos

| Qué no se verifica                                 | Motivo                                        | Mitigación                      |
| -------------------------------------------------- | --------------------------------------------- | ------------------------------- |
| Formato exacto en `es-EC` y `de-DE`                | La prueba de locales solo compara desigualdad | Captura 04                      |
| TalkBack (anuncio del SeekBar y de los resultados) | No automatizado; sin `AccessibilityChecks`    | ANALISIS §7 #3, #4 y #19        |
| Recreación con "No conservar actividades"          | No automatizado                               | Razonamiento en ARQUITECTURA §5 |
| Diseño en horizontal                               | La app se bloquea en vertical                 | ANALISIS §7 #17                 |

---

## 8. Evolución del número de pruebas

| Fase | Commit    | Unitarias | Instrumentadas | Qué se agregó                                             |
| ---- | --------- | --------- | -------------- | --------------------------------------------------------- |
| 2    | `fe9ec15` | 19        | —              | `CalculateTipUseCase` (11) y `ParseBillAmountUseCase` (8) |
| 5    | `78c038b` | 30        | —              | `MoneyFormatter` (5) y `TipCalculatorViewModel` (6)       |
| 8    | `e5a1240` | 30        | 4              | Espresso independiente del locale                         |
