package com.federal.holidays.controller;

import com.federal.holidays.dto.HolidayRequest;
import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public ResponseEntity<List<Holiday>> getHolidays(){
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable int id){
        Holiday holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<Holiday>> getListHolidayByCountryCode(@PathVariable String  countryCode){
        List<Holiday> holidayList = holidayService.listHolidaysByCountry(countryCode);
        return ResponseEntity.ok(holidayList);
    }

    @PostMapping("/addAll")
    public ResponseEntity<HttpStatus> addHolidays(@RequestBody List<HolidayRequest> holidayRequests){
        List<Holiday> holidayList = new ArrayList<>();
        holidayRequests.forEach(data -> {
            Holiday holiday = new Holiday();
            holiday.setName(data.getName());
            holiday.setDate(data.getDate());
            Country country = holidayService.getCountryByCode(data.getCountryCode());
            holiday.setCountry(country);
            holidayList.add(holiday);
        });
        holidayService.addAllHolidays(holidayList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addHoliday(@RequestBody HolidayRequest holidayRequest) {
        Holiday holiday = new Holiday();
        holiday.setName(holidayRequest.getName());
        holiday.setDate(holidayRequest.getDate());
        Country country = holidayService.getCountryByCode(holidayRequest.getCountryCode());
        holiday.setCountry(country);

        holidayService.addHoliday(holiday);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable int id, @RequestBody HolidayRequest holidayRequest) {
        Holiday holidayDetails = new Holiday();
        holidayDetails.setName(holidayRequest.getName());
        holidayDetails.setDate(holidayRequest.getDate());
        holidayDetails.setCountry(holidayService.getCountryByCode(holidayRequest.getCountryCode()));

        Holiday updatedHoliday = holidayService.updateHoliday(id, holidayDetails);
        return ResponseEntity.ok(updatedHoliday);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable int id) {
        holidayService.deleteHolidayById(id);
        return ResponseEntity.ok().body("Holidays Record Deleted Successfully...");
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<Holiday> holidays = new ArrayList<>();
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Holiday holiday = new Holiday();
                holiday.setId(Integer.parseInt(data[0].trim()));
                holiday.setName(data[1].trim());
                holiday.setDate(LocalDate.parse(data[2].trim()));
                holidays.add(holiday);
            }
            holidayService.addHolidaysFromFile(holidays);
            return "File uploaded and holidays saved successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing the file: " + e.getMessage();
        }
    }
}
