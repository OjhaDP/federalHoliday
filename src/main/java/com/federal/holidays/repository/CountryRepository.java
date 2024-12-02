package com.federal.holidays.repository;

import com.federal.holidays.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {

    Optional<Country> findByCountryCode(String countryCode);
}
