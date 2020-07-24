package com.example.springboot.modules.test.service.impl;

import com.example.springboot.modules.test.dao.CountryDao;
import com.example.springboot.modules.test.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl {

    @Autowired
    private CountryDao countryDao;

    public Country getCountryByCountryId(int countryId) {
        return countryDao.getCountryByCountryId(countryId);
    }

}
