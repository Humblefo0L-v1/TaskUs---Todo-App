import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TodoService } from '../../services/todo.service';
import { Todo } from '../../models/todo';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { AddTodoModalComponent } from '../add-todo-modal/add-todo-modal.component';

@Component({
  selector: 'app-todo-list',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    NavbarComponent, 
    SidebarComponent, 
    AddTodoModalComponent
  ],
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.css']
})
export class TodoListComponent implements OnInit {
  todos: Todo[] = [];
  filteredTodos: Todo[] = [];
  activeFilter = 'all';
  showAddModal = false;
  editingTodo: Todo | null = null;
  editTitle = '';
  editDescription = '';

  constructor(private todoService: TodoService) {}

  ngOnInit(): void {
    this.loadTodos();
  }

  loadTodos(): void {
    this.todoService.getAllTodos().subscribe({
      next: (todos) => {
        this.todos = todos;
        this.applyFilter();
      },
      error: (error) => {
        console.error('Error loading todos:', error);
      }
    });
  }

  applyFilter(): void {
    if (this.activeFilter === 'all') {
      this.filteredTodos = this.todos;
    } else if (this.activeFilter === 'active') {
      this.filteredTodos = this.todos.filter(t => !t.completed);
    } else if (this.activeFilter === 'completed') {
      this.filteredTodos = this.todos.filter(t => t.completed);
    }
  }

  onFilterChange(filter: string): void {
    this.activeFilter = filter;
    this.applyFilter();
  }

  openAddModal(): void {
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  addTodo(data: {title: string, description: string}): void {
  this.todoService.createTodo({
    title: data.title,
    description: data.description,
    completed: false
  }).subscribe({
    next: (newTodo) => {
      console.log('Todo created:', newTodo);
      
      // Manually add to the list immediately
      this.todos = [...this.todos, newTodo];
      this.applyFilter();
      
      // Also reload from server to be sure
      this.loadTodos();
      
      this.closeAddModal();
    },
    error: (error) => {
        console.error('Error creating todo:', error);
        alert('Error: ' + (error.error?.message || 'Failed to create todo'));
      }
    });
  }

  toggleTodo(todo: Todo): void {
    this.todoService.toggleTodo(todo.id).subscribe({
      next: () => {
        this.loadTodos();
      },
      error: (error) => {
        console.error('Error toggling todo:', error);
      }
    });
  }

  startEdit(todo: Todo): void {
    this.editingTodo = todo;
    this.editTitle = todo.title;
    this.editDescription = todo.description || '';
  }

  cancelEdit(): void {
    this.editingTodo = null;
    this.editTitle = '';
    this.editDescription = '';
  }

  saveEdit(): void {
    if (this.editingTodo && this.editTitle.trim()) {
      this.todoService.updateTodo(this.editingTodo.id, {
        title: this.editTitle,
        description: this.editDescription
      }).subscribe({
        next: () => {
          this.loadTodos();
          this.cancelEdit();
        },
        error: (error) => {
          console.error('Error updating todo:', error);
        }
      });
    }
  }

  deleteTodo(todo: Todo): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.todoService.deleteTodo(todo.id).subscribe({
        next: () => {
          this.loadTodos();
        },
        error: (error) => {
          console.error('Error deleting todo:', error);
        }
      });
    }
  }
}