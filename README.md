# Distributed IoT Sentinel - Project Work

**Corso:** Tecniche Avanzate di Programmazione
**Progetto n.10:** Distributed IoT Sentinel

## Casi d'Uso

### Funzionalità completate (Requisiti soddisfatti)

- **Acquisizione Dati (Ingestione):** Il sistema riesce a ricevere dati da diversi tipi di sensori (Temperatura, Pressione, Vibrazione, CO2).
  - _Come gestisco il carico:_ Ho usato una coda per le misurazioni. In questo modo, se arrivano migliaia di dati all'improvviso, il sistema non perde nulla e continua a rispondere.
  - _Indipendenza:_ I sensori sono svincolati l'uno dall'altro.
- **Motore di Analisi (Intelligenza):** Ho implementato un motore che non si limita a mostrare i dati, ma li valuta secondo tre tipi di regole:
  - Regole a soglia semplice (es. Temp > 80°C).
  - Regole di correlazione (es. Temp > 80°C AND Pressione > 5).
  - Regole temporali (es. Vibrazione anomala per più di 10 secondi).
- **Smart Alerting:** Per evitare che la dashboard venga inondata di messaggi (Alert Fatigue), il sistema raggruppa gli allarmi continui in un'unica segnalazione e li revoca automaticamente quando la situazione torna normale.
- **Dinamicità:** L'utente (tramite la dashboard) può aggiungere/rimuovere sensori o creare nuove regole di analisi. Tutto questo avviene "a caldo", senza dover riavviare il server.
- **Interfaccia Utente (Frontend):** Dashboard che mostra gli indicatori principali, il flusso di dati in tempo reale (telemetrie) e l'elenco degli allarmi attivi e dello storico, mantenendosi fluida.

### Funzionalità mancanti (Cosa si potrebbe aggiungere in futuro)

- **Comunicazione Real-Time:** Al momento il frontend effettua un polling continuo verso il backend per ricevere aggiornamenti. Questo approccio è poco efficiente; andrebbe sostituito con l'uso di WebSocket (o Server-Sent Events) in modo che sia il server a "spingere" i nuovi dati solo quando ci sono novità.
- **Personalizzazione soglie:** Attualmente le soglie possono essere definite solo con il maggiore.

## Architettura e Design Pattern adottati

### Struttura del sistema

Ho diviso l'applicazione in due parti separate per garantire una migliore manutenibilità:

- **Backend:** Sviluppato in Java utilizzando il framework **Quarkus**. L'ho scelto perché è molto veloce e mi ha permesso di creare API RESTful reattive. Per la persistenza su database PostgreSQL ho usato Hibernate ORM (Panache).
- **Frontend:** Realizzato in **Angular**. È una Single Page Application (SPA) pensata per aggiornarsi in tempo reale senza far "congelare" la pagina durante i rendering.
- **Gestione Asincrona (Il cuore dell'ingestione):** La vera sfida era non perdere dati. Ho separato il momento in cui il dato arriva dall'API e il momento in cui viene analizzato tramite un pattern **Producer-Consumer**. Le API REST inseriscono semplicemente il dato in una coda in memoria, e un worker thread in background lo preleva per l'elaborazione. Questo mi assicura che il server HTTP non si blocchi mai, neanche sotto un "burst" di dati.

### Design Pattern utilizzati nel codice

Durante lo sviluppo ho cercato di applicare i principi di buona progettazione. Ecco i pattern principali che ho inserito:

- **Facade (`IoTSentinelFacade`):** L'ho usato per nascondere la complessità della business logic ai controller REST. I controller chiamano solo la Facade, che poi si occupa di orchestrare il salvataggio dei dati, l'inserimento in coda e la gestione degli observer.
- **Strategy (`IStrategiaAnalisi` / `IRegola`):** Questo pattern è stato fondamentale per il motore di analisi. Invece di avere un enorme blocco `if/else`, ho definito un'interfaccia. In questo modo il motore può iterare e valutare indiscriminatamente una `RegolaSoglia` o una `RegolaTemporale`, rendendo facilissimo aggiungere nuovi tipi di regole in futuro.
- **Observer (`IAlarmObserver`):** L'ho implementato per "avvisare" in automatico le varie parti del sistema quando scatta un allarme o arriva un nuovo dato, disaccoppiando chi genera l'evento da chi deve reagire (ad esempio per l'invio via WebSocket alla dashboard).
- **DAO (Data Access Object):** Ho creato interfacce come `IDAOSensore` per isolare le operazioni sul database dal resto della logica, in modo da non sporcare i servizi o il facade con codice specifico di Panache/Hibernate.
- **Template Method & Ereditarietà:** Per modellare i sensori, ho creato una classe base astratta `SensoreBase` che contiene i campi e i comportamenti comuni, da cui ho derivato i sensori specifici (es. `SensoreTemperatura`, `SensoreVibrazione`).
