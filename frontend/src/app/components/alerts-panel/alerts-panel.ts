import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';

import { AllarmeResponse } from '../../models/sentinel.models';
import { formatTime, statusClass } from '../../shared/formatters';

@Component({
  selector: 'app-alerts-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alerts-panel.html',
  styleUrl: './alerts-panel.scss',
})
export class AlertsPanel {
  allarmi = input.required<AllarmeResponse[]>();

  readonly formatTime = formatTime;
  readonly statusClass = statusClass;
}
