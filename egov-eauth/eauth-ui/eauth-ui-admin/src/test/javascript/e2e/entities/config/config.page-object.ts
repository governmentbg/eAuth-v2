import { browser, ExpectedConditions, element, by, ElementFinder } from 'protractor';

export class ConfigComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-config div table .btn-danger'));
  title = element.all(by.css('jhi-config div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
    return this.title.getText();
  }
}

export class ConfigUpdatePage {
  pageTitle = element(by.id('jhi-config-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  propertyKeyInput = element(by.id('field_propertyKey'));
  propertyValueInput = element(by.id('field_propertyValue'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setPropertyKeyInput(propertyKey) {
    await this.propertyKeyInput.sendKeys(propertyKey);
  }

  async getPropertyKeyInput() {
    return await this.propertyKeyInput.getAttribute('value');
  }

  async setPropertyValueInput(propertyValue) {
    await this.propertyValueInput.sendKeys(propertyValue);
  }

  async getPropertyValueInput() {
    return await this.propertyValueInput.getAttribute('value');
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class ConfigDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-config-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-config'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
