import {QrCode} from './qrCode';

export class Transaction {
    tid: string;
    method: string;
    qrCode: QrCode;
    timestamp: number;
}
