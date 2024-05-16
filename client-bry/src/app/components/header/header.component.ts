import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  title = 'My App';

  constructor(private router: Router) { }

  onRegisterClick(): void {
    this.router.navigate(['/register']);
  }

  onListClick(): void {
    this.router.navigate(['/list']);
  }
}
