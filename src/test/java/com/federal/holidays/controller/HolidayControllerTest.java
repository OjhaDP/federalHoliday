package com.federal.holidays.controller;

import com.federal.holidays.dto.HolidayRequest;
import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class HolidayControllerTest {

    @Mock
    private HolidayService holidayService;

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
        holidayRequest = new HolidayRequest("Diwali", LocalDate.of(2023, 11, 12), "IN");
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
    public void testGetHolidayById() {

        when(holidayService.getHolidayById(1)).thenReturn(holiday);
        ResponseEntity<Holiday> response = holidayController.getHolidayById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Diwali", response.getBody().getName());
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
    public void testAddHoliday() {

        when(holidayService.getCountryByCode("IN")).thenReturn(country);
        doNothing().when(holidayService).addHoliday(any(Holiday.class));
        ResponseEntity<HttpStatus> response = holidayController.addHoliday(holidayRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(holidayService, times(1)).addHoliday(any(Holiday.class));
    }

    @Test
    public void testUpdateHoliday() {

        Holiday updatedHoliday = new Holiday(1, "Diwali Updated", LocalDate.of(2023, 11, 12), country);
        when(holidayService.getCountryByCode("IN")).thenReturn(country);
        when(holidayService.updateHoliday(eq(1), any(Holiday.class))).thenReturn(updatedHoliday);

        ResponseEntity<Holiday> response = holidayController.updateHoliday(1, holidayRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Diwali Updated", response.getBody().getName());
    }

    @Test
    public void testDeleteHoliday() {
        doNothing().when(holidayService).deleteHolidayById(1);

        ResponseEntity<?> response = holidayController.deleteHoliday(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Holidays Record Deleted Successfully...", response.getBody());
        verify(holidayService, times(1)).deleteHolidayById(1);
    }
}
