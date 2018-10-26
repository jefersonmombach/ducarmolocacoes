/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoDeleteDialogComponent } from 'app/entities/locacao/locacao-delete-dialog.component';
import { LocacaoService } from 'app/entities/locacao/locacao.service';

describe('Component Tests', () => {
    describe('Locacao Management Delete Component', () => {
        let comp: LocacaoDeleteDialogComponent;
        let fixture: ComponentFixture<LocacaoDeleteDialogComponent>;
        let service: LocacaoService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoDeleteDialogComponent]
            })
                .overrideTemplate(LocacaoDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LocacaoDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LocacaoService);
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
