/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { EauthAdminTestModule } from '../../../test.module';
import { ConfigDeleteDialogComponent } from 'app/entities/config/config-delete-dialog.component';
import { ConfigService } from 'app/entities/config/config.service';

describe('Component Tests', () => {
  describe('Config Management Delete Component', () => {
    let comp: ConfigDeleteDialogComponent;
    let fixture: ComponentFixture<ConfigDeleteDialogComponent>;
    let service: ConfigService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EauthAdminTestModule],
        declarations: [ConfigDeleteDialogComponent]
      })
        .overrideTemplate(ConfigDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ConfigDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete('123');
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
