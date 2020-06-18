export class Ekatte {
    id: string;
    type: string;
    place: string; 
    municipality: string;
    district: string;
    fullName: string;

    constructor(id: string, type: string, place: string, municipality: string, district: string, fullName: string) {
        this.id = id;
        this.type = type;
        this.place = place;
        this.municipality = municipality;
        this.district = district;
        this.fullName = fullName;
    }
}