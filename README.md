# GeoWatch Android App

App Android che mostra l'ora in tempo reale e le coordinate GPS.

---

## Come compilare l'APK su GitHub (gratis, senza installare nulla)

### Passo 1 — Crea il repository su GitHub

1. Vai su [github.com](https://github.com) e accedi
2. Clicca **"New repository"** (tasto verde in alto a destra)
3. Dai un nome: `geowatch-android`
4. Lascia tutto il resto come predefinito
5. Clicca **"Create repository"**

---

### Passo 2 — Carica i file

1. Nel tuo nuovo repository, clicca **"uploading an existing file"**
2. **Trascina TUTTA la cartella** `geowatch-android` nella pagina
   - oppure clicca "choose your files" e seleziona tutti i file
3. Clicca **"Commit changes"**

> ⚠️ **Importante**: mantieni la struttura delle cartelle! Il file `.github/workflows/build.yml` deve stare nella cartella giusta.

---

### Passo 3 — La compilazione parte automaticamente

Appena carichi i file, GitHub Actions avvia automaticamente la compilazione.

1. Clicca sulla scheda **"Actions"** del tuo repository
2. Vedrai un job in esecuzione chiamato **"Build APK"**
3. Aspetta 3-5 minuti che finisca (pallino arancione = in corso, verde = completato)

---

### Passo 4 — Scarica l'APK

1. Clicca sul job completato (spunta verde)
2. In fondo alla pagina trovi la sezione **"Artifacts"**
3. Clicca **"GeoWatch-APK"** per scaricare lo zip
4. Estrai lo zip → trovi `app-debug.apk`

---

### Passo 5 — Installa sul telefono

1. Copia `app-debug.apk` sul telefono (via USB, email, Drive...)
2. Sul telefono: **Impostazioni → Sicurezza → Sorgenti sconosciute** → Attiva
3. Apri il file APK e clicca **Installa**
4. Al primo avvio, concedi il permesso di localizzazione

---

## Funzionalità

- ⏰ Orologio con ore, minuti, secondi (cifre a larghezza fissa, nessuno spostamento)
- 📅 Data in italiano (es. "Venerdì 28 Febbraio 2025")
- 📍 Latitudine e longitudine con 6 decimali
- 🏔️ Altitudine in metri
- 📶 Precisione del segnale GPS in metri
- 🔄 Pulsante aggiorna posizione

## Design

Tema scuro con palette ciano/arancione, ispirato all'interfaccia PWA.
