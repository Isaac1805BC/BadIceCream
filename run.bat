@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -d bin src\main\java\com\badice\Main.java src\main\java\com\badice\domain\entities\*.java src\main\java\com\badice\domain\interfaces\*.java src\main\java\com\badice\domain\patterns\*.java src\main\java\com\badice\domain\states\*.java src\main\java\com\badice\domain\factories\*.java src\main\java\com\badice\domain\config\*.java src\main\java\com\badice\domain\services\*.java src\main\java\com\badice\presentation\view\*.java src\main\java\com\badice\presentation\controller\*.java
if %ERRORLEVEL% EQU 0 (
    echo Copiando recursos...
    xcopy /E /I /Y src\main\resources\* bin\ >nul 2>&1
    java -cp bin com.badice.Main
) else (
    echo Error al compilar
    pause
)
