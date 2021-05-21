package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Provider;
import com.example.demo.entity.User;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
     
    //kiem tra xem user co ton tai trong csdl ko neu ko thi tao moi voi nha cung cap la fb
    public void processOAuthPostLogin(String email) {
    	System.out.println("username: " + email);
        User existUser = repo.findByEmail(email);
         
        if (existUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            
            newUser.setProvider(Provider.EMAIL);
            newUser.setEnabled(true);          
             
            repo.save(newUser);        
        }
        
         
    }
}
