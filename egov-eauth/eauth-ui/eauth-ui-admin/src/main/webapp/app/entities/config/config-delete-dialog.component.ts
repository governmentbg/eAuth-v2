import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IConfig } from 'app/shared/model/config.model';
import { ConfigService } from './config.service';

@Component({
  selector: 'jhi-config-delete-dialog',
  templateUrl: './config-delete-dialog.component.html'
})
export class ConfigDeleteDialogComponent {
  config: IConfig;

  constructor(protected configService: ConfigService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.configService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'configListModification',
        content: 'Deleted an config'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-config-delete-popup',
  template: ''
})
export class ConfigDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ config }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ConfigDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.config = config;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/config', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/config', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
