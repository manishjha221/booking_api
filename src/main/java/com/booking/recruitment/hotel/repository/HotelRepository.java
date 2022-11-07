package com.booking.recruitment.hotel.repository;

import com.booking.recruitment.hotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Hotel set deleted = true where id=:id")
    int deleteLogicalById(@Param("id") Long id);

    Optional<Hotel>getHotelById(Long id);
}
