
import model.*;
import view.*;
import controller.*;
import exceptions.InsufficientBalanceException;
import exceptions.OverdraftLimitExceedException;
import exceptions.WithdrawalLimitExceedException;

import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

public class TestingAccount {
    private static DataOutputStream dos;
    private static final Random r = new Random();
    public static void main(String[] args) throws IOException{
        ConcurrentHashMap<String, Account> ledger = new ConcurrentHashMap<String, Account>();
        ConcurrentHashMap<String, User> userStore = new ConcurrentHashMap<String, User>();

        initData(ledger);
        initHeader(ledger);
        initUserStore(userStore, ledger);

        //login
        User myTest = userStore.get("sample");
        UserController myTestCon = new UserController(myTest, new UserView());
        
        //enter Account
        Account CurrAcc = myTestCon.getAccountList().get("8006780504");
        AccountController currAccCon = new CurrentAccountController((CurrentAcc)CurrAcc, new CurrentAccView());

        //Deposits
        String accNum = currAccCon.getAccNo();
        Transaction T= currAccCon.addDeposit(344444);
        writeLedger(accNum, T);

        //Withdrawal
        try {
            long amt = 244444;
            if (amt > currAccCon.getWithdrawalLimit()){
                throw new WithdrawalLimitExceedException(currAccCon.getWithdrawalLimit());
            }

            if (currAccCon.getBalance() - amt < currAccCon.getOverdraftLimit()) {
                if (currAccCon.getOverdraftLimit() == 0) {
                    throw new InsufficientBalanceException();
                }
                else {
                    throw new OverdraftLimitExceedException(currAccCon.getOverdraftLimit());
                }
            }
            accNum = currAccCon.getAccNo();
            T= currAccCon.addWithdrawal(amt);
            writeLedger(accNum, T);
        } catch (WithdrawalLimitExceedException e) {
            System.out.println(e.getMessage());
        } catch (OverdraftLimitExceedException e) {
            System.out.println(e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        }

        //String inputUsername = "sample";
        //String inputPin = "1111";

        //User newUser = new User(inputUsername, inputPin);
        //UserController newUserCon = new UserController(newUser, new UserView(dos));


        //String newCurrentAccNo = generateCurrAccNo(ledger);
        // //newUser.addCurrentAcc(newCurrentAccNo);
        // Account newCurrentAcc = ledger.get("8007424313");
        // AccountController currAccCon = new CurrentAccountController((CurrentAcc)newCurrentAcc, new CurrentAccView());
        // //currAccCon.addOpeningTransaction();

        // //ledger.put(newCurrentAccNo, newCurrentAcc);
        // //userStore.put(inputUsername, newUser);

        // writeLedger(currAccCon.getAccNo(), currAccCon.addDeposit(311111));
        // writeLedger(currAccCon.getAccNo(), currAccCon.addDeposit(322222));
        // writeLedger(currAccCon.getAccNo(), currAccCon.addDeposit(333333));
        // writeLedger(currAccCon.getAccNo(), currAccCon.addDeposit(344444));
        // writeLedger(currAccCon.getAccNo(), currAccCon.addWithdrawal(555555));

        System.out.println(currAccCon.printTransactionListing());
        System.out.println(currAccCon.printOverdraftLimit());
        System.out.println(currAccCon.printWithdrawalLimit());
    }

    public static String generateCurrAccNo(ConcurrentHashMap<String, Account> ledger) {
        String str = String.format("%d", 8000000000l + r.nextLong(10000000));
        if (!ledger.containsKey(str)){
            return str;
        }
        else {
            return generateCurrAccNo(ledger);
        }
    }

    public static String generateSavAccNo(ConcurrentHashMap<String, Account> ledger) {
        String str = String.format("%d", 4000000000l + r.nextLong(10000000));
        if (!ledger.containsKey(str)){
            return str;
        }
        else {
            return generateCurrAccNo(ledger);
        }
    }

    public static boolean writeLedger(String accNo, Transaction t) {
        String str = "";
        String csvFilename = "Ledger.csv";

        str += accNo + ",";
        str += t.getTransactionDate() + ",";
        str += t.getDescription() + ",";
        str += t.getChequeNo() + ",";
        str += t.getValueDate() + ",";
        str += String.format("%.2f", (double)t.getWithdraw()/100) + ",";
        str += String.format("%.2f", (double)t.getDeposit()/100) + ",";
        str += String.format("%.2f", (double)t.getRunningBalance()/100);  

        try {
            FileWriter fw = new FileWriter(csvFilename,true);
            fw.append("\n");
            fw.append(str);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false;
        }
    }

    public static boolean initData(ConcurrentHashMap<String, Account> data){
        try {
            Scanner scan = new Scanner(new File("Ledger.csv"));
            //skip header
            scan.nextLine();
            //iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String accountNum = lineScan.next();
                String transDate = lineScan.next();
                String details = lineScan.next();
                String chqNum = lineScan.next();
                String valueDate = lineScan.next();
                long wAmt = 0, dAmt = 0;
                try {
                    wAmt =  (long)(Double.parseDouble(lineScan.next())*100);
                } catch (Exception e) {
                    //do nothing
                }
                try {
                    dAmt = (long)(Double.parseDouble(lineScan.next())*100);
                } catch (Exception e) {
                    //do nothing
                }
                //skips balance amt
                lineScan.next();
                //close line scanner
                lineScan.close();
                long runningBal = 0;

                if (data.get(accountNum) == null) {
                    Account newAcc = new CurrentAcc(accountNum);
                    Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt, runningBal-wAmt+dAmt);
                    newAcc.addTransaction(newT);
                    data.put(accountNum, newAcc);
                } else {
                    runningBal = data.get(accountNum).getBalance();
                    Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt, runningBal-wAmt+dAmt);
                    data.get(accountNum).addTransaction(newT);
                }
            }
            //close file scanner
            scan.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean initHeader(ConcurrentHashMap<String, Account> data) {
        try {
            Scanner scan = new Scanner(new File("AccountHeaders.csv"));
            //skip header
            scan.nextLine();
            //iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String accountNum = lineScan.next();
                long wLimit = 0, oLimit = 0;
                try {
                    wLimit =  (long)(Double.parseDouble(lineScan.next())*100);
                } catch (Exception e) {
                    //do nothing
                }
                try {
                    oLimit = (long)(Double.parseDouble(lineScan.next())*100);
                } catch (Exception e) {
                    //do nothing
                }
                //skips balance amt
                lineScan.next();
                //close line scanner
                lineScan.close();

                if (data.get(accountNum) == null){
                    throw new Exception("Account number not initialised");
                }

                if (accountNum.charAt(0) == '4') {
                    SavingsAcc newAcc = (SavingsAcc)data.get(accountNum);
                    newAcc.setWithdrawalLimit(wLimit);
                }

                if (accountNum.charAt(0) == '8'){
                    CurrentAcc newAcc = (CurrentAcc)data.get(accountNum);
                    newAcc.setWithdrawalLimit(wLimit);
                    newAcc.setOverdraftLimit(oLimit);
                }


            }
            //close file scanner
            scan.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean initUserStore(ConcurrentHashMap<String, User> data, ConcurrentHashMap<String, Account> ledger) {
        try {
            Scanner scan = new Scanner(new File("UserStore.csv"));
            //skip header
            scan.nextLine();
            //iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String username = lineScan.next();
                String pin = lineScan.next();
                String accountNum = lineScan.next();
                lineScan.close();

                if (data.get(username) == null){
                    User newUser = new User(username, pin);
                    data.put(username,newUser);

                    UserController userCon = new UserController(newUser, new UserView());
                    userCon.addAcc(accountNum, ledger.get(accountNum));
                }
                else {
                    UserController userCon = new UserController(data.get(username), new UserView());
                    userCon.addAcc(accountNum, ledger.get(accountNum));
                }
            }
            //close file scanner
            scan.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
