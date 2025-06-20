import {ApplicationRef, ComponentFactoryResolver, ComponentRef, Injectable, Injector} from '@angular/core';
import {Alert} from './alert/alert';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor(
    private resolver: ComponentFactoryResolver,
    private injector: Injector,
    private appRef: ApplicationRef
  ) {}

  showToast(message: string, type: 'success' | 'danger' | 'warning' | 'info', timeTillDestroy: number = 3000) {
    const factory = this.resolver.resolveComponentFactory(Alert);
    const componentRef: ComponentRef<Alert> = factory.create(this.injector);

    componentRef.instance.message = message;
    componentRef.instance.type = type;

    this.appRef.attachView(componentRef.hostView);
    const toastElement = (componentRef.hostView as any).rootNodes[0];

    document.body.appendChild(toastElement);

    setTimeout(() => {
      this.appRef.detachView(componentRef.hostView);
      componentRef.destroy();
    }, timeTillDestroy);
  }

}
