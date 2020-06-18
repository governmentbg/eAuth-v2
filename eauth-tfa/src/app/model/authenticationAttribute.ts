import {AttributeType} from './enums/attributeType';

export interface AuthenticationAttribute {
    id: string;
    label: object;
    mandatory: boolean;
    type: AttributeType;
}
