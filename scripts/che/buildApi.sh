#!/bin/bash

# Set java 17
# sdk install java 17.0.6-tem

# Set maven 
# sdk install maven

# Build
mvn clean install -Dmaven.test.skip=true -Dspring-boot.build-image.skip=true -f api/pom.xml 