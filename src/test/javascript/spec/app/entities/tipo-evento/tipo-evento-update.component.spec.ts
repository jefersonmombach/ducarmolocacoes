/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { TipoEventoUpdateComponent } from 'app/entities/tipo-evento/tipo-evento-update.component';
import { TipoEventoService } from 'app/entities/tipo-evento/tipo-evento.service';
import { TipoEvento } from 'app/shared/model/tipo-evento.model';

describe('Component Tests', () => {
    describe('TipoEvento Management Update Component', () => {
        let comp: TipoEventoUpdateComponent;
        let fixture: ComponentFixture<TipoEventoUpdateComponent>;
        let service: TipoEventoService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [TipoEventoUpdateComponent]
            })
                .overrideTemplate(TipoEventoUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(TipoEventoUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TipoEventoService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new TipoEvento(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.tipoEvento = entity;
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
                    const entity = new TipoEvento();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.tipoEvento = entity;
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
