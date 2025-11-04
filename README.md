"# VisionJava"

package 명령어 순서


rem 0)
rmdir /s /q target 2>nul
mkdir target\classes

javac --module-path "F:\Java\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml -d target\classes --source-path src\main\java src\main\java\forjava\*.java

jar --create --file target\ocrforjava-1.0.jar --main-class forjava.App -C target\classes . -C src\main\resources .


jar tf target\ocrforjava-1.0.jar | findstr /i forjava/AppView.fxml
jar tf target\ocrforjava-1.0.jar | findstr /i forjava/TestCaseSetting.fxml
jar tf target\ocrforjava-1.0.jar | findstr /i forjava/images/home.png


jpackage --name "vojava" --type app-image --dest target/app --input target --main-jar ocrforjava-1.0.jar --main-class forjava.App --module-path "F:\Java\javafx-jmods-25" --add-modules java.sql,javafx.controls,javafx.fxml --java-options "--enable-native-access=javafx.graphics,ALL-UNNAMED" --java-options "-Dprism.order=sw" --win-console --resource-dir "F:\Company\Rootech\PNT\javaproject\ocrforjava\python_scripts" --resource-dir "F:\Company\Rootech\PNT\javaproject\ocrforjava\python_env"
--resource-dir python_scripts --resource-dir python_env


python 코드는 경로에 맞춰 직접 복사 붙이기 요구




visiontest) c:\PNT\AutoProgram\VisionJava> c: && cd c:\PNT\AutoProgram\VisionJava && cmd /C "C:\java\jdk-25\bin\java.exe -agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=localhost:49880 @C:\Users\jscho\AppData\Local\Temp\cp_e80b516amg8l3h8xumbhmqv9x.argfile forjava.App "
Database and table have been initialized.