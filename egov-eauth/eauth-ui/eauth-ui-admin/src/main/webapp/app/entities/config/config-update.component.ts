import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IConfig, Config } from 'app/shared/model/config.model';
import { ConfigService } from './config.service';

@Component({
  selector: 'jhi-config-update',
  templateUrl: './config-update.component.html'
})
export class ConfigUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    propertyKey: [],
    propertyValue: []
  });

  constructor(protected configService: ConfigService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ config }) => {
      this.updateForm(config);
    });
  }

  updateForm(config: IConfig) {
    this.editForm.patchValue({
      propertyKey: config.propertyKey,
      propertyValue: config.propertyValue
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const config = this.createFromForm();
    if (config.propertyKey !== undefined) {
      this.subscribeToSaveResponse(this.configService.update(config));
    } else {
      this.subscribeToSaveResponse(this.configService.create(config));
    }
  }

  private createFromForm(): IConfig {
    return {
      ...new Config(),
      propertyKey: this.editForm.get(['propertyKey']).value,
      propertyValue: this.editForm.get(['propertyValue']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConfig>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
