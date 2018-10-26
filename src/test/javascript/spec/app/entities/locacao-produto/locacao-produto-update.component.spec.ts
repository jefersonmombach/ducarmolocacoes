/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoProdutoUpdateComponent } from 'app/entities/locacao-produto/locacao-produto-update.component';
import { LocacaoProdutoService } from 'app/entities/locacao-produto/locacao-produto.service';
import { LocacaoProduto } from 'app/shared/model/locacao-produto.model';

describe('Component Tests', () => {
    describe('LocacaoProduto Management Update Component', () => {
        let comp: LocacaoProdutoUpdateComponent;
        let fixture: ComponentFixture<LocacaoProdutoUpdateComponent>;
        let service: LocacaoProdutoService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoProdutoUpdateComponent]
            })
                .overrideTemplate(LocacaoProdutoUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(LocacaoProdutoUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LocacaoProdutoService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new LocacaoProduto(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.locacaoProduto = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new LocacaoProduto();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.locacaoProduto = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
