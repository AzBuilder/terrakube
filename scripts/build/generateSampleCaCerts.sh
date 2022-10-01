#!/bin/bash

rm terrakubeDemo1.key
rm terrakubeDemo2.key
rm terrakubeDemo1.crt
rm terrakubeDemo2.crt
rm bindings/ca-certificates/terrakubeDemo1.PEM
rm bindings/ca-certificates/terrakubeDemo1.PEM

openssl req -x509 -sha256 -days 30 -nodes -newkey rsa:2048 -subj "/CN=demo.terrakube1.com/C=SV/L=San Salvador" -keyout terrakubeDemo1.key -out terrakubeDemo1.crt
openssl req -x509 -sha256 -days 30 -nodes -newkey rsa:2048 -subj "/CN=demo.terrakube2.com/C=SV/L=San Salvador" -keyout terrakubeDemo2.key -out terrakubeDemo2.crt

openssl x509 -in terrakubeDemo1.crt -out bindings/ca-certificates/terrakubeDemo1.pem -outform PEM
openssl x509 -in terrakubeDemo2.crt -out bindings/ca-certificates/terrakubeDemo2.pem -outform PEM