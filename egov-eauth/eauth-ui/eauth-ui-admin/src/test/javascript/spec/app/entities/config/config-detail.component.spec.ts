/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EauthAdminTestModule } from '../../../test.module';
import { ConfigDetailComponent } from 'app/entities/config/config-detail.component';
import { Config } from 'app/shared/model/config.model';

describe('Component Tests', () => {
  describe('Config Management Detail Component', () => {
    let comp: ConfigDetailComponent;
    let fixture: ComponentFixture<ConfigDetailComponent>;
    const route = ({ data: of({ config: new Config('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EauthAdminTestModule],
        declarations: [ConfigDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ConfigDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ConfigDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.config).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
