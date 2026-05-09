import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  AlarmStatsResponse,
  AllarmeResponse,
  CorrelationRuleCreateRequest,
  MisurazioneResponse,
  RegolaResponse,
  SensorCreateRequest,
  SensoreResponse,
  TelemetryCreateRequest,
  TemporalRuleCreateRequest,
  ThresholdRuleCreateRequest,
} from '../models/sentinel.models';

@Injectable({ providedIn: 'root' })
export class SentinelApiService {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = 'http://localhost:8080';

  getSensori(page = 0, size = 50, seriale?: string) {
    const params: any = { page, size };
    if (seriale) params.seriale = seriale;
    return this.http.get<SensoreResponse[]>(`${this.apiBaseUrl}/sensori`, { params });
  }

  createSensore(request: SensorCreateRequest) {
    return this.http.post<SensoreResponse>(`${this.apiBaseUrl}/sensori`, request);
  }

  getTelemetrie(page = 0, size = 50) {
    return this.http.get<MisurazioneResponse[]>(`${this.apiBaseUrl}/telemetrie`, {
      params: { page, size }
    });
  }

  createTelemetria(request: TelemetryCreateRequest) {
    return this.http.post<MisurazioneResponse>(`${this.apiBaseUrl}/telemetrie`, request);
  }

  getAllarmiAttivi(page = 0, size = 50) {
    return this.http.get<AllarmeResponse[]>(`${this.apiBaseUrl}/allarmi/attivi`, {
      params: { page, size }
    });
  }

  getAlarmStats() {
    return this.http.get<AlarmStatsResponse>(`${this.apiBaseUrl}/allarmi/stats`);
  }

  getRegole(page = 0, size = 50, nome?: string) {
    const params: any = { page, size };
    if (nome) params.nome = nome;
    return this.http.get<RegolaResponse[]>(`${this.apiBaseUrl}/regole`, { params });
  }

  createRegolaSoglia(request: ThresholdRuleCreateRequest) {
    return this.http.post<RegolaResponse>(`${this.apiBaseUrl}/regole/soglia`, request);
  }

  createRegolaTemporale(request: TemporalRuleCreateRequest) {
    return this.http.post<RegolaResponse>(`${this.apiBaseUrl}/regole/temporale`, request);
  }

  createRegolaCorrelazione(request: CorrelationRuleCreateRequest) {
    return this.http.post<RegolaResponse>(`${this.apiBaseUrl}/regole/correlazione`, request);
  }

  removeRegola(index: number) {
    return this.http.delete<void>(`${this.apiBaseUrl}/regole/${index}`);
  }
}
