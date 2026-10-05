# Tip Calculator — Plan de fases (Práctica 03, Deitel Cap. 3, API 36)

| Fase       | Contenido                                                                                     | Commit          |
| ---------- | --------------------------------------------------------------------------------------------- | --------------- |
| 1          | Proyecto base: SDK 37/36/24, Kotlin DSL, viewBinding, paquetes, pipeline de pruebas           | 0bff88e         |
| 1 (chore)  | Finales de línea según `.gitattributes`; también corrigió targetSdk 37 → 36                   | 8cf0d51         |
| 1 (cierre) | Compila, ejecuta en el AVD y corre las pruebas                                                | 9c2d809         |
| —          | docs: aclara que `8cf0d51` también corrigió el targetSdk                                      | 98dfb78         |
| 2          | Dominio con TDD: `CalculateTipUseCase` y `ParseBillAmountUseCase` en `BigDecimal`, 19 pruebas | fe9ec15         |
| 3          | GUI con GridLayout de 2 × 4, recursos externalizados, ViewBinding e insets                    | 52c7d8f         |
| 4          | Tema Material 3: paleta del PDF con contraste WCAG, app bar y edge-to-edge                    | 5bbc328         |
| 4 (fix)    | GridLayout con `wrap_content`; ignorar `deviceManager.xml`                                    | 4af4077         |
| 5          | Cableado UI-dominio: ViewModel, `MoneyFormatter`, `doOnTextChanged`; 30 pruebas               | 78c038b         |
| 6          | Manifiesto: orientación, teclado y `PROPERTY_COMPAT` con evidencia A/B en teléfono y tablet   | 03bccb7         |
| 7          | Internacionalización (es) y etiquetas de accesibilidad                                        | 696110c         |
| 8          | Espresso: 4 pruebas de UI independientes del locale                                           | e5a1240         |
| 8 (fix)    | `tip_percentage` con `translatable="false"`; Lint sin errores                                 | 8c25741         |
| 9          | Verificación, documentación y capturas; código congelado                                      | v1.0-practica03 |

Regla: no se pasa de fase sin pruebas y Lint en verde y sin commit, revisando `git status` antes y después.

Fase 9: a partir de aquí el código no se modifica. Lo que falla se documenta en [ANALISIS_DISENO.md §7](ANALISIS_DISENO.md).
