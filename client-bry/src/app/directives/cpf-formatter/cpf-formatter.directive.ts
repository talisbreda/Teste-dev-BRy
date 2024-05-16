// cpf-formatter.directive.ts
import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appCpfFormatter]',
  standalone: true
})
export class CpfFormatterDirective {

  constructor(private el: ElementRef) { }

  @HostListener('input', ['$event']) onInputChange(event: any): void {
    const input = this.el.nativeElement;
    let value = input.value.replace(/\D/g, ''); // Remove all non-digit characters

    if (value.length > 11) {
      value = value.substring(0, 11); // Limit to 11 digits
    }

    // Apply CPF formatting
    value = value.replace(/(\d{3})(\d)/, '$1.$2');
    value = value.replace(/(\d{3})(\d)/, '$1.$2');
    value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');

    input.value = value;
  }

  @HostListener('blur', ['$event']) onBlur(event: any): void {
    const input = this.el.nativeElement;
    let value = input.value.replace(/\D/g, ''); // Remove all non-digit characters

    if (value.length !== 11) {
      input.value = ''; // Clear the input if the length is not 11 digits
    }
  }
}
