describe('Hybrid SCM Hub E2E Tests', () => {
  beforeEach(() => {
    // Assuming the backend is running on port 8080
    cy.visit('/');
  });

  it('should load the application', () => {
    cy.contains('Hybrid SCM Hub').should('be.visible');
  });

  it('should navigate to inventory management', () => {
    cy.contains('Inventory').click();
    cy.url().should('include', '/inventory');
    cy.contains('Inventory Management').should('be.visible');
  });

  it('should create a product', () => {
    cy.contains('Inventory').click();
    cy.get('input[name="sku"]').type('TEST001');
    cy.get('input[name="name"]').type('Test Product');
    cy.get('input[name="description"]').type('Test Description');
    cy.get('input[name="basePrice"]').type('10.99');
    cy.get('button[type="submit"]').contains('Add Product').click();
    cy.contains('TEST001').should('be.visible');
  });

  it('should create a warehouse', () => {
    cy.contains('Inventory').click();
    cy.get('input[name="name"]').type('Test Warehouse');
    cy.get('input[name="location"]').type('Test Location');
    cy.get('button[type="submit"]').contains('Add Warehouse').click();
    cy.contains('Test Warehouse').should('be.visible');
  });

  it('should navigate to orders', () => {
    cy.contains('Orders').click();
    cy.url().should('include', '/orders');
    cy.contains('Order Management').should('be.visible');
  });

  it('should navigate to audit trail', () => {
    cy.contains('Audit Trail').click();
    cy.url().should('include', '/audit-trail');
    cy.contains('Audit Trail').should('be.visible');
  });
});