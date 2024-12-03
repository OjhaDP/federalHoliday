package com.federal.holidays.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Country name cannot be blank")
    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    @Column(name = "country_Name", nullable = false)
    private String countryName;

    @NotBlank(message = "Country code cannot be blank")
    @Size(min = 2, max = 5, message = "Country code must be between 2 and 5 characters")
    @Column(name = "country_Code", nullable = false, unique = true)
    private String countryCode;

}