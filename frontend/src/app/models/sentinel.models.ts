export type SensorType = 'TEMPERATURA' | 'PRESSIONE' | 'VIBRAZIONE' | 'CO2';
export type SensorState = 'ATTIVO' | 'DISCONNESSO' | 'ERRORE';
export type AlarmState = 'ATTIVO' | 'REVOCATO' | 'RISOLTO';
export type Severity = 'BASSA' | 'MEDIA' | 'ALTA';

export interface SensoreResponse {
  id: number;
  seriale: string;
  unitaDiMisura?: string;
  tipo: SensorType;
  stato: SensorState;
}

export interface MisurazioneResponse {
  id: number;
  idSensore: number;
  valore: number;
  unitaDiMisura?: string;
  tipoMisurazione: SensorType;
  timestamp: string;
}

export interface AllarmeResponse {
  id: number;
  idSensori: number[];
  gravita: Severity;
  stato: AlarmState;
  timestampInizio: string;
  timestampFine?: string;
  descrizione: string;
  occorrenze: number;
}

export interface AlarmStatsResponse {
  counts: Record<Severity, number>;
}

export interface RegolaResponse {
  id: number;
  tipo: string;
  nome?: string;
  idSensori?: number[];
  tipoMisurazione?: SensorType;
  soglia?: number;
  regolaA?: RegolaResponse;
  regolaB?: RegolaResponse;
  durataMinimaSecondi?: number;
  finestraCorrelazioneSecondi?: number;
  gravita: Severity;
}

export interface SensorCreateRequest {
  seriale: string;
  unitaDiMisura: string;
  tipo: SensorType;
  stato: SensorState;
}

export interface TelemetryCreateRequest {
  idSensore: number;
  tipoMisurazione: SensorType;
  valore: number;
  timestamp: string;
}

export interface ThresholdRuleCreateRequest {
  tipo: 'SOGLIA';
  idSensori: number[];
  nome: string;
  tipoMisurazione: SensorType;
  soglia: number;
  gravita: Severity;
}

export interface TemporalRuleCreateRequest {
  tipo: 'TEMPORALE';
  idSensori: number[];
  nome: string;
  tipoMisurazione: SensorType;
  soglia: number;
  durataMinima: string; // ISO-8601 duration, e.g., PT10S
  gravita: Severity;
}

export type RuleType = 'SOGLIA' | 'TEMPORALE' | 'CORRELAZIONE';

export interface SubRuleRequest {
  tipo: RuleType;
  tipoMisurazione: SensorType;
  soglia: number;
  durataMinima?: string;
}

export interface CorrelationRuleCreateRequest {
  tipo: 'CORRELAZIONE';
  idSensori: number[];
  nome: string;
  regolaA: SubRuleRequest;
  regolaB: SubRuleRequest;
  finestraCorrelazione: string; // ISO-8601 duration, e.g., PT30S
  gravita: Severity;
}

export type BaseRuleCreateRequest = ThresholdRuleCreateRequest | TemporalRuleCreateRequest | CorrelationRuleCreateRequest;

export const SENSOR_TYPES: SensorType[] = ['TEMPERATURA', 'PRESSIONE', 'VIBRAZIONE', 'CO2'];
export const SEVERITIES: Severity[] = ['BASSA', 'MEDIA', 'ALTA'];
export const RULE_TYPES: RuleType[] = ['SOGLIA', 'TEMPORALE', 'CORRELAZIONE'];
