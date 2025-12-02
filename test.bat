@echo off
cd /d "%~dp0"

javac -d bin src\main\java\com\badice\Main.java src\main\java\com\badice\domain\entities\*.java src\main\java\com\badice\domain\interfaces\*.java src\main\java\com\badice\domain\patterns\*.java src\main\java\com\badice\domain\states\*.java src\main\java\com\badice\domain\factories\*.java src\main\java\com\badice\domain\config\*.java src\main\java\com\badice\domain\services\*.java src\main\java\com\badice\domain\enums\*.java src\main\java\com\badice\presentation\view\*.java src\main\java\com\badice\presentation\controller\*.java 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar el codigo principal
    pause
    exit /b 1
)

javac -cp "lib\*;bin" -d bin src\test\java\com\badice\domain\entities\*.java src\test\java\com\badice\domain\services\*.java 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar las pruebas
    pause
    exit /b 1
)

java -jar lib\junit-platform-console-standalone-1.9.3.jar --class-path bin --scan-classpath
pause
