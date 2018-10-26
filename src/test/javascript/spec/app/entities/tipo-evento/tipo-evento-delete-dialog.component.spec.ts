/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { TipoEventoDeleteDialogComponent } from 'app/entities/tipo-evento/tipo-evento-delete-dialog.component';
import { TipoEventoService } from 'app/entities/tipo-evento/tipo-evento.service';

describe('Component Tests', () => {
    describe('TipoEvento Management Delete Component', () => {
        let comp: TipoEventoDeleteDialogComponent;
        let fixture: ComponentFixture<TipoEventoDeleteDialogComponent>;
        let service: TipoEventoService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [TipoEventoDeleteDialogComponent]
            })
                .overrideTemplate(TipoEventoDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TipoEventoDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TipoEventoService);
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
                    comp.confirmDelete(123);
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
