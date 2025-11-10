#!/bin/bash
newTag=$(date +%y.%m.%d)
echo new tag: $newTag

./gradlew clean build

docker build -t shiviraj/nexora-backend:latest -t shiviraj/nexora-backend:$newTag .
docker push shiviraj/nexora-backend:latest
docker push shiviraj/nexora-backend:$newTag