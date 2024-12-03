package com.federal.holidays.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_Id")
    private int countryId;
    @Column(name = "country_Name", nullable = false)
    private String countryName;
    @Column(name = "country_Code", nullable = false, unique = true)
    private String countryCode;

}