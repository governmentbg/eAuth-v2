/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { EauthAdminTestModule } from '../../../test.module';
import { ConfigUpdateComponent } from 'app/entities/config/config-update.component';
import { ConfigService } from 'app/entities/config/config.service';
import { Config } from 'app/shared/model/config.model';

describe('Component Tests', () => {
  describe('Config Management Update Component', () => {
    let comp: ConfigUpdateComponent;
    let fixture: ComponentFixture<ConfigUpdateComponent>;
    let service: ConfigService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EauthAdminTestModule],
        declarations: [ConfigUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ConfigUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ConfigUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Config('123');
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Config();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
