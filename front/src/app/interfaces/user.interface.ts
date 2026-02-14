export interface User {
  id: number;
  lastName: string | null;
  firstName: string | null;
  admin: boolean;
  email: string | null;
  password: string | null;
  createdAt: string;
  updatedAt: string;
}
