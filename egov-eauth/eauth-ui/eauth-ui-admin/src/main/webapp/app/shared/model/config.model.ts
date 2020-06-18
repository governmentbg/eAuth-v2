export interface IConfig {
  propertyKey?: string;
  propertyValue?: string;
}

export class Config implements IConfig {
  constructor(public propertyKey?: string, public propertyValue?: string) {}
}
