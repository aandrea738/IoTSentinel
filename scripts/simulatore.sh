#!/bin/bash

# Identifica l'intensità (valore di default: NORMALE)
INTENSITA=${1:-NORMALE}

# Spostati nella cartella dello script
cd "$(dirname "$0")"

echo "=========================================="
echo " Avvio Simulatore IoT Sentinel (Python)"
echo " Intensità selezionata: $INTENSITA"
echo "=========================================="

if command -v python3 >/dev/null 2>&1; then
    python3 simulatore.py -i "$INTENSITA"
elif command -v python >/dev/null 2>&1; then
    python simulatore.py -i "$INTENSITA"
else
    echo "Errore: Python non è stato trovato nel sistema."
    exit 1
fi
