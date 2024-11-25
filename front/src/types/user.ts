export interface User {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  phone?: string;
  dni?: number;
  user_id?: number;
}

export interface UserAccount {
  balance: number;
  cvu: string;
  alias: string;
  user_id: number;
  id: string;
  name: string;
}
