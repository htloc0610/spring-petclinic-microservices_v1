package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void testMap() {
        // Arrange
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("First", "Last", "Addr", "City", "1234567890");

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertEquals("First", result.getFirstName());
        assertEquals("Last", result.getLastName());
        assertEquals("Addr", result.getAddress());
        assertEquals("City", result.getCity());
        assertEquals("1234567890", result.getTelephone());
    }

    @Test
    void testMapWithNullValues() {
        // Arrange
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest(null, null, null, null, null);

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertEquals(null, result.getFirstName());
        assertEquals(null, result.getLastName());
        assertEquals(null, result.getAddress());
        assertEquals(null, result.getCity());
        assertEquals(null, result.getTelephone());
    }

    @Test
    void testMapWithEmptyStrings() {
        // Arrange
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("", "", "", "", "");

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        assertEquals("", result.getAddress());
        assertEquals("", result.getCity());
        assertEquals("", result.getTelephone());
    }

    @Test
    void testMapWithSpecialCharacters() {
        // Arrange
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("José", "O'Connor", "123 Main St. #4", "São Paulo", "+1-555-123-4567");

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertEquals("José", result.getFirstName());
        assertEquals("O'Connor", result.getLastName());
        assertEquals("123 Main St. #4", result.getAddress());
        assertEquals("São Paulo", result.getCity());
        assertEquals("+1-555-123-4567", result.getTelephone());
    }

    @Test
    void testMapWithVeryLongValues() {
        // Arrange
        Owner owner = new Owner();
        String longName = "A".repeat(1000);
        String longAddress = "B".repeat(1000);
        String longCity = "C".repeat(1000);
        String longPhone = "D".repeat(1000);
        
        OwnerRequest request = new OwnerRequest(longName, longName, longAddress, longCity, longPhone);

        // Act
        Owner result = mapper.map(owner, request);

        // Assert
        assertEquals(longName, result.getFirstName());
        assertEquals(longName, result.getLastName());
        assertEquals(longAddress, result.getAddress());
        assertEquals(longCity, result.getCity());
        assertEquals(longPhone, result.getTelephone());
    }

    @Test
    void testMapMultipleTimes() {
        // Arrange
        Owner owner1 = new Owner();
        Owner owner2 = new Owner();
        OwnerRequest request1 = new OwnerRequest("John", "Doe", "Addr1", "City1", "1111111111");
        OwnerRequest request2 = new OwnerRequest("Jane", "Smith", "Addr2", "City2", "2222222222");

        // Act
        Owner result1 = mapper.map(owner1, request1);
        Owner result2 = mapper.map(owner2, request2);

        // Assert
        assertEquals("John", result1.getFirstName());
        assertEquals("Jane", result2.getFirstName());
    }
}