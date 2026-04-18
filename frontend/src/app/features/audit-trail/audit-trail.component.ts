import { Component, OnInit } from '@angular/core';
import { RealtimeService } from '../../core/services/realtime.service';

@Component({
  selector: 'app-audit-trail',
  templateUrl: './audit-trail.component.html',
  styleUrls: ['./audit-trail.component.scss']
})
export class AuditTrailComponent implements OnInit {
  auditLogs: string[] = [];

  constructor(private realtimeService: RealtimeService) {}

  ngOnInit(): void {
    this.realtimeService.getUpdates().subscribe(update => {
      this.auditLogs.push(update);
    });
  }
}