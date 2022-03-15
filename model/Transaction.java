package model;

import java.io.FileWriter;

public class Transaction {
    private String transactionDate;
    private String valueDate;
    private String chequeNo;
    private String description;
    private long withdraw;
    private long deposit;
    private long runningBalance;

    public Transaction(String transDate, String valueDate, String chq, String desc, long withdraw, long deposit, long runningBalance){
        this.transactionDate = transDate;
        this.valueDate = valueDate;
        this.chequeNo = chq;
        this.description = desc;
        this.withdraw = withdraw;
        this.deposit = deposit;
        this.runningBalance = runningBalance;
    }

    @Override
    public String toString(){
        //String s = String.format("|%1$td/%1$tm/%1$tY   |%2$td/%2$tm/%2$tY   |%3$-9s|%4$-60s|%5$12.2f|%6$12.2f|%7$15.2f|", this.transactionDate, this.valueDate, this.chequeNo, this.description, (float)this.withdraw/100, (float)this.deposit/100, (float)this.runningBalance/100);
        String s = String.format("|%1$s   |%2$s  |%3$-9s|%4$-60s|%5$12.2f|%6$12.2f|%7$15.2f|", this.transactionDate, this.valueDate, this.chequeNo, this.description, (double)this.withdraw/100, (double)this.deposit/100, (double)this.runningBalance/100);

        return s;
    }
    public String getTransactionDate() {
        return this.transactionDate;
    }

    public String getValueDate() {
        return this.valueDate;
    }

    public String getChequeNo() {
        return this.chequeNo;
    }

    public String getDescription() {
        return this.description;
    }

    public long getWithdraw() {
        return this.withdraw;
    }

    public long getDeposit() {
        return this.deposit;
    }

    public long getRunningBalance() {
        return this.runningBalance;
    }


    public void exportTransaction(String accNo){
        String str = "";
        String csvFilename = "bankTransactions.csv";

        str += accNo + ",";
        str += this.transactionDate + ",";
        str += this.description + ",";
        str += this.chequeNo + ",";
        str += this.valueDate + ",";
        str += String.format("%.2f", (double)this.withdraw/100) + ",";
        str += String.format("%.2f", (double)this.deposit/100) + ",";
        str += String.format("%.2f", (double)this.runningBalance/100);  

        try {
            FileWriter fw = new FileWriter(csvFilename,true);
            fw.append("\n");
            fw.append(str);
            fw.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }
    }

}
