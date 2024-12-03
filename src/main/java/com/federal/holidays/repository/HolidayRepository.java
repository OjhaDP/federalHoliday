package com.federal.holidays.repository;

import com.federal.holidays.entity.Country;
import com.federal.holidays.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday,Integer> {

    List<Holiday> findByCountry(Country country);
    boolean existsByCountry_CountryCodeAndNameAndDate(String countryCode, String countryName, LocalDate date);
}