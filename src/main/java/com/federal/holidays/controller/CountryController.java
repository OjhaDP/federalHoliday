package com.federal.holidays.controller;

import com.federal.holidays.entity.Country;
import com.federal.holidays.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @Autowired
   private CountryService countryService;

    @GetMapping
    public ResponseEntity<List<Country>> getCountries(){
        List<Country> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable int countryId){
        return ResponseEntity.ok(countryService.getCountryById(countryId));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable String countryCode){
        return ResponseEntity.ok(countryService.getCountryByCode(countryCode));
    }
    @PostMapping("/add")
    public ResponseEntity<Country> addCountry(@RequestBody Country country){
        Country countryCreated = countryService.addCountry(country);
        return ResponseEntity.status(HttpStatus.CREATED).body(countryCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable int id , @RequestBody Country countryData){
        Country countryUpdated = countryService.updateCountry(id, countryData);
        return ResponseEntity.ok(countryUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable int id){
        countryService.deleteCountry(id);
        return ResponseEntity.ok().body("Country Record Deleted Successfully...");
    }
}
