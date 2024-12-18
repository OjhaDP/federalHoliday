package com.federal.holidays.controller;

import com.federal.holidays.entity.Country;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.service.CountryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private Country country;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        country = new Country(1, "India", "IN");
    }

    @Test
    public void testGetCountries() {
        List<Country> countries = Arrays.asList(new Country(1, "India", "IN"), new Country(2, "USA", "US"));
        when(countryService.getAllCountries()).thenReturn(countries);
        ResponseEntity<List<Country>> response = countryController.getCountries();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetCountriesFalse() {
        List<Country> countries = Arrays.asList(new Country(1, "India", "IN"), new Country(2, "USA", "US"));
        when(countryService.getAllCountries()).thenReturn(countries);
        ResponseEntity<List<Country>> response = countryController.getCountries();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetCountryById() {
        when(countryService.getCountryById(1)).thenReturn(country);
        ResponseEntity<Country> response = countryController.getCountryById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("India", response.getBody().getCountryName());
    }

    @Test
    void testGetCountryById_NotFound() {
        int countryId = 1;
        when(countryService.getCountryById(countryId))
                .thenThrow(new ResourceNotFoundException("Country not found for ID: " + countryId));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryController.getCountryById(countryId));
        Assertions.assertEquals("Country not found for ID: " + countryId, exception.getMessage());
    }

    @Test
    public void testGetCountryByCode() {
        when(countryService.getCountryByCode("IN")).thenReturn(country);
        ResponseEntity<Country> response = countryController.getCountryByCode("IN");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("India", response.getBody().getCountryName());
    }

    @Test
    void testGetCountryByCode_NotFound() {
        String countryCode = "XYZ";
        when(countryService.getCountryByCode(countryCode))
                .thenThrow(new ResourceNotFoundException("Country not found for Code: " + countryCode));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryController.getCountryByCode(countryCode));
        Assertions.assertEquals("Country not found for Code: " + countryCode, exception.getMessage());
    }

    @Test
    public void testAddCountry() {
        when(countryService.addCountry(any(Country.class))).thenReturn(country);
        ResponseEntity<Country> response = countryController.addCountry(country);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("India", response.getBody().getCountryName());
    }

    @Test
    void testAddCountry_InvalidInput() {
        Country invalidCountry = new Country();
        when(countryService.addCountry(invalidCountry))
                .thenThrow(new IllegalArgumentException("Invalid country data"));
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                countryController.addCountry(invalidCountry));
        Assertions.assertEquals("Invalid country data", exception.getMessage());
    }
    @Test
    public void testUpdateCountry() {
        Country updatedCountry = new Country(1, "India", "IN");
        when(countryService.updateCountry(1, updatedCountry)).thenReturn(updatedCountry);
        ResponseEntity<Country> response = countryController.updateCountry(1, updatedCountry);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("India", response.getBody().getCountryName());
    }

    @Test
    void testUpdateCountry_NotFound() {
        int countryId = 2;
        Country countryData = new Country();
        countryData.setCountryName("Updated Country");
        countryData.setCountryCode("UC");

        when(countryService.updateCountry(countryId, countryData))
                .thenThrow(new ResourceNotFoundException("Country not found for ID: " + countryId));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryController.updateCountry(countryId, countryData));
        Assertions.assertEquals("Country not found for ID: " + countryId, exception.getMessage());
    }

    @Test
    public void testDeleteCountry() {
        doNothing().when(countryService).deleteCountry(1);
        ResponseEntity<?> response = countryController.updateCountry(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Country Record Deleted Successfully...", response.getBody());
    }

    @Test
    void testDeleteCountry_NotFound() {
        int countryId = 3;
        Mockito.doThrow(new ResourceNotFoundException("Country not found for ID: " + countryId))
                .when(countryService).deleteCountry(countryId);
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryController.updateCountry(countryId));
        Assertions.assertEquals("Country not found for ID: " + countryId, exception.getMessage());
    }
}
