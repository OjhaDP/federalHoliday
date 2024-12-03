package com.federal.holidays.controller;

import com.federal.holidays.dto.HolidayRequest;
import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.exception.InvalidCsvException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.service.HolidayService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping
    public ResponseEntity<List<Holiday>> getHolidays(){
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/holiday/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable int id){
        Holiday holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping("/holiday/countryCode/{countryCode}")
    public ResponseEntity<List<Holiday>> getListHolidayByCountryCode(@PathVariable String  countryCode){
        List<Holiday> holidayList = holidayService.listHolidaysByCountry(countryCode);
        return ResponseEntity.ok(holidayList);
    }

    @PostMapping("/holiday/addAll")
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

    @PostMapping("/holiday/add")
    public ResponseEntity<HttpStatus> addHoliday(@RequestBody HolidayRequest holidayRequest) {
        Holiday holiday = new Holiday();
        holiday.setName(holidayRequest.getName());
        holiday.setDate(holidayRequest.getDate());
        Country country = holidayService.getCountryByCode(holidayRequest.getCountryCode());
        holiday.setCountry(country);

        holidayService.addHoliday(holiday);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/holiday/update/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable int id, @RequestBody HolidayRequest holidayRequest) {
        Holiday holidayDetails = new Holiday();
        holidayDetails.setName(holidayRequest.getName());
        holidayDetails.setDate(holidayRequest.getDate());
        holidayDetails.setCountry(holidayService.getCountryByCode(holidayRequest.getCountryCode()));

        Holiday updatedHoliday = holidayService.updateHoliday(id, holidayDetails);
        return ResponseEntity.ok(updatedHoliday);
    }

    @DeleteMapping("/holiday/delete/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable int id) {
        holidayService.deleteHolidayById(id);
        return ResponseEntity.ok().body("Holidays Record Deleted Successfully...");
    }

    @PostMapping("/holiday/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        List<Holiday> holidays = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();
        Set<LocalDate> uniqueDate = new HashSet<>();
        StringBuilder duplicateErrors = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            for (CSVRecord record : records) {

                String name = record.get("Name");
                LocalDate date = LocalDate.parse(record.get("Date"));
                String countryName = record.get("Country");
                String countryCode = record.get("CountryCode");
                String uniqueKey = countryCode + "-" + name + "-" + date;
                validateRecord(name, countryName, countryCode, file.getOriginalFilename());

                // Check for duplicates in the database
                if (holidayService.getExistsByCountryCodeAndNameAndDate(countryCode, countryName, date) || !uniqueKeys.add(uniqueKey) || !uniqueDate.add(date)) {

                        duplicateErrors.append("Duplicate found in database: ")
                                .append("CountryCode=").append(countryCode)
                                .append(", Name=").append(name)
                                .append(", Date=").append(date).append("\n");
                        continue;
                }

                Country country = countryRepository.findByCountryCode(countryCode)
                        .orElseGet(() -> {
                            Country newCountry = new Country();
                            newCountry.setCountryName(countryName);
                            newCountry.setCountryCode(countryCode);
                            return countryRepository.save(newCountry);
                        });

                Holiday holiday = new Holiday();
                holiday.setName(name);
                holiday.setDate(date);
                holiday.setCountry(country);

                holidays.add(holiday);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing the file: " + e.getMessage();
        }
        holidayService.addHolidaysFromFile(holidays);
        return "Record saved in database successfully :"+ duplicateErrors;
    }

    private void validateRecord(String name, String countryName, String countryCode, String fileName) throws InvalidCsvException {

        if (!countryCode.matches("[a-zA-Z]+")) {
            throw new InvalidCsvException("Invalid CountryCode: " + countryCode + ". It must be alphabetic.", fileName);
        }

        if (!countryName.matches("[a-zA-Z ]+")) {
            throw new InvalidCsvException("Invalid Country Name: " + name + ". It must be alphabetic.", fileName);
        }

        if (!name.matches("[a-zA-Zà-ÿÀ-ß'\\- ]+")) {
            throw new InvalidCsvException("Invalid Holiday Name: " + name + ". It must be alphabetic, and can include spaces, apostrophes, or hyphens.", fileName);
        }
    }
}
