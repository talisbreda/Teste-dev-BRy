import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-custom-button',
  standalone: true,
  imports: [],
  templateUrl: './custom-button.component.html',
  styleUrl: './custom-button.component.css'
})
export class CustomButtonComponent {
  @Input() text: string = '';
  @Input() color: string = 'cadetblue';
  @Input() disabled: boolean = false;

  constructor() { }

}
