package com.federal.holidays.controller;

import com.federal.holidays.dto.HolidayRequest;
import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.exception.InvalidCsvException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/holiday")
@Tag(name = "Holiday", description = "APIs to create, update and other operations of holiday.")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private CountryRepository countryRepository;

    @Operation(summary = "Get all holidays")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all holidays",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Holiday.class))})
    })
    @GetMapping
    public ResponseEntity<List<Holiday>> getHolidays(){
        List<Holiday> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }


    @Operation(summary = "Get a holiday by ID")
    @Parameters({
            @Parameter(name = "id", description = "ID of the holiday to retrieve", required = true, schema = @Schema(type = "integer"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the holiday",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Holiday.class))}),
            @ApiResponse(responseCode = "404", description = "Holiday not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable int id){
        Holiday holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holiday);
    }

    @Operation(summary = "Get holidays by country code")
    @Parameters({
            @Parameter(name = "countryCode", description = "Country code to filter holidays by", required = true, schema = @Schema(type = "string"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found holidays",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Holiday.class))})
    })
    @GetMapping("/countryCode/{countryCode}")
    public ResponseEntity<List<Holiday>> getListHolidayByCountryCode(@PathVariable String  countryCode){
        List<Holiday> holidayList = holidayService.listHolidaysByCountry(countryCode);
        return ResponseEntity.ok(holidayList);
    }

    @Operation(summary = "Add multiple holidays")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Holidays added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
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

    @Operation(summary = "Add a single holiday")
    @Parameters({
            @Parameter(name = "holidayRequest", description = "Holiday data to be added", required = true, schema = @Schema(implementation = HolidayRequest.class))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Holiday added successfully"),
            @ApiResponse(responseCode = "409", description = "Holiday already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addHoliday(@RequestBody HolidayRequest holidayRequest) {
        Holiday holiday = new Holiday();
        holiday.setName(holidayRequest.getName());
        holiday.setDate(holidayRequest.getDate());
        Country country = holidayService.getCountryByCode(holidayRequest.getCountryCode());
        holiday.setCountry(country);
        boolean exists = holidayService.getExistsByCountryCodeAndNameAndDate(holidayRequest.getCountryCode(), holidayRequest.getName(), holidayRequest.getDate());
        if (!exists) {
            holidayService.addHoliday(holiday);
            return ResponseEntity.status(HttpStatus.CREATED).body("Holiday addedd successfully!");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Holiday already exists for the given country, name, and date.");

    }

    @Operation(summary = "Update a holiday")
    @Parameters({
            @Parameter(name = "id", description = "ID of the holiday to update", required = true, schema = @Schema(type = "integer")),
            @Parameter(name = "holidayRequest", description = "Updated holiday data", required = true, schema = @Schema(implementation = HolidayRequest.class))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Holiday updated successfully"),
            @ApiResponse(responseCode = "404", description = "Holiday not found")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable int id, @RequestBody HolidayRequest holidayRequest) {
        Holiday holidayDetails = new Holiday();
        holidayDetails.setName(holidayRequest.getName());
        holidayDetails.setDate(holidayRequest.getDate());
        holidayDetails.setCountry(holidayService.getCountryByCode(holidayRequest.getCountryCode()));

        Holiday updatedHoliday = holidayService.updateHoliday(id, holidayDetails);
        if (updatedHoliday == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(updatedHoliday);
    }

    @Operation(summary = "Delete a holiday")
    @Parameters({
            @Parameter(name = "id", description = "ID of the holiday to delete", required = true, schema = @Schema(type = "integer"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Holiday deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Holiday not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable int id) {
        holidayService.deleteHolidayById(id);
        return ResponseEntity.ok().body("Holidays Record Deleted Successfully...");
    }

    @Operation(summary = "Upload holidays via file")
    @Parameters({
            @Parameter(description = "File(s) to upload. Accepted formats: CSV.",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary")))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format")
    })
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("files") MultipartFile[] files) {
        List<Holiday> holidays = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();
        Set<LocalDate> uniqueDate = new HashSet<>();
        StringBuilder duplicateErrors = new StringBuilder();
        LocalDate date;
        for (MultipartFile file : files) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

                for (CSVRecord record : records) {

                    String name = record.get("Name");
                    try {
                        date = LocalDate.parse(record.get("Date"));
                    } catch (DateTimeParseException e) {
                        throw new InvalidCsvException("Invalid Date : " + name + ". It must be proper Date format YYYY-MM-DD.", file.getOriginalFilename());
                    }
                    String countryName = record.get("Country");
                    String countryCode = record.get("CountryCode");
                    String uniqueKey = countryCode + "-" + name + "-" + date;
                    validateRecord(name, countryName, countryCode, file.getOriginalFilename());

                    // Check for duplicates in the database
                    if (holidayService.getExistsByCountryCodeAndNameAndDate(countryCode, countryName, date)
                            || !uniqueKeys.add(uniqueKey) || !uniqueDate.add(date)) {

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
        }
        if(holidays.isEmpty()){
            return "Record not saved because file is invalid or record not present.";
        }else
        holidayService.addHolidaysFromFile(holidays);
        return "Record saved in database successfully :" + duplicateErrors;

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