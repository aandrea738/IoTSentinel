#!/bin/bash

# Esempio di utilizzo: ./avvia_simulatore.sh --threads=4 --error-rate=0.2 --delay=1000

cd "$(dirname "$0")"

./gradlew :app:quarkusRun -Dquarkus.package.main-class=simulatore -Dquarkus.args="$*"
