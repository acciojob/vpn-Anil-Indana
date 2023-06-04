package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Country country = new Country();
        countryName = countryName.toUpperCase();
        CountryName name = CountryName.valueOf(countryName);
        country.setCountryName(name);
        country.setServiceProvider(null);
        country.setCode(name.toCode());
        country.setUser(user);
        user.setConnected(false);
        user.setMaskedIp(null);
        User user1 = userRepository3.save(user);
        user1.setOriginalIp(name.toCode() + user1.getId());
        countryRepository3.save(country);
        return userRepository3.save(user1);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);
        userRepository3.save(user);
        serviceProviderRepository3.save(serviceProvider);
        return user;
    }
}
