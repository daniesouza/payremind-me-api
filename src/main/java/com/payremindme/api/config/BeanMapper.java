package com.payremindme.api.config;

import org.dozer.DozerBeanMapper;

/**
 * Created by daniel
 */
public class BeanMapper {

    private static DozerBeanMapper instance;

    public static DozerBeanMapper getInstance(){

        if(instance == null){
            instance = new DozerBeanMapper();
        }

        return instance;
    }

}
