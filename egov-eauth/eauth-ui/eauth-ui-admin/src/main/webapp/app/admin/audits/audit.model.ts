import { AuditData } from './audit-data.model';

export class Audit {
         constructor(
           public data: AuditData,
           public principal: string,
           public auditEventDate: string,
           public auditEventType: string,
           public auditOrigin: string,
           public clientIP: string
         ) {}
       }
