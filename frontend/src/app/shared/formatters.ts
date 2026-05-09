import { AlarmState, SensorState, Severity } from '../models/sentinel.models';

export function formatTime(value?: string): string {
  if (!value) {
    return '-';
  }
  return new Intl.DateTimeFormat('it-IT', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).format(new Date(value));
}

export function statusClass(stato: SensorState | AlarmState | Severity): string {
  return stato.toLowerCase();
}
