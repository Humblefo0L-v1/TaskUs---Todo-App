import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTodoModal } from './add-todo-modal.component';

describe('AddTodoModal', () => {
  let component: AddTodoModal;
  let fixture: ComponentFixture<AddTodoModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddTodoModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddTodoModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
