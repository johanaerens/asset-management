import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('AssetHistory e2e test', () => {
  const assetHistoryPageUrl = '/asset-history';
  const assetHistoryPageUrlPattern = new RegExp('/asset-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const assetHistorySample = {};

  let assetHistory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/asset-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/asset-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/asset-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (assetHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/asset-histories/${assetHistory.id}`,
      }).then(() => {
        assetHistory = undefined;
      });
    }
  });

  it('AssetHistories menu should load AssetHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('asset-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AssetHistory').should('exist');
    cy.url().should('match', assetHistoryPageUrlPattern);
  });

  describe('AssetHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(assetHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AssetHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/asset-history/new$'));
        cy.getEntityCreateUpdateHeading('AssetHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assetHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/asset-histories',
          body: assetHistorySample,
        }).then(({ body }) => {
          assetHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/asset-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [assetHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(assetHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AssetHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('assetHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assetHistoryPageUrlPattern);
      });

      it('edit button click should load edit AssetHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AssetHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assetHistoryPageUrlPattern);
      });

      it('edit button click should load edit AssetHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AssetHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assetHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of AssetHistory', () => {
        cy.intercept('GET', '/api/asset-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('assetHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', assetHistoryPageUrlPattern);

        assetHistory = undefined;
      });
    });
  });

  describe('new AssetHistory page', () => {
    beforeEach(() => {
      cy.visit(`${assetHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AssetHistory');
    });

    it('should create an instance of AssetHistory', () => {
      cy.get(`[data-cy="startDate"]`).type('2024-11-09T22:22');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2024-11-09T22:22');

      cy.get(`[data-cy="endDate"]`).type('2024-11-10T10:54');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2024-11-10T10:54');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        assetHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', assetHistoryPageUrlPattern);
    });
  });
});
