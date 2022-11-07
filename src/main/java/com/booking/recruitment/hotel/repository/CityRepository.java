package com.booking.recruitment.hotel.repository;

import com.booking.recruitment.hotel.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> getCityById(Long id);
}
