import { SharedService } from './../../services/shared/shared.service';
import { Component, Directive, ElementRef, HostListener } from '@angular/core';
import axios, { AxiosError, AxiosHeaders, AxiosResponse } from 'axios';
import { CustomButtonComponent } from '../../components/custom-button/custom-button.component';
import { CpfFormatterDirective } from '../../directives/cpf-formatter/cpf-formatter.directive';

@Component({
  selector: 'app-register-screen',
  standalone: true,
  imports: [CustomButtonComponent, CpfFormatterDirective],
  templateUrl: './register-screen.component.html',
  styleUrl: './register-screen.component.css'
})

export class RegisterScreenComponent {
  base64file: string = '';
  user = {
    name: '',
    cpf: '',
  };

  constructor(private sharedService: SharedService,) {}

  onFileSelected(event: Event): void {
    const fileInput: HTMLInputElement = event.target as HTMLInputElement;
    const file: File | null = fileInput.files ? fileInput.files[0] : null;

    if (file) {
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);

      reader.onload = () => {
        let image = this.sharedService.createImage(reader);
        image.onload = () => {
          this.base64file = this.sharedService.compressImage(image);
        }
      }
    }
  }

  onBlur(input: String) {
    if (input === 'name') {
      this.user.name = (document.querySelector('#name-input') as HTMLInputElement).value;
    } else if (input === 'cpf') {
      this.user.cpf = (document.querySelector('#cpf-input') as HTMLInputElement).value;
    }
    const returnMessageElement: HTMLParagraphElement | null = document.querySelector('.return-message');
    returnMessageElement!.innerText = '';
  }

  onSave(): void {
    if (this.user.name === '' || this.user.cpf === '') {
      const returnMessageElement: HTMLParagraphElement | null = document.querySelector('.return-message');
      returnMessageElement!.innerText = 'Please fill all fields!';
      returnMessageElement!.style.color = 'red';
      return;
    }

    if (this.base64file === '') {
      const returnMessageElement: HTMLParagraphElement | null = document.querySelector('.return-message');
      returnMessageElement!.innerText = 'Please select a photo!';
      returnMessageElement!.style.color = 'red';
      return;
    }

    const returnMessageElement: HTMLParagraphElement | null = document.querySelector('.return-message');

    const headers = new AxiosHeaders();
    const body = {
      name: this.user.name,
      cpf: this.user.cpf,
      photo: this.base64file
    };

    axios.post('http://localhost:8080/register', body, { headers }).then((response: AxiosResponse) => {
        if (response.status === 201) {
          returnMessageElement!.innerText = 'User successfully registered!';
          returnMessageElement!.style.color = 'green';
          this.resetFields();
        }
      }
    ).catch((error) => {
      console.log(error);
      returnMessageElement!.innerText = 'Error registering user: ' + error.response.data;
      returnMessageElement!.style.color = 'red';
    });
  }

  resetFields(): void {
    (document.querySelector('#name-input') as HTMLInputElement).value = '';
    (document.querySelector('#cpf-input') as HTMLInputElement).value = '';
    (document.querySelector('#photo-input') as HTMLInputElement).value = '';
    this.base64file = '';
    this.user.name = '';
    this.user.cpf = '';
  }
}
