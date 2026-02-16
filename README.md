# LiftIt Java Starter

Minimal Gradle starter project for Java 25.

## Project structure

- `src/main/java/com/liftit/App.java` - application entrypoint
- `src/test/java/com/liftit/AppTest.java` - JUnit 5 sample test
- `build.gradle` - Gradle build configuration
- `settings.gradle` - Gradle project settings
- `gradle/wrapper/gradle-wrapper.properties` - wrapper pinned to Gradle 9.3.1
- `gradlew` / `gradlew.bat` - wrapper launchers

## Requirements

- Java 25+
- No global Gradle install required (uses wrapper)

## Build and test

```bash
./gradlew clean test
```

## Run

```bash
./gradlew run
```
