#!/bin/sh

while [ ! -f /projects/terrakube/scripts/setup/dex/config-ldap.yaml ]
do
  sleep 2
done
dex serve --web-http-addr 0.0.0.0:5556 /projects/terrakube/scripts/setup/dex/config-ldap.yaml 