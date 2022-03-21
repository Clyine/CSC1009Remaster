package controller;
import model.*;
import view.*;

import java.io.IOException;

public class SystemController {

    private AccountController accCon;
    private UserController userCon;


    public SystemController(User user){
        this.userCon = new UserController(user, new UserView()); 
    }

    public SystemController(Account acc, User user){
        this.userCon = new UserController(user, new UserView()); 
        if (acc.getAccNo().charAt(0) == '8'){
            this.accCon = new CurrentAccountController((CurrentAcc)acc, new CurrentAccView());
        }
        else {
            this.accCon = new SavingAccountController((SavingsAcc)acc , new SavingAccView());
        }
    }

    public void setAccountCon(Account acc) {
        if (acc.getAccNo().charAt(0) == '8'){
            this.accCon = new CurrentAccountController((CurrentAcc)acc, new CurrentAccView());
        }
        else {
            this.accCon = new SavingAccountController((SavingsAcc)acc , new SavingAccView());
        }
    }


    public Transaction addDeposit(long amt){
        return this.accCon.addDeposit(amt);
    } 

    public Transaction addWithdrawal(long amt){
        return this.accCon.addWithdrawal(amt);
    } 

    public Transaction addDeposit(long amt, String message){
        return this.accCon.addDeposit(amt, message);
    } 

    public Transaction addWithdrawal(long amt, String message){
        return this.accCon.addWithdrawal(amt, message);
    } 

    public Transaction addOpeningTransaction(){
        return this.accCon.addOpeningTransaction();
    }

    public String setPin(String pin) {
        return this.userCon.setPin(pin);
    }

    public void addAcc(String accNo, Account acc) {
        this.userCon.addAcc(accNo, acc);
    }

    public long setOverdraftLimit(long amt) {
        return accCon.setOverdraftLimit(amt);
    }
    
    public long setWithdrawalLimit(long amt) {
        return accCon.setWithdrawalLimit(amt);
    }

    public String getAccNo(){
        return accCon.getAccNo();
    }

    public long getBalance(){
        return accCon.getBalance();
    }

    public String getUsername(){
        return userCon.getUsername();
    }

    public long getWithdrawalLimit() {
        return accCon.getWithdrawalLimit();
    }

    public long getOverdraftLimit() {
        return accCon.getOverdraftLimit();
    }

    public String printTransactionListing() throws IOException {
        return accCon.printTransactionListing();
    }

    public String printOverdraftLimit() throws IOException{
        return accCon.printOverdraftLimit();
    }

    public String printWithdrawalLimit() throws IOException{
        return accCon.printWithdrawalLimit();
    }

    public String printBalance() throws IOException {
        return accCon.printBalance();
    }

}
