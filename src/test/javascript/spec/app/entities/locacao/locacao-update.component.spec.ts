/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoUpdateComponent } from 'app/entities/locacao/locacao-update.component';
import { LocacaoService } from 'app/entities/locacao/locacao.service';
import { Locacao } from 'app/shared/model/locacao.model';

describe('Component Tests', () => {
    describe('Locacao Management Update Component', () => {
        let comp: LocacaoUpdateComponent;
        let fixture: ComponentFixture<LocacaoUpdateComponent>;
        let service: LocacaoService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoUpdateComponent]
            })
                .overrideTemplate(LocacaoUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(LocacaoUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LocacaoService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Locacao(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.locacao = entity;
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
                    const entity = new Locacao();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.locacao = entity;
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
