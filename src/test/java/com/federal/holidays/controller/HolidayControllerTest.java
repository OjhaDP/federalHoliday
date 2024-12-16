package com.federal.holidays.controller;

import com.federal.holidays.dto.HolidayRequest;
import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HolidayControllerTest {

    @Mock
    private HolidayService holidayService;

    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private HolidayController holidayController;

    private Holiday holiday;
    private HolidayRequest holidayRequest;
    private Country country;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        country = new Country(1, "India", "IN");
        holiday = new Holiday(1, "Diwali", LocalDate.of(2023, 11, 12), country);
        holidayRequest = new HolidayRequest("Diwali", LocalDate.of(2023, 11, 12), "IN", "Active");
    }

    @Test
    public void testGetHolidays() {
        List<Holiday> holidays = Arrays.asList(holiday);
        when(holidayService.getAllHolidays()).thenReturn(holidays);

        ResponseEntity<List<Holiday>> response = holidayController.getHolidays();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetHolidays_Negative() {
        when(holidayService.getAllHolidays()).thenReturn(Arrays.asList());

        ResponseEntity<List<Holiday>> response = holidayController.getHolidays();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(holidayService, times(1)).getAllHolidays();
    }

    @Test
    public void testGetHolidayById_Positive() {
        when(holidayService.getHolidayById(1)).thenReturn(holiday);

        ResponseEntity<Holiday> response = holidayController.getHolidayById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(holiday, response.getBody());
        verify(holidayService, times(1)).getHolidayById(1);
    }

    @Test
    public void testGetHolidayById_Negative() {
        when(holidayService.getHolidayById(1)).thenThrow(new ResourceNotFoundException("Holiday not found"));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            holidayController.getHolidayById(1);
        });

        assertEquals("Holiday not found", exception.getMessage());
        verify(holidayService, times(1)).getHolidayById(1);
    }

    @Test
    public void testGetListHolidayByCountryCode() {

        List<Holiday> holidays = Arrays.asList(holiday);
        when(holidayService.listHolidaysByCountry("IN")).thenReturn(holidays);

        ResponseEntity<List<Holiday>> response = holidayController.getListHolidayByCountryCode("IN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testAddHolidays() {

        List<HolidayRequest> requests = Arrays.asList(holidayRequest);
        when(holidayService.getCountryByCode("IN")).thenReturn(country);
        doNothing().when(holidayService).addAllHolidays(anyList());
        ResponseEntity<HttpStatus> response = holidayController.addHolidays(requests);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(holidayService, times(1)).addAllHolidays(anyList());
    }

    @Test
    public void testUpdateHoliday() {
        HolidayRequest holidayRequest = new HolidayRequest("Diwali",LocalDate.of(2023,11,12),"IN","Active");
        Holiday updatedHoliday = new Holiday(1, "Diwali Updated", LocalDate.of(2023, 11, 12), country);
        when(holidayService.getCountryByCode("IN")).thenReturn(country);
        when(holidayService.updateHoliday(eq(1), any(Holiday.class))).thenReturn(updatedHoliday);

        ResponseEntity<Holiday> response = holidayController.updateHoliday(1, holidayRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteHoliday() {
        doNothing().when(holidayService).deleteHolidayById(1);

        ResponseEntity<?> response = holidayController.deleteHoliday(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Holidays Record Deleted Successfully...", response.getBody());
        verify(holidayService, times(1)).deleteHolidayById(1);
    }

    @Test
    public void testUploadFile_Positive() {
        MockMultipartFile file = new MockMultipartFile("file", "holidays.csv", "text/csv", "Name,Date,Country,CountryCode\nChristmas,2024-12-25,USA,US".getBytes());
        when(countryRepository.findByCountryCode("US")).thenReturn(Optional.of(new Country("USA", "US")));

        String response = holidayController.uploadFile(new MultipartFile[]{file});

        assertTrue(response.contains("Record saved in database successfully"));
        verify(holidayService, times(1)).addHolidaysFromFile(anyList());
    }

    @Test
    public void testUploadFile_Negative() {
        MockMultipartFile file = new MockMultipartFile("file", "holidays.csv", "text/csv", "InvalidContent".getBytes());

        String response = holidayController.uploadFile(new MultipartFile[]{file});

        assertTrue(response.contains("Record not saved because file is invalid or record not present."));
        verify(holidayService, never()).addHolidaysFromFile(anyList());
    }
}
