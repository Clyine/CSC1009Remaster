package controller;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.*;
import view.AccountView;

public abstract class AccountController {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected Account model;
    protected AccountView view;

    public AccountController(Account model, AccountView view) {
        this.model = model;
        this.view = view;
    }

    public String getAccNo(){
        return model.getAccNo();
    }

    public long getBalance(){
        return model.getBalance();
    }

    private void addTransaction(Transaction t) {
        model.addTransaction(t);
    }

    public Transaction addDeposit(long amt) {
        Transaction newT = new Transaction(dateFormat.format(new Date()), dateFormat.format(new Date()), "", "New Deposit", 0l, amt, model.getBalance() + amt);
        addTransaction(newT);
        return newT;
    }

    public Transaction addDeposit(long amt, String message) {
        Transaction newT = new Transaction(dateFormat.format(new Date()), dateFormat.format(new Date()), "", message, 0l, amt, model.getBalance() + amt);
        addTransaction(newT);
        return newT;
    }

    public Transaction addWithdrawal(long amt) {
        Transaction newT = new Transaction(dateFormat.format(new Date()), dateFormat.format(new Date()), "", "New Withdrawal", amt, 0l, model.getBalance() - amt);
        addTransaction(newT);
        return newT;
    }

    public Transaction addWithdrawal(long amt,String message) {
        Transaction newT = new Transaction(dateFormat.format(new Date()), dateFormat.format(new Date()), "", message, amt, 0l, model.getBalance() - amt);
        addTransaction(newT);
        return newT;
    }


    public Transaction addOpeningTransaction() {
        Transaction newT = new Transaction(dateFormat.format(new Date()), dateFormat.format(new Date()), "", "Account Opened", 0l, 0l, model.getBalance());
        addTransaction(newT);
        return newT;
    }

    public long setWithdrawalLimit(long amt) {
        return model.setWithdrawalLimit(amt);
    }

    public long getWithdrawalLimit() {
        return model.getWithdrawalLimit();
    }

    public abstract long getOverdraftLimit();
    public abstract long setOverdraftLimit(long amt);


//VIEWS METHODS    

    public String printTransactionListing() throws IOException {
        String str = model.getTransactionListing(20);
        return view.printTransactionListing(str);
    }

    public String printBalance() throws IOException {
        return view.printBalance(getBalance());
    }

    public String printWithdrawalLimit() throws IOException {
        return view.printWithdrawalLimit(getWithdrawalLimit());
    }

    public abstract String printOverdraftLimit() throws IOException;



}
