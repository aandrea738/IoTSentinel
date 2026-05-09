import { CommonModule } from '@angular/common';
import { Component, input, output, signal } from '@angular/core';

import { AllarmeResponse, MisurazioneResponse, SensoreResponse } from '../../models/sentinel.models';
import { formatTime, statusClass } from '../../shared/formatters';

@Component({
  selector: 'app-telemetry-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './telemetry-panel.html',
})
export class TelemetryPanel {
  sensori = input.required<SensoreResponse[]>();
  telemetrie = input.required<MisurazioneResponse[]>();
  allarmi = input.required<AllarmeResponse[]>();

  page = input<number>(0);
  size = input<number>(50);
  pageChange = output<number>();

  alarmPage = input<number>(0);
  alarmPageChange = output<number>();

  activeTab = signal<'TELEMETRIA' | 'ALLARMI'>('TELEMETRIA');

  readonly formatTime = formatTime;
  readonly statusClass = statusClass;

  sensorName(id: number): string {
    const sensore = this.sensori().find((item) => item.id === id);
    return sensore ? (sensore.seriale || `#${sensore.id}`) : `ID: ${id}`;
  }

  alarmSensors(idSensori: number[]): string {
    if (!idSensori || idSensori.length === 0) return '-';
    return idSensori.map(id => this.sensorName(id)).join(', ');
  }

  nextPage(): void {
    if (this.activeTab() === 'TELEMETRIA') {
      this.pageChange.emit(this.page() + 1);
    } else {
      this.alarmPageChange.emit(this.alarmPage() + 1);
    }
  }

  prevPage(): void {
    if (this.activeTab() === 'TELEMETRIA') {
      if (this.page() > 0) {
        this.pageChange.emit(this.page() - 1);
      }
    } else {
      if (this.alarmPage() > 0) {
        this.alarmPageChange.emit(this.alarmPage() - 1);
      }
    }
  }
}
