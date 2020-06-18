import { Address } from './address';

export class User {
    id: string;
    names: string;
    egn: string;
    email: string;
    preferred2FA: string;
    phoneNumber: string;
    address: Address;
}