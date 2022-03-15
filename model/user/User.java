package model.user;

import java.util.LinkedList;
import java.util.Random;

public class User {
    private String username, pin;
    private LinkedList<String> accountList;
    private Random r = new Random();

    public User(String username, String pin) {
        this.username = username;
        this.pin = pin;
        this.accountList = new LinkedList<String>();
    }


    public String getUsername() {
        return this.username;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String addSavingAcc(String accNo) {
        accountList.add(accNo);
        return accNo;
    }

    public String addCurrentAcc(String accNo) {
        accountList.add(accNo);
        return accNo;
    }
}
