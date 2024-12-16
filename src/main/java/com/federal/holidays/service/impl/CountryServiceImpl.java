package com.federal.holidays.service.impl;

import com.federal.holidays.entity.Country;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.service.CountryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class CountryServiceImpl implements CountryService {

    private Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);
    @Autowired
    private CountryRepository countryRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Country addCountry(Country country) {
        country.setCountryCode(country.getCountryCode().toUpperCase());
        logger.info("Country Record added successfully");
        return countryRepository.save(country);
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Country getCountryById(int id) {
        return countryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Country not found for this id :"+ id));
    }

    @Override
    public Country getCountryByCode(String code) {
        return countryRepository.findByCountryCode(code).orElseThrow(()-> new ResourceNotFoundException("Country not found for this code :"+ code));

    }

    @Override
    public Country updateCountry(int id, Country countryDetails) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found for this id :" + id));
        logger.info("Updating country details : Id " + id);
        country.setCountryName(countryDetails.getCountryName());
        country.setCountryCode(countryDetails.getCountryCode());
        logger.info("Country Record updated in database for this id :" + id);
        return countryRepository.save(country);

    }

    @Override
    public void deleteCountry(int id) {
        Country country = countryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Country not found for this id :"+ id));
        if(country != null)
        countryRepository.deleteById(id);
        logger.info("Country Record Deleted for this id :" + id);

    }

}
