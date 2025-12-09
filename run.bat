@echo off
cd /d "%~dp0"
if not exist bin mkdir bin

REM Generar una lista temporal de archivos .java
(for /R src\main\java %%f in (*.java) do echo %%f) > sources.txt

REM Compilar usando la lista de archivos y estableciendo el sourcepath
javac -d bin -sourcepath src\main\java @sources.txt

REM Eliminar el archivo temporal
del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilación exitosa.
    echo Copiando recursos...
    xcopy /E /I /Y src\main\resources\* bin\ >nul 2>&1
    java -cp bin com.badice.Main
) else (
    echo Error al compilar
    pause
)