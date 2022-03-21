package terminal;

import java.io.*;
import java.util.*;


import controller.*;
import model.*;
import datastore.*;
import exceptions.*;

public class Pages {

    private final DataStore d;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private SystemController controller;
    //private UserController currentUser;
    //private AccountController currentAcc;
    private static final Random r = new Random();
    private final Validate v;

    // contructor, pass datastore object, input/output stream objects
    public Pages(DataInputStream dis, DataOutputStream dos, DataStore d) {
        this.dis = dis;
        this.dos = dos;
        this.d = d;
        this.v = new Validate(this.dis, this.dos);
    }
    
    public void authPage() throws Exception{
        this.dos.writeUTF("Welcome to ABC ATM!\nEnter 1 to create a new user account\nEnter 2 to login using an existing account\nPlease enter your choice: ");
        String selection = dis.readUTF(); //take input from Inputstream
        switch (selection) {
                case "1":
                    this.dos.writeUTF("\nPlease enter a username.: ");
                    String username = v.checkUsernameDuplicate(this.dis.readUTF(), d);
                    this.dos.writeUTF("Please enter a pin no.: ");
                    String newPin = this.dis.readUTF();
                    this.d.getUserStore().put(username, newPin);
                    this.d.writeUser(username, newPin);
                    User newUser = new User(username, newPin);
                    String accNo = generateSavAccNo(this.d);
                    Account newAcc = new SavingsAcc(accNo);
                    newUser.addAcc(accNo, newAcc);
                    this.controller = new SystemController(newAcc, newUser);
                    Transaction t = controller.addOpeningTransaction();
                    this.d.getDataStore().put(controller.getUsername(), newUser);
                    this.d.writeLedger(controller, t);
                    this.d.writeHeader(controller);
                    this.dos.writeUTF("\nSuccess, your new savings account number is : " + accNo + "\nPress enter to continue.");
                    this.dis.readUTF();
                    authPage();
                    break;
                case "2":
                    loginTest();
                    break;
                default:
                    dos.writeUTF("\nInvalid Selection. Press enter to continue");
                    dis.readUTF();
                    authPage();
                    break;
                }
    }
        
    public void loginTest() throws Exception{
        while (true) {
            this.dos.writeUTF("\nPlease enter your username.: ");
            String username = this.v.validateUsername(this.dis.readUTF()); //validate and assign input string from InputStream
            this.dos.writeUTF("Please enter your pin no.: ");
            String pin = this.dis.readUTF();
            if (!this.d.authUser(username, pin)) { //Check credentials stored in userStore hashmap
                this.dos.writeUTF("Invalid username or PIN!\nRe-enter user");
            } else {
                User currUser = this.d.getDataStore().get(username); //get User object from userData hashmap
                this.dos.writeUTF("\nWelcome, " + currUser.getUsername()+ "\nPlease select option\n1. Open a new Account\n2. Transact on an active account");
                String input = this.dis.readUTF();
                switch (input) {
                    case "1":
                        createAcc(currUser);
                        break;
                    case "2":
                        Object[] currList = currUser.getAccountList().keySet().toArray(); //get List of accounts from User Object
                        String out = "\n"; //set account list output format
                        for (int i = 0; i < currUser.getAccountList().values().size(); i++){
                            out += String.format("%2d",(i+1)) + ") " + currList[i] + "\n";
                        }
                        this.dos.writeUTF(out + "\nPlease select account: "); //generate account list menu
                        int select = Integer.parseInt(this.dis.readUTF()); //take selection
                        String acc = (String)currList[select-1]; //set acc as AccountNo of selected account
                        Account myAccount = currUser.getAccountList().get(acc);
                        this.controller = new SystemController(myAccount, currUser); //set Account object based on AccountNo
                        break;
                    default:
                        dos.writeUTF("\nInvalid Selection. Press enter to continue");
                        dis.readUTF();
                        break;
                }
                break;
            }
        }
    }

    private void createAcc(User currUser) throws IOException{
        this.dos.writeUTF("Please select account to create\n1) Savings Account\n2) Current Account");
        String input = this.dis.readUTF();
        switch (input) {
            case "1":
                String accNo = generateSavAccNo(this.d);
                Account newAcc = new SavingsAcc(accNo);
                currUser.addAcc(accNo, newAcc);
                this.controller = new SystemController(newAcc, currUser);
                Transaction t = controller.addOpeningTransaction();
                this.d.getDataStore().put(controller.getUsername(), currUser);
                this.d.getUserLookup().put(accNo, controller.getUsername());
                this.d.writeLedger(controller, t);
                this.d.writeHeader(controller);
                this.dos.writeUTF("\nSuccess, your new savings account number is : " + accNo + "\nPress enter to continue.");
                this.dis.readUTF();
                break;
            case "2":
                accNo = generateCurrAccNo(this.d);
                newAcc = new CurrentAcc(accNo);
                currUser.addAcc(accNo, newAcc);
                this.controller = new SystemController(newAcc, currUser);
                t = controller.addOpeningTransaction();
                this.d.getDataStore().put(controller.getUsername(), currUser);
                this.d.getUserLookup().put(accNo, controller.getUsername());
                this.d.writeLedger(controller, t);
                this.d.writeHeader(controller);
                this.dos.writeUTF("\nSuccess, your new current account number is : " + accNo + "\nPress enter to continue.");
                this.dis.readUTF();
                break;
        
            default:
                dos.writeUTF("\nInvalid Selection. Press enter to continue");
                dis.readUTF();
                break;
        }
    }

    public void depositPage() throws Exception {
        this.dos.writeUTF("\nAmount to deposit: ");
        long amt = v.validateAmount(this.dis.readUTF());
        Transaction T = controller.addDeposit(amt);
        d.writeLedger(controller, T);
        this.dos.writeUTF("\nSuccess, Your new balance is $" + String.format("%.2f", (double) (this.controller.getBalance()) / 100) + "\nPress enter to continue");
        this.dis.readUTF();
    }

    public void withdrawPage() throws Exception {
        try {
            this.dos.writeUTF("\nAmount to withdraw: ");
            long amt = v.validateAmount(this.dis.readUTF());
            if (amt > controller.getWithdrawalLimit()){
                throw new WithdrawalLimitExceedException(controller.getWithdrawalLimit());
            }

            if (controller.getBalance() - amt < controller.getOverdraftLimit()) {
                if (controller.getOverdraftLimit() == 0) {
                    throw new InsufficientBalanceException();
                }
                else {
                    throw new OverdraftLimitExceedException(controller.getOverdraftLimit());
                }
            }
            Transaction T= controller.addWithdrawal(amt);
            d.writeLedger(controller, T);
            this.dos.writeUTF("\nSuccess, Your new balance is $" + String.format("%.2f", (double) (this.controller.getBalance()) / 100) + "\nPress enter to continue");
            this.dis.readUTF();
        } catch (Exception e) {
            this.dos.writeUTF(e.getMessage()+ "\nPress enter to continue");
            this.dis.readUTF();
        }
    }

    public void transferPage() throws Exception {
        try{
            this.dos.writeUTF("\nInput payee's Account Number: ");
            String str = dis.readUTF();
            if (d.getUsername(str) == null) {
                this.dos.writeUTF("\nInvalid Account Number. Press Enter to continue");
            }
            else {
                User payeeUser = this.d.getDataStore().get(this.d.getUsername(str));
                Account payeeAcc = payeeUser.getAccountList().get(str);
                SystemController payeeCon = new SystemController(payeeAcc, payeeUser);
                SystemController payorCon = this.controller;
                this.dos.writeUTF("\nAmount to transfer: ");
                long amt = v.validateAmount(this.dis.readUTF());
                if (amt > controller.getWithdrawalLimit()){
                    throw new WithdrawalLimitExceedException(controller.getWithdrawalLimit());
                }

                if (controller.getBalance() - amt < controller.getOverdraftLimit()) {
                    if (controller.getOverdraftLimit() == 0) {
                        throw new InsufficientBalanceException();
                    }
                    else {
                        throw new OverdraftLimitExceedException(controller.getOverdraftLimit());
                    }
                }
                Transaction inbound = payeeCon.addDeposit(amt, "Transfer from " + payorCon.getAccNo());
                Transaction outbound = payorCon.addWithdrawal(amt, "Transfer to " + payeeCon.getAccNo());

                this.d.writeLedger(payeeCon, inbound);
                this.d.writeLedger(payorCon, outbound);
                this.dos.writeUTF("\nSuccess, Your new balance is $" + String.format("%.2f",(double)(payorCon.getBalance())/100) + "\nPress enter to continue");
                }
            this.dis.readUTF();
        } catch (Exception e) {
            this.dos.writeUTF(e.getMessage()+ "\nPress enter to continue");
            this.dis.readUTF();
        }
    }

    public void detailsPage() throws Exception {
        this.dos.writeUTF("\nPlease select option:\n1)View Withdrawal Limit\n2)Set Withdrawal Limit\n3)View Overdraft Limit\n4)Set Overdraft Limit\n");
        String select = this.dis.readUTF();
        switch (select) {
            case "1":
                this.dos.writeUTF(this.controller.printWithdrawalLimit() + "\nPress enter to continue");
                this.dis.readUTF();
                break;
            case "2":
                this.dos.writeUTF("\nPlease input preferred withdrawal limit");
                long input = v.validateAmount(this.dis.readUTF());
                controller.setWithdrawalLimit(input);
                this.d.writeHeader(controller);
                this.dos.writeUTF("Success, " + this.controller.printWithdrawalLimit() + "\nPress enter to continue");
                this.dis.readUTF();
                break;
            case "3":
                this.dos.writeUTF(this.controller.printOverdraftLimit() + "\nPress enter to continue");
                this.dis.readUTF();
                break;
            case "4":
                if (this.controller.printOverdraftLimit().equals("This is a savings account. Overdraft is not available.")){
                    this.dos.writeUTF("Failure, " + this.controller.printOverdraftLimit()+ "\nPress enter to continue");
                } else {
                this.dos.writeUTF("\nPlease input preferred overdraft limit");
                input = v.validateAmount(this.dis.readUTF());
                controller.setOverdraftLimit(input);
                this.d.writeHeader(controller);
                this.dos.writeUTF("Success, " + this.controller.printOverdraftLimit() + "\nPress enter to continue");
                this.dis.readUTF();
                }
                break;
            default:
                dos.writeUTF("\nInvalid Selection. Press enter to continue");
                dis.readUTF();
                detailsPage();
                break;
        }
    }

    public void statementPage() throws Exception{
        this.dos.writeUTF(this.controller.printTransactionListing() + "\nPress Enter to continue");
        this.dis.readUTF();
    }

    public void balancePage() throws Exception{
        this.dos.writeUTF(this.controller.printBalance() + "\nPress Enter to continue");
        this.dis.readUTF();
    }

    public void printPrompt() throws Exception{
        this.dos.writeUTF("\nCurrently in: " + this.controller.getAccNo() + "\n\n1. View Balance\n2. View Statement\n3. Deposit\n4. Withdraw\n5. Transfer\n6. View Account Details\n9. Logout\n\nPlease select option: ");
    }

    private static String generateCurrAccNo(DataStore d) {
        String str = String.format("%d", 8000000000l + r.nextLong(10000000));
        if (!d.getDataStore().containsKey(str)){
            return str;
        }
        else {
            return generateCurrAccNo(d);
        }
    }

    private static String generateSavAccNo(DataStore d) {
        String str = String.format("%d", 4000000000l + r.nextLong(10000000));
        if (!d.getDataStore().containsKey(str)){
            return str;
        }
        else {
            return generateCurrAccNo(d);
        }
    }
}
