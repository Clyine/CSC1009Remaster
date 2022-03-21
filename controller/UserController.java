package controller;

import java.util.concurrent.ConcurrentHashMap;

import model.*;
import view.*;

public class UserController {
    private User model;
    private UserView view;

    public UserController(User model, UserView view) {
        this.model = model;
        this.view = view;
    }

    public String getUsername() {
        return model.getUsername();
    }

    public String getPin() {
        return model.getPin();
    }

    public String setPin(String pin) {
        model.setPin(pin);
        return model.getPin();
    }

    public void addAcc(String accNo, Account acc) {
        model.addAcc(accNo, acc);
    }

    public ConcurrentHashMap<String, Account> getAccountList(){
        return model.getAccountList();
    }


    //VIEWS
    
}
