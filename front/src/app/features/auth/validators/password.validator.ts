import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class PasswordValidator {
  static strength(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null; // Ne valide pas si vide (le required le fera)
      }

      const errors: ValidationErrors = {};

      // Au moins 8 caractères
      if (value.length < 8) {
        errors['minLength'] = { requiredLength: 8, actualLength: value.length };
      }

      // Au moins un chiffre
      if (!/\d/.test(value)) {
        errors['digit'] = true;
      }

      // Au moins une lettre minuscule
      if (!/[a-z]/.test(value)) {
        errors['lowercase'] = true;
      }

      // Au moins une lettre majuscule
      if (!/[A-Z]/.test(value)) {
        errors['uppercase'] = true;
      }

      // Au moins un caractère spécial
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)) {
        errors['special'] = true;
      }

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }
}
