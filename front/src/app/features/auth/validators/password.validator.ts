import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class PasswordValidator {
  static strength(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value) {
        return null; // Do not validate if empty (required validator will handle this)
      }

      const errors: ValidationErrors = {};

      // At least 8 characters
      if (value.length < 8) {
        errors['minLength'] = { requiredLength: 8, actualLength: value.length };
      }

      // At least one digit
      if (!/\d/.test(value)) {
        errors['digit'] = true;
      }

      // At least one lowercase letter
      if (!/[a-z]/.test(value)) {
        errors['lowercase'] = true;
      }

      // At least one uppercase letter
      if (!/[A-Z]/.test(value)) {
        errors['uppercase'] = true;
      }

      // At least one special character
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)) {
        errors['special'] = true;
      }

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }
}
