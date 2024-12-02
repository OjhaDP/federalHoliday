package com.federal.holidays.service;

import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;

import java.util.List;

public interface HolidayService {

    Holiday addHoliday(Holiday holiday);
    void addAllHolidays(List<Holiday> holidays);
    Holiday updateHoliday(int id, Holiday holidayData);
    List<Holiday> getAllHolidays();
    Holiday getHolidayById(int id);
    List<Holiday> listHolidaysByCountry(String countryCode);
    void deleteHolidayById(int id);
    Country getCountryByCode(String countryCode);
    void addHolidaysFromFile(List<Holiday> holidays);
}
