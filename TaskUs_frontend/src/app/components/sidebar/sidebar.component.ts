import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  @Output() filterChange = new EventEmitter<string>();
  
  activeFilter = 'all';

  setFilter(filter: string): void {
    this.activeFilter = filter;
    this.filterChange.emit(filter);
  }
}