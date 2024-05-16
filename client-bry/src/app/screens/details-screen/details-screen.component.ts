import { Component, Inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import axios from 'axios';
import { CustomButtonComponent } from '../../components/custom-button/custom-button.component';
import { NgIf } from '@angular/common';
import { SharedService } from '../../services/shared/shared.service';
import { share } from 'rxjs';

@Component({
  selector: 'app-details-screen',
  standalone: true,
  imports: [CustomButtonComponent, NgIf],
  templateUrl: './details-screen.component.html',
  styleUrl: './details-screen.component.css'
})
export class DetailsScreenComponent {
  id: number = 0;
  editing: boolean = false;
  confirmDelete: boolean = false;
  titleText: string = 'User Details';
  base64file: string = '';
  user: {
    id: number,
    name: string,
    cpf: string,
    photo: string
  } = { id: 0, name: '', cpf: '', photo: ''};

  constructor(
    private route : ActivatedRoute,
    private sharedService: SharedService
  ){}

  ngOnInit(){
    this.route.params.subscribe(params => {
      this.id = params['id'];
    });
    axios.get('http://localhost:8080/user/id/' + this.id).then(response => {
      this.user = response.data;
      this.user.photo = 'data:image/png;base64,' + this.user.photo;
    });
  }

  onFileSelected(event: Event): void {
    const fileInput: HTMLInputElement = event.target as HTMLInputElement;
    const file: File | null = fileInput.files ? fileInput.files[0] : null;

    if (file) {
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);

      reader.onload = (event) => {
        let image = this.sharedService.createImage(reader);

        image.onload = () => {
          this.user.photo = this.sharedService.compressImage(image);
        }
      }
    }
  }

  goBack() {
    window.history.back();
  }

  startUpdate() {
    const nameInput: HTMLInputElement = document.querySelector('.name-input') as HTMLInputElement;
    nameInput.disabled = false;
    this.editing = true;
    this.titleText = 'Edit User';
  }

  cancelUpdate() {
    const nameInput: HTMLInputElement = document.querySelector('.name-input') as HTMLInputElement;
    nameInput.disabled = true;
    this.editing = false;
    this.titleText = 'User Details';
  }

  updateUser() {
    const nameInput: HTMLInputElement = document.querySelector('.name-input') as HTMLInputElement;
    this.user.name = nameInput.value;

    const feedbackMessageParagraph: HTMLParagraphElement = document.querySelector('.feedback-message') as HTMLParagraphElement;

    axios.put('http://localhost:8080/user/id/' + this.id, {
      name: this.user.name,
      photo: this.user.photo
    }).then(response => {
      feedbackMessageParagraph.innerHTML = 'User updated successfully!';
      feedbackMessageParagraph.style.color = 'green';
      this.editing = false;
      nameInput.disabled = true;
      this.titleText = 'User Details';
    }).catch(error => {
      feedbackMessageParagraph.innerHTML = 'Error updating user: ' + error.response.data;
      feedbackMessageParagraph.style.color = 'red';
    });
  }

  askDelete() {
    this.confirmDelete = true;
  }

  deleteUser() {
    axios.delete('http://localhost:8080/user/id/' + this.id).then(response => {
      window.history.back();
    });
  }

  cancelDelete() {
    this.confirmDelete = false;
  }
}
