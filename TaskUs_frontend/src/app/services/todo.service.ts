import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Todo } from '../models/todo';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  private apiUrl = 'http://localhost:8080/api/todos';
  private todosSubject = new BehaviorSubject<Todo[]>([]);
  public todos$ = this.todosSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  loadAllTodos(): void {
    this.getAllTodos().subscribe(todos => {
      this.todosSubject.next(todos);
    });
  }

  getAllTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(this.apiUrl, {
      headers: this.getHeaders()
    });
  }

  getCompletedTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(`${this.apiUrl}?completed=true`, {
      headers: this.getHeaders()
    });
  }

  getIncompleteTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(`${this.apiUrl}?completed=false`, {
      headers: this.getHeaders()
    });
  }

  getTodoById(id: number): Observable<Todo> {
    return this.http.get<Todo>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  createTodo(todo: Partial<Todo>): Observable<Todo> {
    return this.http.post<Todo>(this.apiUrl, todo, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => this.loadAllTodos())
    );
  }

  updateTodo(id: number, todo: Partial<Todo>): Observable<Todo> {
    return this.http.patch<Todo>(`${this.apiUrl}/${id}`, todo, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => this.loadAllTodos())
    );
  }

  toggleTodo(id: number): Observable<Todo> {
    return this.http.patch<Todo>(`${this.apiUrl}/${id}/toggle`, {}, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => this.loadAllTodos())
    );
  }

  deleteTodo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => this.loadAllTodos())
    );
  }

  getTodoCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`, {
      headers: this.getHeaders()
    });
  }
}