import { CommonModule } from '@angular/common';
import { Component, computed, input, output, signal, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, takeUntil } from 'rxjs';

import {
  CorrelationRuleCreateRequest,
  RULE_TYPES,
  RuleType,
  RegolaResponse,
  SENSOR_TYPES,
  SEVERITIES,
  SensorType,
  Severity,
  SensoreResponse,
  TemporalRuleCreateRequest,
  ThresholdRuleCreateRequest,
} from '../../models/sentinel.models';

@Component({
  selector: 'app-rules-panel',
  imports: [CommonModule, FormsModule],
  templateUrl: './rules-panel.html',
  styleUrl: './rules-panel.scss',
})
export class RulesPanel implements OnDestroy {
  regole = input.required<RegolaResponse[]>();
  sensori = input.required<SensoreResponse[]>();
  selectedSensorId = input<number | null>(null);
  createThresholdRule = output<ThresholdRuleCreateRequest>();
  createTemporalRule = output<TemporalRuleCreateRequest>();
  createCorrelationRule = output<CorrelationRuleCreateRequest>();
  removeRule = output<number>();
  
  page = input<number>(0);
  size = input<number>(10);
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

  readonly gravities = SEVERITIES;
  readonly ruleTypes = RULE_TYPES;
  readonly sensorTypes = SENSOR_TYPES;

  showForm = signal(false);
  selectedRuleType: RuleType = 'SOGLIA';
  nomeRegola = '';
  targetSensorIds = signal<number[]>([]);
  gravita: Severity = 'ALTA';

  // SOGLIA / TEMPORALE
  tipoMisurazione: SensorType = 'TEMPERATURA';
  soglia = 80;

  // TEMPORALE
  durataMinimaSecondi = 10;

  // CORRELAZIONE
  tipoRegolaA: RuleType = 'SOGLIA';
  tipoA: SensorType = 'TEMPERATURA';
  sogliaA = 80;
  durataA = 10;
  
  tipoRegolaB: RuleType = 'SOGLIA';
  tipoB: SensorType = 'PRESSIONE';
  sogliaB = 5;
  durataB = 10;
  
  finestraCorrelazioneSecondi = 30;

  editIndex: number | null = null;

  filteredSensors = computed(() => {
    if (this.selectedRuleType === 'SOGLIA' || this.selectedRuleType === 'TEMPORALE') {
      return this.sensori().filter((s) => s.tipo === this.tipoMisurazione);
    }
    return this.sensori();
  });

  filteredRegole = computed(() => {
    const sensorId = this.selectedSensorId();
    if (sensorId === null) return this.regole();
    return this.regole().filter((r) => (r.idSensori && r.idSensori.includes(sensorId)) || r.tipo === 'CORRELAZIONE');
  });

  toggleForm(): void {
    this.showForm.update((v) => !v);
    if (this.showForm()) {
      const current = this.selectedSensorId();
      if (current !== null) {
        this.targetSensorIds.set([current]);
      } else {
        this.targetSensorIds.set([]);
      }
    }
  }

  toggleSensor(id: number): void {
    this.targetSensorIds.update((ids) =>
      ids.includes(id) ? ids.filter((i) => i !== id) : [...ids, id],
    );
  }

  submit(): void {
    const ids = this.targetSensorIds();
    if (ids.length === 0 || !this.nomeRegola.trim()) return;

    if (this.editIndex !== null) {
      this.removeRule.emit(this.editIndex);
    }

    if (this.selectedRuleType === 'SOGLIA') {
      this.createThresholdRule.emit({
        tipo: 'SOGLIA',
        idSensori: ids,
        nome: this.nomeRegola,
        tipoMisurazione: this.tipoMisurazione,
        soglia: this.soglia,
        gravita: this.gravita,
      });
    } else if (this.selectedRuleType === 'TEMPORALE') {
      if (this.durataMinimaSecondi <= 0) return;
      this.createTemporalRule.emit({
        tipo: 'TEMPORALE',
        idSensori: ids,
        nome: this.nomeRegola,
        tipoMisurazione: this.tipoMisurazione,
        soglia: this.soglia,
        durataMinima: `PT${this.durataMinimaSecondi}S`,
        gravita: this.gravita,
      });
    } else if (this.selectedRuleType === 'CORRELAZIONE') {
      if (this.finestraCorrelazioneSecondi <= 0) return;
      this.createCorrelationRule.emit({
        tipo: 'CORRELAZIONE',
        idSensori: ids,
        nome: this.nomeRegola,
        regolaA: {
          tipo: this.tipoRegolaA,
          tipoMisurazione: this.tipoA,
          soglia: this.sogliaA,
          ...(this.tipoRegolaA === 'TEMPORALE' ? { durataMinima: `PT${this.durataA}S` } : {}),
        },
        regolaB: {
          tipo: this.tipoRegolaB,
          tipoMisurazione: this.tipoB,
          soglia: this.sogliaB,
          ...(this.tipoRegolaB === 'TEMPORALE' ? { durataMinima: `PT${this.durataB}S` } : {}),
        },
        finestraCorrelazione: `PT${this.finestraCorrelazioneSecondi}S`,
        gravita: this.gravita,
      });
    }

    this.resetForm();
  }

  private resetForm(): void {
    this.editIndex = null;
    this.nomeRegola = '';
    this.showForm.set(false);
    this.targetSensorIds.set([]);
  }

  remove(regola: RegolaResponse): void {
    if (confirm(`Delete rule "${regola.nome || regola.id}"?`)) {
      const globalIndex = this.regole().indexOf(regola);
      if (globalIndex >= 0) {
        this.removeRule.emit(globalIndex);
      }
    }
  }

  editRule(regola: RegolaResponse): void {
    const globalIndex = this.regole().indexOf(regola);
    this.editIndex = globalIndex;
    this.nomeRegola = regola.nome ?? '';
    this.targetSensorIds.set(regola.idSensori ?? []);
    this.selectedRuleType = (regola.tipo as RuleType) || 'SOGLIA';

    if (regola.tipoMisurazione) this.tipoMisurazione = regola.tipoMisurazione;
    if (regola.soglia != null) this.soglia = regola.soglia;
    if (regola.durataMinimaSecondi != null) this.durataMinimaSecondi = regola.durataMinimaSecondi;

    if (regola.regolaA) {
      this.tipoRegolaA = (regola.regolaA.tipo as RuleType) || 'SOGLIA';
      if (regola.regolaA.tipoMisurazione) this.tipoA = regola.regolaA.tipoMisurazione;
      if (regola.regolaA.soglia != null) this.sogliaA = regola.regolaA.soglia;
      if (regola.regolaA.durataMinimaSecondi != null) this.durataA = regola.regolaA.durataMinimaSecondi;
    }

    if (regola.regolaB) {
      this.tipoRegolaB = (regola.regolaB.tipo as RuleType) || 'SOGLIA';
      if (regola.regolaB.tipoMisurazione) this.tipoB = regola.regolaB.tipoMisurazione;
      if (regola.regolaB.soglia != null) this.sogliaB = regola.regolaB.soglia;
      if (regola.regolaB.durataMinimaSecondi != null) this.durataB = regola.regolaB.durataMinimaSecondi;
    }

    if (regola.finestraCorrelazioneSecondi != null)
      this.finestraCorrelazioneSecondi = regola.finestraCorrelazioneSecondi;

    this.gravita = regola.gravita;
    this.showForm.set(true);
  }

  cancelEdit(): void {
    this.resetForm();
  }

  onTipoChange(): void {
    this.targetSensorIds.set([]);
  }

  onRuleTypeChange(): void {
    this.targetSensorIds.set([]);
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
