package com.federal.holidays.service;

import static org.mockito.Mockito.*;

import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.repository.HolidayRepository;
import com.federal.holidays.service.impl.HolidayServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HolidayServiceImplTest {

    @InjectMocks
    private HolidayServiceImpl holidayService;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private CountryRepository countryRepository;

    private Country country;
    private Holiday holiday;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        holiday = new Holiday(1,"Christmas", LocalDate.of(2024, 12, 25), null);
    }

    @Test
    void testAddHoliday() {
        when(holidayRepository.save(holiday)).thenReturn(holiday);
        Holiday result = holidayService.addHoliday(holiday);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Christmas", result.getName());
        verify(holidayRepository, times(1)).save(holiday);
    }

    @Test
    void testAddAllHolidays() {
        List<Holiday> holidays = Arrays.asList(
                new Holiday(1,"New Year", LocalDate.of(2024, 1, 1), null),
                new Holiday(2, "Independence Day", LocalDate.of(2024, 8, 15), null)
        );
        holidayService.addAllHolidays(holidays);

        verify(holidayRepository, times(1)).saveAll(holidays);
    }

    @Test
    void testUpdateHoliday_Success() {
        int holidayId = 1;
        Holiday existingHoliday = new Holiday(1,"Old Name", LocalDate.of(2024, 12, 25), null);
        Holiday updatedData = new Holiday(2, "Christmas", LocalDate.of(2024, 12, 25), null);

        when(holidayRepository.findById(holidayId)).thenReturn(Optional.of(existingHoliday));
        when(holidayRepository.save(existingHoliday)).thenReturn(updatedData);

        Holiday result = holidayService.updateHoliday(holidayId, updatedData);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Christmas", result.getName());
        verify(holidayRepository, times(1)).findById(holidayId);
        verify(holidayRepository, times(1)).save(existingHoliday);
    }

    @Test
    void testUpdateHoliday_NotFound() {
        int holidayId = 99;
        when(holidayRepository.findById(holidayId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                holidayService.updateHoliday(holidayId, holiday));

        Assertions.assertEquals("Holiday not found for this id :: 99", exception.getMessage());
    }

    @Test
    void testGetAllHolidays() {
        List<Holiday> holidays = Arrays.asList(
                new Holiday(1,"New Year", LocalDate.of(2024, 1, 1), null),
                new Holiday(2, "Christmas", LocalDate.of(2024, 12, 25), null)
        );
        when(holidayRepository.findAll()).thenReturn(holidays);
        List<Holiday> result = holidayService.getAllHolidays();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        verify(holidayRepository, times(1)).findAll();
    }

    @Test
    void testGetHolidayById_Success() {
        int holidayId = 1;
        when(holidayRepository.findById(holidayId)).thenReturn(Optional.of(holiday));
        Holiday result = holidayService.getHolidayById(holidayId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Christmas", result.getName());
        verify(holidayRepository, times(1)).findById(holidayId);
    }

    @Test
    void testGetHolidayById_NotFound() {
        int holidayId = 99;
        when(holidayRepository.findById(holidayId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                holidayService.getHolidayById(holidayId));

        Assertions.assertEquals("Holiday not found for this id :99", exception.getMessage());
    }

    @Test
    void testListHolidaysByCountry_Success() {
        String countryCode = "US";
        Country country = new Country("United States", "US");
        List<Holiday> holidays = Arrays.asList(
                new Holiday(1,"Independence Day", LocalDate.of(2024, 7, 4), country),
                new Holiday(2,"Thanksgiving", LocalDate.of(2024, 11, 28), country)
        );
        when(countryRepository.findByCountryCode(countryCode.toUpperCase())).thenReturn(Optional.of(country));
        when(holidayRepository.findByCountry(country)).thenReturn(holidays);
        List<Holiday> result = holidayService.listHolidaysByCountry(countryCode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        verify(countryRepository, times(1)).findByCountryCode(countryCode.toUpperCase());
        verify(holidayRepository, times(1)).findByCountry(country);
    }

    @Test
    void testListHolidaysByCountry_NotFound() {
        String countryCode = "XYZ";
        when(countryRepository.findByCountryCode(countryCode.toUpperCase())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                holidayService.listHolidaysByCountry(countryCode));

        Assertions.assertEquals("Country not found by code :: XYZ", exception.getMessage());
    }

    @Test
    void testDeleteHolidayById_Success() {
        int holidayId = 1;
        when(holidayRepository.findById(holidayId)).thenReturn(Optional.of(holiday));
        doNothing().when(holidayRepository).delete(holiday);

        holidayService.deleteHolidayById(holidayId);

        verify(holidayRepository, times(1)).findById(holidayId);
        verify(holidayRepository, times(1)).delete(holiday);
    }

    @Test
    void testDeleteHolidayById_NotFound() {
        int holidayId = 99;
        when(holidayRepository.findById(holidayId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                holidayService.deleteHolidayById(holidayId));

        Assertions.assertEquals("Holiday not found for this id :: 99", exception.getMessage());
    }
}
