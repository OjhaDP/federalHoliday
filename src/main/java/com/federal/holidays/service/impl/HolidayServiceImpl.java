package com.federal.holidays.service.impl;

import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import com.federal.holidays.exception.ResourceNotFoundException;
import com.federal.holidays.repository.CountryRepository;
import com.federal.holidays.repository.HolidayRepository;
import com.federal.holidays.service.HolidayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    private Logger logger = LoggerFactory.getLogger(HolidayServiceImpl.class);
    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Holiday addHoliday(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    @Override
    public void addAllHolidays(List<Holiday> holidays) {
        holidayRepository.saveAll(holidays);
    }

    @Override
    public Holiday updateHoliday(int id, Holiday holidayData) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found for this id :: " + id));
        holiday.setName(holidayData.getName());
        holiday.setDate(holidayData.getDate());
        holiday.setCountry(holidayData.getCountry());
        logger.info("Holiday updated successfully.. : Id " + id);
        return holidayRepository.save(holiday);
    }

    @Override
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    @Override
    public Holiday getHolidayById(int id) {
        return holidayRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Holiday not found for this id :" + id));
    }

    @Override
    public List<Holiday> listHolidaysByCountry(String countryCode) {
        Country country = countryRepository.findByCountryCode(countryCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found by code :: " + countryCode));
        logger.info("Fetching all holidays by country");
        return holidayRepository.findByCountry(country);
    }

    @Override
    public void deleteHolidayById(int id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found for this id :: " + id));
        logger.info("Deleting holiday: id " + id);
        holidayRepository.delete(holiday);
    }

    @Override
    public Country getCountryByCode(String countryCode) {
        logger.info("Getting country by country code: " + countryCode);
        return countryRepository.findByCountryCode(countryCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found by code :: " + countryCode));
    }

    @Override
    public void addHolidaysFromFile(List<Holiday> holidays) {
        holidayRepository.saveAll(holidays);
    }
}
