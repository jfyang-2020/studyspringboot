package com.example.springboot.modules.test.service.impl;

import com.example.springboot.modules.common.vo.Result;
import com.example.springboot.modules.test.dao.CityDao;
import com.example.springboot.modules.test.entity.City;
import com.example.springboot.modules.test.service.CityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityDao cityDao;

    public List<City> getCitiesById(int id) {
//        return cityDao.getCitiesById(id);
        return Optional.ofNullable(cityDao.getCitiesById(id))
                .orElse(Collections.emptyList());
    }

    public City getCityByCityName(String cityName) {
        return cityDao.getCityByCityName(cityName);
    }

    //pagehelper 插件使用
    public PageInfo<City> getCitiesByPage(int currentPage,int pageSize,int id){
        PageHelper.startPage(currentPage,pageSize);
        return new PageInfo<>(Optional.ofNullable(cityDao.getCitiesById(id))
                .orElse(Collections.emptyList()));
    }

    @Override
    public Result<City> insertCity(City city) {
        city.setDateCreated(new Date());
        cityDao.insertCity(city);
        return new Result<City>(Result.ResultStatus.SUCCESS.status,"Insert success",city);
    }

    @Override
//    noRollbackFor 表示遇到某种异常不回滚   propagation 配置传播方式
//    @Transactional(noRollbackFor = ,propagation = )
    @Transactional
    public Result<City> updateCity(City city) {
        cityDao.updateCity(city);
        return new Result<City>(Result.ResultStatus.SUCCESS.status,"update success",city);
    }

    @Override
    public Result<Object> deleteCity(int id) {
        cityDao.deleteCity(id);
        return new Result<Object>(Result.ResultStatus.SUCCESS.status,"delete success");

    }

}
