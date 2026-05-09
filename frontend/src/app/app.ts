import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { interval } from 'rxjs';

import { KpiStrip } from './components/kpi-strip/kpi-strip';
import { RulesPanel } from './components/rules-panel/rules-panel';
import { SensorsPanel } from './components/sensors-panel/sensors-panel';
import { SentinelHeader } from './components/sentinel-header/sentinel-header';
import { TelemetryPanel } from './components/telemetry-panel/telemetry-panel';
import {
  AllarmeResponse,
  CorrelationRuleCreateRequest,
  MisurazioneResponse,
  RegolaResponse,
  SensoreResponse,
  SensorType,
  TemporalRuleCreateRequest,
  ThresholdRuleCreateRequest,
} from './models/sentinel.models';
import { SentinelApiService } from './services/sentinel-api.service';
import { formatTime } from './shared/formatters';

@Component({
  selector: 'app-root',
  imports: [SentinelHeader, KpiStrip, SensorsPanel, TelemetryPanel, RulesPanel],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly api = inject(SentinelApiService);
  private readonly destroyRef = inject(DestroyRef);

  readonly sensori = signal<SensoreResponse[]>([]);
  readonly sensoriLookup = signal<SensoreResponse[]>([]);
  readonly telemetrie = signal<MisurazioneResponse[]>([]);
  readonly allarmi = signal<AllarmeResponse[]>([]);
  readonly allarmiStats = signal<Record<string, number>>({});
  readonly regole = signal<RegolaResponse[]>([]);
  readonly ultimaTelemetria = signal<MisurazioneResponse | null>(null);
  readonly loading = signal(false);
  readonly backendOnline = signal(true);
  readonly lastError = signal<string | null>(null);
  readonly selectedSensorId = signal<number | null>(null);
  readonly telemetryPage = signal(0);
  readonly telemetryPageSize = 15;
  readonly alarmPage = signal(0);
  readonly alarmPageSize = 15;
  readonly rulePage = signal(0);
  readonly rulePageSize = 10;
  readonly ruleSearch = signal('');
  readonly sensorPage = signal(0);
  readonly sensorPageSize = 8;
  readonly sensorSearch = signal('');

  readonly sensoriAttivi = computed(
    () => this.sensoriLookup().filter((sensore) => sensore.stato === 'ATTIVO').length,
  );
  readonly allarmiAlta = computed(() => this.allarmiStats()['ALTA'] || 0);
  readonly allarmiMedia = computed(() => this.allarmiStats()['MEDIA'] || 0);
  readonly allarmiBassa = computed(() => this.allarmiStats()['BASSA'] || 0);
  readonly ultimoAggiornamento = computed(() => {
    const ultima = this.ultimaTelemetria();
    return ultima?.timestamp ? formatTime(ultima.timestamp) : 'In attesa';
  });

  constructor() {
    this.loadDashboard();

    interval(2000)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (this.backendOnline()) {
          if (this.telemetryPage() === 0) {
            this.loadTelemetrie(true);
          } else {
            this.loadUltimaTelemetria(true);
          }
          if (this.alarmPage() === 0) {
            this.loadAllarmi(true);
          }
          if (this.rulePage() === 0 && !this.ruleSearch()) {
            this.loadRegole(true);
          }
          if (this.sensorPage() === 0 && !this.sensorSearch()) {
            this.loadSensori(true);
          }
          this.loadAllSensori(true);
          this.loadAllarmiStats(true);
        }
      });
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.lastError.set(null);
    this.loadSensori();
    this.loadAllSensori();
    this.loadTelemetrie();
    this.loadUltimaTelemetria();
    this.loadAllarmi();
    this.loadAllarmiStats();
    this.loadRegole();
  }

  addSensore(data: { tipo: SensorType; seriale: string; unitaDiMisura: string }): void {
    this.api.createSensore({ ...data, stato: 'ATTIVO' }).subscribe({
      next: (sensore) => {
        this.backendOnline.set(true);
        this.sensori.update((sensori) => [...sensori, sensore]);
      },
      error: (err: HttpErrorResponse) => this.handleApiError('Sensore non creato.', err),
    });
  }

  addThresholdRule(request: ThresholdRuleCreateRequest): void {
    this.api.createRegolaSoglia(request).subscribe({
      next: (regola) => {
        this.backendOnline.set(true);
        this.regole.update((regole) => [...regole, regola]);
        this.loadRegole(true);
      },
      error: (err) => this.handleApiError('Regola non salvata.', err),
    });
  }

  addTemporalRule(request: TemporalRuleCreateRequest): void {
    this.api.createRegolaTemporale(request).subscribe({
      next: (regola) => {
        this.backendOnline.set(true);
        this.regole.update((regole) => [...regole, regola]);
        this.loadRegole(true);
      },
      error: (err) => this.handleApiError('Regola temporale non salvata.', err),
    });
  }

  addCorrelationRule(request: CorrelationRuleCreateRequest): void {
    this.api.createRegolaCorrelazione(request).subscribe({
      next: (regola) => {
        this.backendOnline.set(true);
        this.regole.update((regole) => [...regole, regola]);
        this.loadRegole(true);
      },
      error: (err) => this.handleApiError('Regola correlazione non salvata.', err),
    });
  }

  removeRule(index: number): void {
    this.api.removeRegola(index).subscribe({
      next: () => {
        this.backendOnline.set(true);
        this.loadRegole(true);
      },
      error: (err) => this.handleApiError('Impossibile rimuovere la regola.', err),
    });
  }

  private loadSensori(silent = false): void {
    this.api.getSensori(this.sensorPage(), this.sensorPageSize, this.sensorSearch()).subscribe({
      next: (sensori) => {
        this.backendOnline.set(true);
        this.sensori.set(sensori);
      },
      error: (err) => this.handleLoadError('Errore caricamento sensori.', err, silent),
    });
  }

  private loadAllSensori(silent = false): void {
    this.api.getSensori(0, 1000).subscribe({
      next: (sensori) => {
        this.backendOnline.set(true);
        this.sensoriLookup.set(sensori);
      },
      error: (err) => this.handleLoadError('Errore caricamento lookup sensori.', err, silent),
    });
  }

  private loadTelemetrie(silent = false): void {
    this.api.getTelemetrie(this.telemetryPage(), this.telemetryPageSize).subscribe({
      next: (telemetrie) => {
        this.backendOnline.set(true);
        this.telemetrie.set(telemetrie);
        if (this.telemetryPage() === 0) {
          this.ultimaTelemetria.set(telemetrie[0] || null);
        }
      },
      error: (err) => this.handleLoadError('Errore caricamento telemetrie.', err, silent),
    });
  }

  private loadUltimaTelemetria(silent = false): void {
    this.api.getTelemetrie(0, 1).subscribe({
      next: (telemetrie) => {
        this.backendOnline.set(true);
        this.ultimaTelemetria.set(telemetrie[0] || null);
      },
      error: (err) => this.handleLoadError('Errore caricamento ultima telemetria.', err, silent),
    });
  }

  onTelemetryPageChange(page: number): void {
    this.telemetryPage.set(page);
    this.loadTelemetrie();
  }

  onAlarmPageChange(page: number): void {
    this.alarmPage.set(page);
    this.loadAllarmi();
  }

  onRulePageChange(page: number): void {
    this.rulePage.set(page);
    this.loadRegole();
  }

  onRuleSearch(search: string): void {
    this.ruleSearch.set(search);
    this.rulePage.set(0);
    this.loadRegole();
  }

  onSensorPageChange(page: number): void {
    this.sensorPage.set(page);
    this.loadSensori();
  }

  onSensorSearch(search: string): void {
    this.sensorSearch.set(search);
    this.sensorPage.set(0);
    this.loadSensori();
  }

  private loadAllarmi(silent = false): void {
    this.api.getAllarmiAttivi(this.alarmPage(), this.alarmPageSize).subscribe({
      next: (allarmi) => {
        this.backendOnline.set(true);
        this.allarmi.set(allarmi);
        if (!silent) this.loading.set(false);
      },
      error: (err) => this.handleLoadError('Errore caricamento allarmi.', err, silent),
    });
  }

  private loadAllarmiStats(silent = false): void {
    this.api.getAlarmStats().subscribe({
      next: (stats) => {
        this.backendOnline.set(true);
        this.allarmiStats.set(stats.counts);
      },
      error: (err) => this.handleLoadError('Errore caricamento statistiche allarmi.', err, silent),
    });
  }

  private loadRegole(silent = false): void {
    this.api.getRegole(this.rulePage(), this.rulePageSize, this.ruleSearch()).subscribe({
      next: (regole) => {
        this.backendOnline.set(true);
        this.regole.set(regole);
      },
      error: (err) => this.handleLoadError('Errore caricamento regole.', err, silent),
    });
  }

  private handleApiError(context: string, err: HttpErrorResponse): void {
    const isOffline = err.status === 0;
    this.backendOnline.set(!isOffline);
    const message = isOffline
      ? `${context} Backend non raggiungibile (Offline).`
      : `${context} Errore server (${err.status}): ${err.error?.message || err.message}`;
    this.lastError.set(message);
  }

  private handleLoadError(context: string, err: HttpErrorResponse, silent = false): void {
    const isOffline = err.status === 0;
    this.backendOnline.set(!isOffline);
    if (!silent) {
      this.loading.set(false);
      const message = isOffline
        ? `${context} Backend non raggiungibile (Offline).`
        : `${context} Errore server (${err.status}): ${err.error?.message || err.message}`;
      this.lastError.set(message);
    }
  }
}
