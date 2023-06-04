package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUserName(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setAdmin(admin);
        serviceProvider.setName(providerName);
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        if(!isAvailable(countryName)){
            throw new Exception("Country not found");
        }
        Country country = new Country();
        countryName = countryName.toUpperCase();
        CountryName name = CountryName.valueOf(countryName);
        country.setCountryName(name);
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        country.setServiceProvider(serviceProvider);
        country.setCode(name.toCode());
        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;
    }
    public boolean isAvailable(String country){
        String name = country.toLowerCase();
        HashSet<String> set = new HashSet<>();
        set.add("ind");
        set.add("aus");
        set.add("jpn");
        set.add("chi");
        set.add("usa");

        return set.contains(name);
    }
}
