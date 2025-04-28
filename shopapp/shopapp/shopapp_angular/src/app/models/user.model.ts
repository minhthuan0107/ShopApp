export class User {
    id: number;
    fullname: string;
    phone_number: string;
    address: string;
    is_active: boolean;
    date_of_birth: string;
    facebook_account_id: number;
    google_account_id: number;
    role: { id: number; name: string };
  
    constructor(data: any) {
      this.id = data.id || 0;
      this.fullname = data.fullname || '';
      this.phone_number = data.phone_number || '';
      this.address = data.address || '';
      this.is_active = data.is_active || false;
      this.date_of_birth = data.date_of_birth || '';
      this.facebook_account_id = data.facebook_account_id || 0;
      this.google_account_id = data.google_account_id || 0;
      this.role = data.role || { id: 0, name: '' };
    }
  }
  