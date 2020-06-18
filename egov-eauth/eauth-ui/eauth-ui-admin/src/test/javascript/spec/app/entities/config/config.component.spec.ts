/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { EauthAdminTestModule } from '../../../test.module';
import { ConfigComponent } from 'app/entities/config/config.component';
import { ConfigService } from 'app/entities/config/config.service';
import { Config } from 'app/shared/model/config.model';

describe('Component Tests', () => {
  describe('Config Management Component', () => {
    let comp: ConfigComponent;
    let fixture: ComponentFixture<ConfigComponent>;
    let service: ConfigService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EauthAdminTestModule],
        declarations: [ConfigComponent],
        providers: []
      })
        .overrideTemplate(ConfigComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ConfigComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Config('123')],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.configs[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
