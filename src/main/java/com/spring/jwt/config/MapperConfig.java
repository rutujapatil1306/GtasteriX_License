package com.spring.jwt.config;

import com.spring.jwt.dto.CustomerDTO;
import com.spring.jwt.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public
    ModelMapper modelMapper() {
        ModelMapper mapper=new ModelMapper();
        mapper.typeMap(Customer.class, CustomerDTO.class).setProvider(request -> new CustomerDTO());
        return mapper; // Return the ModelMapper instance
    }
}
