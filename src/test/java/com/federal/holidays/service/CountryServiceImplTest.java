package com.federal.holidays.service;

import com.federal.holidays.entity.Country;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @InjectMocks
    private CountryServiceImpl countryService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    void testAddCountry() {
        Country country = new Country();
        country.setCountryName("India");
        country.setCountryCode("IN");

        Country savedCountry = new Country();
        savedCountry.setCountryName("India");
        savedCountry.setCountryCode("IN");

        when(countryRepository.save(country)).thenReturn(savedCountry);
        Country result = countryService.addCountry(country);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("IN", result.getCountryCode());
        Assertions.assertEquals("India", result.getCountryName());
        Mockito.verify(countryRepository, Mockito.times(1)).save(country);
    }

    @Test
    void testGetAllCountries() {
        List<Country> countries = Arrays.asList(
                new Country("India", "IN"),
                new Country("United States", "US")
        );

        Mockito.when(countryRepository.findAll()).thenReturn(countries);
        List<Country> result = countryService.getAllCountries();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Mockito.verify(countryRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetCountryById_Found() {
        int countryId = 1;
        Country country = new Country("India", "IN");
        country.setCountryId(countryId);

        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        Country result = countryService.getCountryById(countryId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(countryId, result.getCountryId());
        Assertions.assertEquals("India", result.getCountryName());
        Mockito.verify(countryRepository, Mockito.times(1)).findById(countryId);
    }

    @Test
    void testGetCountryById_NotFound() {
        int countryId = 99;

        Mockito.when(countryRepository.findById(countryId))
                .thenThrow(new ResourceNotFoundException("Country not found for this id :" + countryId));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryService.getCountryById(countryId));

        Assertions.assertEquals("Country not found for this id :" + countryId, exception.getMessage());
    }

    @Test
    void testGetCountryByCode_Found() {
        String countryCode = "IN";
        Country country = new Country("India", "IN");
        Mockito.when(countryRepository.findByCountryCode(countryCode)).thenReturn(Optional.of(country));
        Country result = countryService.getCountryByCode(countryCode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("IN", result.getCountryCode());
        Assertions.assertEquals("India", result.getCountryName());
        Mockito.verify(countryRepository, Mockito.times(1)).findByCountryCode(countryCode);
    }

    @Test
    void testGetCountryByCode_NotFound() {
        String countryCode = "XYZ";
        Mockito.when(countryRepository.findByCountryCode(countryCode))
                .thenThrow(new ResourceNotFoundException("Country not found for this code :" + countryCode));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryService.getCountryByCode(countryCode));

        Assertions.assertEquals("Country not found for this code :" + countryCode, exception.getMessage());
    }

    @Test
    void testUpdateCountry() {
        int countryId = 1;
        Country existingCountry = new Country("India", "IN");
        existingCountry.setCountryId(countryId);

        Country updatedCountry = new Country("Bharat", "BH");
        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.of(existingCountry));
        Mockito.when(countryRepository.save(existingCountry)).thenReturn(updatedCountry);
        Country result = countryService.updateCountry(countryId, updatedCountry);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bharat", result.getCountryName());
        Assertions.assertEquals("BH", result.getCountryCode());
        Mockito.verify(countryRepository, Mockito.times(1)).findById(countryId);
        Mockito.verify(countryRepository, Mockito.times(1)).save(existingCountry);
    }

    @Test
    void testDeleteCountry() {
        int countryId = 1;
        Country country = new Country("India", "IN");
        country.setCountryId(countryId);

        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        Mockito.doNothing().when(countryRepository).delete(country);
        countryService.deleteCountry(countryId);

        Mockito.verify(countryRepository, Mockito.times(1)).findById(countryId);
        Mockito.verify(countryRepository, Mockito.times(1)).delete(country);
    }

    @Test
    void testDeleteCountry_NotFound() {
        int countryId = 99;
        Mockito.when(countryRepository.findById(countryId))
                .thenThrow(new ResourceNotFoundException("Country not found for this id :" + countryId));
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                countryService.deleteCountry(countryId));

        Assertions.assertEquals("Country not found for this id :" + countryId, exception.getMessage());
    }
}

