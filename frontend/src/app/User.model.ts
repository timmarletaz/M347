export interface UserModel {
  firstname: string;
  lastname: string;
  email: string;
  authorities: string;
}

export interface LoginResponse {
  user: UserModel;
}
