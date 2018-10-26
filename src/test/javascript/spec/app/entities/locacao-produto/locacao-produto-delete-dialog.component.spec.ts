/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoProdutoDeleteDialogComponent } from 'app/entities/locacao-produto/locacao-produto-delete-dialog.component';
import { LocacaoProdutoService } from 'app/entities/locacao-produto/locacao-produto.service';

describe('Component Tests', () => {
    describe('LocacaoProduto Management Delete Component', () => {
        let comp: LocacaoProdutoDeleteDialogComponent;
        let fixture: ComponentFixture<LocacaoProdutoDeleteDialogComponent>;
        let service: LocacaoProdutoService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoProdutoDeleteDialogComponent]
            })
                .overrideTemplate(LocacaoProdutoDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LocacaoProdutoDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LocacaoProdutoService);
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
