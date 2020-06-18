import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConfig } from 'app/shared/model/config.model';

@Component({
  selector: 'jhi-config-detail',
  templateUrl: './config-detail.component.html'
})
export class ConfigDetailComponent implements OnInit {
  config: IConfig;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ config }) => {
      this.config = config;
    });
  }

  previousState() {
    window.history.back();
  }
}
