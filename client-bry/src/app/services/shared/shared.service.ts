import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  constructor() { }

  createImage(reader: FileReader): HTMLImageElement {
    let blob = new Blob([reader.result as ArrayBuffer]);
    window.URL = window.URL || window.webkitURL;
    let blobURL = window.URL.createObjectURL(blob);

    let image = new Image();
    image.src = blobURL;
    return image;
  }

  compressImage(image: HTMLImageElement): string{
    const canvas = document.createElement('canvas');

    const MAX_WIDTH = 800;
    const MAX_HEIGHT = 600;

    let width = image.width;
    let height = image.height;

    if (width > height) {
      if (width > MAX_WIDTH) {
        height = Math.round(height * MAX_WIDTH / width);
        width = MAX_WIDTH;
      }
    } else {
      if (height > MAX_HEIGHT) {
        width = Math.round(width * MAX_HEIGHT / height);
        height = MAX_HEIGHT;
      }
    }

    canvas.width = width;
    canvas.height = height;
    const context = canvas.getContext('2d');
    context!.drawImage(image, 0, 0, width, height);

    return canvas.toDataURL('image/jpeg', 0.7);
  }
}
