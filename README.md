# Aljabar Linier dan Geometri Tubes 1 Template

## About this template

This template is the starter file structure for Tubes 1 Algeo 2025/2026. This template is a Java project using Maven Build tool with JavaFX GUI already configured in pom.xml.

In this project, you can choose if you want to develop to CLI or GUI app. The default is CLI, if you want GUI do uncomment the necessary part in App.java to run the template GUI test then run the commands below to run the app.

## Requirements

Before building and running the **Matrix Calculator**, make sure you have the following installed:

### Java
- **Version:** 17 or higher
- **Download links:**
  - [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads)

### Maven
- **Version:** 3.2.5 or higher (recommended 3.6.3+)
- **Download links:**
  - [Direct Apache Maven Official Downloads](https://dlcdn.apache.org/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.zip)

### Additional installation info

### Windows
For maven installation, download the .zip and it should contain a directory with
```
apache-maven-<version>/
├── bin/               <-- executable scripts (mvn, mvn.cmd)
├── boot/         
├── conf/          
├── lib/          
├── NOTICE
├── LICENSE
├── README.txt
```

Put bin/ in environment PATH to use in terminal. [Add folder to PATH tutorial](https://www.youtube.com/watch?v=pGRw1bgb1gU)

### Linux
```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
sudo apt install maven -y
```

### MacOS
```bash
brew install openjdk@17
brew install maven
```

## How to develop

Using maven, the root development directory is `src/main/java/algeo`
There should not be any coding outside of that directory other than `test` using JUnit or other libraries.

Inside `src/main/java/algeo`, develop modules that are modular to use in the main program (`App.java`)

When running `mvn exec:java` later, `App.java` main program will be the one that is run.

## How to run
1. Compiling the program
The following command will produce a `target` directory with `matrix-calculator-1.0-SNAPSHOT.jar` in it
```bash
mvn clean package
```

alternatively, if you don't want to make a .jar file, you can use
```bash
mvn clean compile
```

2. Running the program
To run CLI, run:
```bash
mvn exec:java
```

To run GUI, be sure to uncomment the main GUI and run:
```bash
mvn clean javafx:run
```

when the program is first run, it should print in terminal:
```bash
Hai 
Halo Algeo!
```

## Using the program as a library

Copy the .jar file that is in the `target` file (from running `mvn clean compile`) to `bin` for submission, this .jar file can be used in other projects to import modules in this current project