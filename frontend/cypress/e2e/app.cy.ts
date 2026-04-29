describe('Hybrid SCM Hub E2E Tests', () => {
  beforeEach(() => {
    // Assuming the backend is running on port 8080
    cy.visit('/login');
    cy.get('input[name="username"]').type('admin');
    cy.get('input[name="password"]').type('password');
    cy.get('button[type="submit"]').click();
    
    // Wait for navigation to dashboard
    cy.url().should('not.include', '/login');
    cy.contains('Hybrid-Cloud SCM', { timeout: 10000 }).should('be.visible');
  });

  it('should load the application', () => {
    // Already verified in beforeEach, but here for completeness
    cy.contains('Hybrid-Cloud SCM').should('be.visible');
  });

  it('should navigate to inventory management', () => {
    cy.contains('Inventory').click();
    cy.url().should('include', '/inventory');
    cy.contains('Inventory Management').should('be.visible');
  });

  it('should create a product', () => {
    const sku = `TEST-${Date.now()}`;
    cy.contains('Inventory').click();
    cy.contains('h3', 'Add Product').parent().within(() => {
      cy.get('input[name="sku"]').type(sku);
      cy.get('input[name="name"]').type('Test Product');
      cy.get('input[name="description"]').type('Test Description');
      cy.get('input[name="basePrice"]').clear().type('10.99');
      cy.get('button[type="submit"]').contains('Add Product').click();
    });
    cy.contains(sku).should('be.visible');
  });

  it('should create a warehouse', () => {
    const name = `Warehouse-${Date.now()}`;
    cy.contains('Inventory').click();
    cy.contains('h3', 'Add Warehouse').parent().within(() => {
      cy.get('input[name="name"]').type(name);
      cy.get('input[name="location"]').type('Test Location');
      cy.get('button[type="submit"]').contains('Add Warehouse').click();
    });
    cy.contains(name).should('be.visible');
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