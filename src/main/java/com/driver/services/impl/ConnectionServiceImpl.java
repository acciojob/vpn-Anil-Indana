package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        countryName = countryName.toUpperCase();
       // CountryName countryName1 = CountryName.valueOf(countryName);
        boolean serviceProvider = true,countryAvaliable = true;
        int id = Integer.MAX_VALUE;
        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        Country newCountry = null;
        ServiceProvider serviceProvider1 = null;
        if(user.isConnected()){
            throw new Exception("Already connected");
        }
        else if(user.getOriginalCountry().getCountryName().toString().equals(countryName)){
            return user;
        }
        else {
            if(user.getServiceProviderList().size() == 0) serviceProvider = false;
            else {
                for(ServiceProvider s : serviceProviderList){
                    for(Country country : s.getCountryList()){
//                    if(!country.getCountryName().equals(countryName)) countryAvaliable = true;
                        if(country.getCountryName().toString().equals(countryName) && s.getId() < id) {
                            id = s.getId();
                            newCountry = country;
                            serviceProvider1 = s;
                            countryAvaliable = false;
                            break;
                        }
                    }
                }
            }
        }
        if(serviceProvider || countryAvaliable) throw new Exception("Unable to connect");

        String maskedIp = newCountry.getCode()+"."+serviceProvider1.getId()+"."+user.getId();
        user.setMaskedIp(maskedIp);
        Connection connection = new Connection();
        connection.setServiceProvider(serviceProvider1);
        connection.setUser(user);
        user.getConnectionList().add(connection);
        user.setConnected(true);
        serviceProvider1.getConnectionList().add(connection);
        User user1 = userRepository2.save(user);
        serviceProviderRepository2.save(serviceProvider1);
        connectionRepository2.save(connection);
        return user1;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(!user.isConnected()) {
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();
        if (receiver.getMaskedIp()!=null){
            String maskedIp = receiver.getMaskedIp();
            String code = maskedIp.substring(0,3);
            code = code.toUpperCase();
            if (code.equals(sender.getOriginalCountry().getCode())) return sender;
            String countryName = "";
            CountryName[] countryNames = CountryName.values();
            for(CountryName countryName1 : countryNames){
                if (countryName1.toCode().toString().equals(code)){
                    countryName = countryName1.toString();
                }
            }
            try {
                sender = connect(senderId,countryName);
            }catch (Exception e){
                throw new Exception("Cannot establish communication");
            }
            if (!sender.isConnected()){
                throw new Exception("Cannot establish communication");
            }
            return sender;
        }
        if (sender.getOriginalCountry().equals(receiver.getOriginalCountry())){
            return sender;
        }
        String countryName = receiver.getOriginalCountry().getCountryName().toString();
        try {
            sender = connect(senderId,countryName);
        }catch (Exception e){
            if (!sender.isConnected()) throw new Exception("Cannot establish communication");
        }
        return sender;
    }
}
