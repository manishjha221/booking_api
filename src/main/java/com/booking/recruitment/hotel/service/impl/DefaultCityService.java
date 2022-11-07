package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.City;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.CityRepository;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
class DefaultCityService implements CityService {
    private final CityRepository cityRepository;



    private final HotelRepository hotelRepository;

    @Autowired
    DefaultCityService(CityRepository cityRepository, HotelRepository hotelRepository) {
        this.cityRepository = cityRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public City getCityById(Long id) {
        return cityRepository
                .findById( id )
                .orElseThrow( () -> new ElementNotFoundException( "Could not find city with ID provided" ) );
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City createCity(City city) {
        if (city.getId() != null) {
            throw new BadRequestException( "The ID must not be provided when creating a new City" );
        }

        return cityRepository.save( city );
    }

    @Override
    public List<Hotel> getClosetCityHotels(Long cityId, String sortBy) {
        List<Hotel> hotelList = hotelRepository.findAll();
        Optional<City> centralCity = cityRepository.getCityById( cityId );
        if (!centralCity.isPresent()||centralCity == null) {
            throw new BadRequestException( "City id not present" );
        }
        List<HotelDistance> computeDistanceList = new ArrayList<>();
        for (Hotel h : hotelList) {
            if(h.isDeleted())continue;
            double distance = haversineFormulaComputeDistance( centralCity.get().getCityCentreLatitude(), centralCity.get().getCityCentreLongitude(),
                    h.getLatitude(), h.getLongitude() );
            computeDistanceList.add( new HotelDistance( h, distance ) );
        }
        Collections.sort( computeDistanceList, Comparator.comparing( a -> a.distance ) );
        List<Hotel> hotels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hotels.add( computeDistanceList.get( i ).hotel );
        }
        return hotels;
    }

    private double haversineFormulaComputeDistance(double lat1, double lon1,
                                                   double lat2, double lon2) {
        double dLat = Math.toRadians( lat2 - lat1 );
        double dLon = Math.toRadians( lon2 - lon1 );

        lat1 = Math.toRadians( lat1 );
        lat2 = Math.toRadians( lat2 );

        double a = Math.pow( Math.sin( dLat / 2 ), 2 ) +
                Math.pow( Math.sin( dLon / 2 ), 2 ) *
                        Math.cos( lat1 ) *
                        Math.cos( lat2 );
        double rad = 6371;
        double c = 2 * Math.asin( Math.sqrt( a ) );
        return rad * c;
    }

    class HotelDistance {
        Hotel hotel;
        Double distance;

        HotelDistance(Hotel hotel, Double distance) {
           this.hotel = hotel;
          this.distance = distance;
        }
    }
}
