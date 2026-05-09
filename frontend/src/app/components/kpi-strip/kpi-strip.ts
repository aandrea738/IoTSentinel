import { Component, input } from '@angular/core';

@Component({
  selector: 'app-kpi-strip',
  templateUrl: './kpi-strip.html',
  styleUrl: './kpi-strip.scss',
})
export class KpiStrip {
  sensoriAttivi = input.required<number>();
  allarmiAlta = input.required<number>();
  allarmiMedia = input.required<number>();
  allarmiBassa = input.required<number>();
  ultimoAggiornamento = input.required<string>();
}
