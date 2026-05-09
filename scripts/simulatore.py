import json
import time
import urllib.request
import urllib.error
import argparse
import sys
import os
from datetime import datetime

# URL dell'API di IoTSentinel
API_URL = "http://localhost:8080/ingestione"

def simula(intensita):
    json_path = os.path.join(os.path.dirname(__file__), "./misurazioni.json")
    try:
        with open(json_path, "r") as f:
            data = json.load(f)
            misurazioni = data.get("misurazione", [])
    except Exception as e:
        print(f"Errore durante la lettura del file JSON: {e}")
        sys.exit(1)

    if not misurazioni:
        print("Nessuna misurazione trovata nel file.")
        sys.exit(0)

    # Determina il delay in base all'intensità
    if intensita == "BASSA":
        delay = 2.0   # 1 misurazione ogni 2 secondi
    elif intensita == "NORMALE":
        delay = 0.5   # 2 misurazioni al secondo
    elif intensita == "ALTA":
        delay = 0.1   # 10 misurazioni al secondo
    else:
        delay = 0.5

    print(f"Inizio simulazione di {len(misurazioni)} misurazioni.")
    print(f"Intensità: {intensita} (ritardo iterazione: {delay}s)")
    print(f"Endpoint: {API_URL}")
    print("-" * 50)

    for m in misurazioni:
        payload_dict = {
            "idSensore": m.get("sensore_id"),
            "valore": m.get("valore"),
            "tipoMisurazione": m.get("tipomisurazione"),
            "timestamp": m.get("timestamp")
        }
        
        payload_bytes = json.dumps(payload_dict).encode('utf-8')
        req = urllib.request.Request(API_URL, data=payload_bytes, headers={'Content-Type': 'application/json'})

        try:
            with urllib.request.urlopen(req) as response:
                status_code = response.getcode()
                if status_code in (200, 201, 204):
                    print(f"[{datetime.now().strftime('%H:%M:%S')}] Inviato sensore {payload_dict['idSensore']} - Valore: {payload_dict['valore']:.2f}")
                else:
                    print(f"[{datetime.now().strftime('%H:%M:%S')}] Errore API: Codice HTTP {status_code}")
        except urllib.error.HTTPError as e:
            error_body = e.read().decode('utf-8')
            print(f"[{datetime.now().strftime('%H:%M:%S')}] Errore API {e.code}: {error_body}")
        except urllib.error.URLError as e:
            print(f"Errore di rete: Impossibile contattare {API_URL} - {e.reason}")
        
        time.sleep(delay)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Simulatore di misurazioni IoT")
    parser.add_argument("-i", "--intensita", type=str, choices=["BASSA", "NORMALE", "ALTA"], 
                        default="NORMALE", help="Intensità del carico simulato: BASSA, NORMALE, ALTA")
    
    args = parser.parse_args()
    simula(args.intensita)
