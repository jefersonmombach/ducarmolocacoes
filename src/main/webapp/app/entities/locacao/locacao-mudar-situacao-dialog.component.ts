import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILocacao } from 'app/shared/model/locacao.model';
import { LocacaoService } from './locacao.service';

@Component({
    selector: 'jhi-locacao-mudar-situacao-dialog',
    templateUrl: './locacao-mudar-situacao-dialog.component.html'
})
export class LocacaoMudarSituacaoDialogComponent {
    locacao: ILocacao;
    situacaoNova: any;
    descricaoAcao: string;

    constructor(private locacaoService: LocacaoService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmMudarSituacao(locacao: ILocacao) {
        this.locacao.situacao = this.situacaoNova;
        this.locacaoService.update(this.locacao).subscribe(response => {
            // imprime quando for a situacao nova de contrato emitido
            if (this.situacaoNova === '1') {
                this.locacaoService.printContrato(response.body);
            }
            this.eventManager.broadcast({
                name: 'locacaoListModification',
                content: 'Updated an locacao'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-locacao-mudar-situacao-popup',
    template: ''
})
export class LocacaoMudarSituacaoPopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ locacao, situacaoNova }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(LocacaoMudarSituacaoDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.locacao = locacao;
                this.ngbModalRef.componentInstance.situacaoNova = situacaoNova;
                this.ngbModalRef.componentInstance.descricaoAcao = this.definirDescricaoAcao(situacaoNova);
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }

    private definirDescricaoAcao(situacaoNova: any) {
        let retorno: string;
        switch (situacaoNova) {
            case '1':
                retorno = 'Emitir contrato';
                break;
            case '2':
                retorno = 'Entregar trajes';
                break;
            case '3':
                retorno = 'Devolver trajes';
                break;
        }
        return retorno;
    }
}
