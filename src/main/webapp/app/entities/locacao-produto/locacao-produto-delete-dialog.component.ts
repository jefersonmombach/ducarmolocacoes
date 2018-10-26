import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';
import { LocacaoProdutoService } from './locacao-produto.service';

@Component({
    selector: 'jhi-locacao-produto-delete-dialog',
    templateUrl: './locacao-produto-delete-dialog.component.html'
})
export class LocacaoProdutoDeleteDialogComponent {
    locacaoProduto: ILocacaoProduto;

    constructor(
        private locacaoProdutoService: LocacaoProdutoService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.locacaoProdutoService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'locacaoProdutoListModification',
                content: 'Deleted an locacaoProduto'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-locacao-produto-delete-popup',
    template: ''
})
export class LocacaoProdutoDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ locacaoProduto }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(LocacaoProdutoDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.locacaoProduto = locacaoProduto;
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
}
