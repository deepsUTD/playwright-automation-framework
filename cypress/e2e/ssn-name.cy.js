describe('SSN form', () => {
  beforeEach(() => {
    cy.visit('/ssn_validation/index.html');
  });

  it('asserts the SSN field by its name attribute', () => {
    cy.get('input[name="ssn"]')
      .should('exist')
      .and('be.visible')
      .and('have.attr', 'name', 'ssn');
  });
});