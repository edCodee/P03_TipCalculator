@echo off
set PKG=ec.edu.uta.fisei.tipcalculator
set ACT=%PKG%/.ui.tipcalculator.MainActivity
set IMG=docs\img
if not exist %IMG% mkdir %IMG%

:: Obligamos a usar el Pixel 7 (ahora sabemos que es el 5556)
set ADB=adb -s emulator-5556

echo Iniciando capturas automaticas...

:: Estado base
%ADB% shell settings put system accelerometer_rotation 0
%ADB% shell settings put system user_rotation 0
%ADB% shell cmd uimode night no
%ADB% shell settings put system font_scale 1.0

echo 1. Captura inicial (teclado visible)...
%ADB% shell am force-stop %PKG%
%ADB% shell am start -W -n %ACT% >nul
timeout /t 3 >nul
%ADB% exec-out screencap -p > %IMG%\01_inicial.png

echo 2. Escribiendo 5632 al 15 por ciento...
%ADB% shell input text 5632
timeout /t 2 >nul
%ADB% exec-out screencap -p > %IMG%\02_quince.png

echo ---------------------------------------------------
echo 3. MANUAL: Ve al emulador y arrastra el SeekBar 
echo hasta el 25 por ciento. Luego presiona ENTER aqui.
echo ---------------------------------------------------
pause >nul
%ADB% exec-out screencap -p > %IMG%\03_veinticinco.png

echo 4. Cambiando a espanol Ecuador...
%ADB% shell cmd locale set-app-locales %PKG% --locales es-EC
%ADB% shell am force-stop %PKG%
%ADB% shell am start -W -n %ACT% >nul
timeout /t 3 >nul
%ADB% shell input text 5632
timeout /t 2 >nul
%ADB% exec-out screencap -p > %IMG%\04_espanol.png
%ADB% shell cmd locale set-app-locales %PKG% --locales en-US

echo 5. Cambiando a modo oscuro...
%ADB% shell cmd uimode night yes
%ADB% shell am force-stop %PKG%
%ADB% shell am start -W -n %ACT% >nul
timeout /t 3 >nul
%ADB% shell input text 5632
timeout /t 2 >nul
%ADB% exec-out screencap -p > %IMG%\05_oscuro.png
%ADB% shell cmd uimode night no

echo 6. Cambiando a fuente grande...
%ADB% shell settings put system font_scale 2.0
%ADB% shell am force-stop %PKG%
%ADB% shell am start -W -n %ACT% >nul
timeout /t 3 >nul
%ADB% shell input text 5632
timeout /t 2 >nul
%ADB% exec-out screencap -p > %IMG%\06_fuente_grande.png
%ADB% shell settings put system font_scale 1.0

echo Listo. Revisa la carpeta docs\img.