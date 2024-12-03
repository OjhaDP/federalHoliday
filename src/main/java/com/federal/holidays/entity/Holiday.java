package com.federal.holidays.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Data
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_Id")
    private int id;
    @Column(name = "holiday_name")
    private String name;
    @Column(name = "holiday_date")
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "country_Id", nullable = false)
    private Country country;

    public Holiday(int i, String diwali, LocalDate of, Country country) {
    }
}

