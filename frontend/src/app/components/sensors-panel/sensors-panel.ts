import { CommonModule } from '@angular/common';
import { Component, input, output, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, takeUntil } from 'rxjs';

import { SENSOR_TYPES, SensoreResponse, SensorType, AllarmeResponse } from '../../models/sentinel.models';
import { statusClass } from '../../shared/formatters';

@Component({
  selector: 'app-sensors-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sensors-panel.html',
  styleUrl: './sensors-panel.scss',
})
export class SensorsPanel implements OnDestroy {
  sensori = input.required<SensoreResponse[]>();
  allarmi = input.required<AllarmeResponse[]>();
  selectedSensorId = input<number | null>(null);

  createSensor = output<{ tipo: SensorType; seriale: string; unitaDiMisura: string }>();
  selectSensor = output<number | undefined>();
  
  page = input<number>(0);
  size = input<number>(8);
  search = input<string>('');
  pageChange = output<number>();
  searchChange = output<string>();

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor() {
    this.searchSubject.pipe(
      debounceTime(400),
      takeUntil(this.destroy$)
    ).subscribe(value => {
      this.searchChange.emit(value);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  readonly sensorTypes = SENSOR_TYPES;
  selectedType: SensorType = 'TEMPERATURA';
  newSerial = '';
  newUnit = '';
  showAdd = false;
  readonly statusClass = statusClass;

  alarmCount(sensorId: number): number {
    return this.allarmi().filter(a => a.idSensori.includes(sensorId)).length;
  }

  submit(): void {
    if (this.newSerial.trim()) {
      this.createSensor.emit({ 
        tipo: this.selectedType, 
        seriale: this.newSerial.trim(),
        unitaDiMisura: this.newUnit.trim()
      });
      this.newSerial = '';
      this.newUnit = '';
    }
  }

  onSensorClick(id: number): void {
    this.selectSensor.emit(id);
  }

  nextPage(): void {
    this.pageChange.emit(this.page() + 1);
  }

  prevPage(): void {
    if (this.page() > 0) {
      this.pageChange.emit(this.page() - 1);
    }
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }
}
