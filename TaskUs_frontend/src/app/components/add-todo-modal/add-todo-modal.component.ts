import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-todo-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-todo-modal.component.html',
  styleUrls: ['./add-todo-modal.component.css']
})
export class AddTodoModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() addTodo = new EventEmitter<{title: string, description: string}>();

  title = '';
  description = '';

  onSubmit(): void {
    if (this.title.trim()) {
      this.addTodo.emit({
        title: this.title,
        description: this.description
      });
      this.title = '';
      this.description = '';
    }
  }

  onClose(): void {
    this.close.emit();
  }
}