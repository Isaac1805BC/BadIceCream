@echo off
cd /d "%~dp0"

REM Crear directorio bin si no existe
if not exist bin mkdir bin

echo Compilando codigo principal...
REM Generar lista de archivos fuente principales
(for /R src\main\java %%f in (*.java) do echo %%f) > sources_main.txt

REM Compilar codigo principal
javac -d bin -sourcepath src\main\java @sources_main.txt 2>nul

REM Eliminar archivo temporal
del sources_main.txt

if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar el codigo principal
    pause
    exit /b 1
)

echo Codigo principal compilado exitosamente.
echo.
echo Compilando pruebas...

REM Generar lista de archivos de prueba
(for /R src\test\java %%f in (*.java) do echo %%f) > sources_test.txt

REM Compilar pruebas con JUnit en el classpath
javac -cp "lib\*;bin" -d bin -sourcepath src\test\java @sources_test.txt 2>nul

REM Eliminar archivo temporal
del sources_test.txt

if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar las pruebas
    pause
    exit /b 1
)

echo Pruebas compiladas exitosamente.
echo.
echo Ejecutando pruebas...
echo.

REM Ejecutar todas las pruebas con JUnit
java -jar lib\junit-platform-console-standalone-1.9.3.jar --class-path bin --scan-classpath

echo.
pause
