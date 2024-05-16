import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import axios from 'axios';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-screen',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './list-screen.component.html',
  styleUrl: './list-screen.component.css'
})
export class ListScreenComponent {
  users: { id: number, name: string, cpf: string }[]
  filteredUsers: { id: number, name: string, cpf: string }[]
  searchText: any;
  searching: boolean = false;
  searchResult: string = '';

  constructor(private router: Router) {
    this.users = []
    this.filteredUsers = []
  }

  ngOnInit() {
    axios.get("http://localhost:8080/users").then((response) => {
      this.users = response.data;
      this.filteredUsers = this.users;
    });
  }

  searchUser(): void {
    const cpf = this.searchText;
    if (!cpf) return;
    this.searching = true;
    axios.get(`http://localhost:8080/user/cpf/${cpf}`).then((response) => {
      this.filteredUsers = [response.data];
      this.searchResult = '';
    }).catch(() => {
      this.filteredUsers = [];
      this.searchResult = 'No users found.';
    });
  }

  searchLocally(): void {
    const name: string = this.searchText;
    this.searching = false;
    if (name.match(/[a-z]/i)) {
      this.filteredUsers = this.users.filter(user => user.name.toLowerCase().includes(name.toLowerCase()));
    } else {
      this.filteredUsers = this.users;
    }
  }

  detailUser(id: number): void {
    this.router.navigate([`user/${id}`])
  }
}
