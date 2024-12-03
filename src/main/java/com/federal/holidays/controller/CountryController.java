package com.federal.holidays.controller;

import com.federal.holidays.entity.Country;
import com.federal.holidays.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CountryController {

    @Autowired
   private CountryService countryService;

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getCountries(){
        List<Country> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/countries/{countryId}")
    public ResponseEntity<Country> getCountryById(@PathVariable int countryId){
        return ResponseEntity.ok(countryService.getCountryById(countryId));
    }

    @GetMapping("/countries/countryCode/{countryCode}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable String countryCode){
        return ResponseEntity.ok(countryService.getCountryByCode(countryCode));
    }
    @PostMapping("/countries/add")
    public ResponseEntity<Country> addCountry(@RequestBody Country country){
        Country countryCreated = countryService.addCountry(country);
        return ResponseEntity.status(HttpStatus.CREATED).body(countryCreated);
    }

    @PutMapping("/countries/update/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable int id , @RequestBody Country countryData){
        Country countryUpdated = countryService.updateCountry(id, countryData);
        return ResponseEntity.ok(countryUpdated);
    }

    @DeleteMapping("/countries/delete/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable int id){
        countryService.deleteCountry(id);
        return ResponseEntity.ok().body("Country Record Deleted Successfully...");
    }
}
