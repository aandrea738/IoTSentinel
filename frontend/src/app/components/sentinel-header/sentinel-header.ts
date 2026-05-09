import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-sentinel-header',
  templateUrl: './sentinel-header.html',
  styleUrl: './sentinel-header.scss',
})
export class SentinelHeader {
  backendOnline = input.required<boolean>();
  lastError = input<string | null>(null);
  refresh = output<void>();
}
