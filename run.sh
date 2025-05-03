#!/bin/bash

# Print the current working directory
pwd
cd ./ProjectManagerApp

javac -cp src/mysql-connector-j-9.3.0.jar -d out $(find src/main/java -name "*.java") 

wait

java -cp "out:src/mysql-connector-j-9.3.0.jar" com.projectmanager.App 