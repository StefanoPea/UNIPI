#!/bin/bash
javac -d out -cp ./jackson/jackson-annotations-2.12.3.jar:./jackson/jackson-core-2.12.3.jar:./jackson/jackson-databind-2.12.3.jar:./src src/serverMain.java

javac -d out -cp ./jackson/jackson-annotations-2.12.3.jar:./jackson/jackson-core-2.12.3.jar:./jackson/jackson-databind-2.12.3.jar:./src src/clientMain.java
