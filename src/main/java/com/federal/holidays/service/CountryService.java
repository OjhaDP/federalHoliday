package com.federal.holidays.service;

import com.federal.holidays.entity.Country;

import java.util.List;

public interface CountryService {

    Country addCountry(Country country);
    List<Country> getAllCountries();
    Country getCountryById(int id);
    Country getCountryByCode(String code);

    Country updateCountry(int id, Country countryDetails);
    void deleteCountry(int id);
}
