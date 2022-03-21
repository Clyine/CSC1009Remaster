package datastore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import controller.*;
import view.*;
import model.*;

public class DataStore {

    private ConcurrentHashMap<String, User> transactionsStore;
    private ConcurrentHashMap<String, String> userStore;
    private ConcurrentHashMap<String, String> userLookup;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DataStore() {
        this.transactionsStore = new ConcurrentHashMap<String, User>();
        this.userStore = new ConcurrentHashMap<String, String>();
        this.userLookup = new ConcurrentHashMap<String, String>();
        initUserStore(userStore);
        initData(transactionsStore, userStore);
        initHeader(transactionsStore);

    }

    public ConcurrentHashMap<String, User> getTransactionsStore() {
        return this.transactionsStore;
    }

    public ConcurrentHashMap<String, String> getUserStore() {
        return this.userStore;
    }

    public ConcurrentHashMap<String, String> getUserLookup() {
        return this.userLookup;
    }

    public boolean initData(ConcurrentHashMap<String, User> data, ConcurrentHashMap<String, String> userStore) {
        try {
            Scanner scan = new Scanner(new File("Ledger.csv"));
            // skip header
            scan.nextLine();
            // iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String username = lineScan.next();
                String accountNum = lineScan.next();
                String transDate = lineScan.next();
                String details = lineScan.next();
                String chqNum = lineScan.next();
                String valueDate = lineScan.next();
                long wAmt = 0, dAmt = 0;
                try {
                    wAmt = (long) (Double.parseDouble(lineScan.next()) * 100);
                } catch (Exception e) {
                    // do nothing
                }
                try {
                    dAmt = (long) (Double.parseDouble(lineScan.next()) * 100);
                } catch (Exception e) {
                    // do nothing
                }
                // skips balance amt
                lineScan.next();
                // close line scanner
                lineScan.close();
                long runningBal = 0;
                // If current user is not in memory
                if (data.get(username) == null) {
                    User myUser = new User(username, userStore.get(username));
                    UserController myUserCon = new UserController(myUser, new UserView());
                    if (myUserCon.getAccountList().get(accountNum) == null) {
                        Account newAcc;
                        if (accountNum.charAt(0) == '4') {
                            newAcc = new SavingsAcc(accountNum);
                        } else {
                            newAcc = new CurrentAcc(accountNum);
                        }
                        Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt, runningBal - wAmt + dAmt);
                        newAcc.addTransaction(newT);
                        myUserCon.getAccountList().put(accountNum, newAcc);
                        data.put(username, myUser);
                    }
                    else {
                        runningBal = myUserCon.getAccountList().get(accountNum).getBalance();
                        Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt,runningBal - wAmt + dAmt);
                        myUserCon.getAccountList().get(accountNum).addTransaction(newT);
                    }
                } else {
                    User myUser = data.get(username);
                    UserController myUserCon = new UserController(myUser, new UserView());
                    if (myUserCon.getAccountList().get(accountNum) == null) {
                        Account newAcc;
                        if (accountNum.charAt(0) == '4') {
                            newAcc = new SavingsAcc(accountNum);
                        } else {
                            newAcc = new CurrentAcc(accountNum);
                        }
                        Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt, runningBal - wAmt + dAmt);
                        newAcc.addTransaction(newT);
                        myUserCon.getAccountList().put(accountNum, newAcc);
                        data.put(username, myUser);
                    } else {
                        runningBal = myUserCon.getAccountList().get(accountNum).getBalance();
                        Transaction newT = new Transaction(transDate, valueDate, chqNum, details, wAmt, dAmt,runningBal - wAmt + dAmt);
                        myUserCon.getAccountList().get(accountNum).addTransaction(newT);
                    }
                }

            }
            // close file scanner
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean initHeader(ConcurrentHashMap<String, User> data) {
        try {
            Scanner scan = new Scanner(new File("AccountHeaders.csv"));
            // skip header
            scan.nextLine();
            // iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String username = lineScan.next();
                String accountNum = lineScan.next();
                long wLimit = 0, oLimit = 0;
                try {
                    wLimit = (long) (Double.parseDouble(lineScan.next()) * 100);
                } catch (Exception e) {
                    // do nothing
                }
                try {
                    oLimit = (long) (Double.parseDouble(lineScan.next()) * 100);
                } catch (Exception e) {
                    // do nothing
                }
                // close line scanner
                lineScan.close();

                if (this.userLookup.get(accountNum) == null) {
                    this.userLookup.put(accountNum, username);
                }

                if (data.get(username).getAccountList().get(accountNum) == null) {
                    throw new Exception("Account number not initialised");
                }

                if (accountNum.charAt(0) == '4') {
                    SavingsAcc newAcc = (SavingsAcc) data.get(username).getAccountList().get(accountNum);
                    newAcc.setWithdrawalLimit(wLimit);
                }

                if (accountNum.charAt(0) == '8') {
                    CurrentAcc newAcc = (CurrentAcc) data.get(username).getAccountList().get(accountNum);
                    newAcc.setWithdrawalLimit(wLimit);
                    newAcc.setOverdraftLimit(oLimit);
                }

            }
            // close file scanner
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean initUserStore(ConcurrentHashMap<String, String> data) {
        try {
            Scanner scan = new Scanner(new File("UserStore.csv"));
            // skip header
            scan.nextLine();
            // iterate Line by Line
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");
                String username = lineScan.next();
                String pin = lineScan.next();
                lineScan.close();
                data.put(username, pin);
            }
            // close file scanner
            scan.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean authUser(String user, String pin) {
        return userStore.get(user).equals(pin);
    }

    public boolean writeLedger(SystemController controller, Transaction t) {
        String str = "";
        String csvFilename = "Ledger.csv";

        str += controller.getUsername() + ",";
        str += controller.getAccNo() + ",";
        str += t.getTransactionDate() + ",";
        str += t.getDescription() + ",";
        str += t.getChequeNo() + ",";
        str += t.getValueDate() + ",";
        str += String.format("%.2f", (double) t.getWithdraw() / 100) + ",";
        str += String.format("%.2f", (double) t.getDeposit() / 100) + ",";
        str += String.format("%.2f", (double) t.getRunningBalance() / 100)+ ",";

        try {
            FileWriter fw = new FileWriter(csvFilename, true);
            fw.append("\n");
            fw.append(str);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false;
        }
    }

    public boolean writeUser(String username, String pin) {
        String str = "";
        String csvFilename = "UserStore.csv";

        str += username + ",";
        str += pin;

        try {
            FileWriter fw = new FileWriter(csvFilename, true);
            fw.append("\n");
            fw.append(str);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false;
        }
    }

    public boolean writeNewUserAcc(String username) {
        String str = "";
        String csvFilename = "Ledger.csv";

        str += username + ",,";
        str += dateFormat.format(new Date()) + ",New user Account,,,,,,";
        try {
            FileWriter fw = new FileWriter(csvFilename, true);
            fw.append("\n");
            fw.append(str);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false;
        }

    }

    public boolean writeHeader(SystemController controller) {
        String str = "";
        String csvFilename = "AccountHeaders.csv";

        str += controller.getUsername() + ",";
        str += controller.getAccNo() + ",";
        str += String.format("%.2f", (double) controller.getWithdrawalLimit() / 100)+ ",";
        str += String.format("%.2f", (double) controller.getOverdraftLimit() / 100 * -1)+ ",";

        try {
            FileWriter fw = new FileWriter(csvFilename, true);
            fw.append("\n");
            fw.append(str);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false;
        }
    }

    public String getUsername(String accNum) {
        return this.userLookup.get(accNum);
    }
}
