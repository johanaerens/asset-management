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

describe('Employee e2e test', () => {
  const employeePageUrl = '/employee';
  const employeePageUrlPattern = new RegExp('/employee(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const employeeSample = {};

  let employee;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/employees+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/employees').as('postEntityRequest');
    cy.intercept('DELETE', '/api/employees/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (employee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/employees/${employee.id}`,
      }).then(() => {
        employee = undefined;
      });
    }
  });

  it('Employees menu should load Employees page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('employee');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Employee').should('exist');
    cy.url().should('match', employeePageUrlPattern);
  });

  describe('Employee page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(employeePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Employee page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/employee/new$'));
        cy.getEntityCreateUpdateHeading('Employee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', employeePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/employees',
          body: employeeSample,
        }).then(({ body }) => {
          employee = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/employees+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [employee],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(employeePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Employee page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('employee');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', employeePageUrlPattern);
      });

      it('edit button click should load edit Employee page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Employee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', employeePageUrlPattern);
      });

      it('edit button click should load edit Employee page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Employee');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', employeePageUrlPattern);
      });

      it('last delete button click should delete instance of Employee', () => {
        cy.intercept('GET', '/api/employees/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('employee').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', employeePageUrlPattern);

        employee = undefined;
      });
    });
  });

  describe('new Employee page', () => {
    beforeEach(() => {
      cy.visit(`${employeePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Employee');
    });

    it('should create an instance of Employee', () => {
      cy.get(`[data-cy="firstName"]`).type('Vicente');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Vicente');

      cy.get(`[data-cy="lastName"]`).type('Hammes');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Hammes');

      cy.get(`[data-cy="email"]`).type('Sammie69@gmail.com');
      cy.get(`[data-cy="email"]`).should('have.value', 'Sammie69@gmail.com');

      cy.get(`[data-cy="employeeNumber"]`).type('kissingly');
      cy.get(`[data-cy="employeeNumber"]`).should('have.value', 'kissingly');

      cy.get(`[data-cy="phoneNumber"]`).type('boo');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'boo');

      cy.get(`[data-cy="hireDate"]`).type('2024-11-10T00:31');
      cy.get(`[data-cy="hireDate"]`).blur();
      cy.get(`[data-cy="hireDate"]`).should('have.value', '2024-11-10T00:31');

      cy.get(`[data-cy="language"]`).select('FRENCH');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        employee = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', employeePageUrlPattern);
    });
  });
});
