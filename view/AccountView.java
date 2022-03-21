package view;

import java.io.IOException;

public abstract class AccountView {
    
    protected AccountView() {
    }

    public String printBalance(long balance) throws IOException {
        String str = "Your current balance is $" + String.format("%.2f", ((double)balance/100));
        return str;
    }

    public String printTransactionListing(String str) throws IOException {
        return str;
    }

    public String printWithdrawalLimit(long balance) throws IOException {
        String str = "Your current withdrawal limit is $" + String.format("%.2f", ((double)balance/100));
        return str;
    }

    public abstract String printOverdraftLimit(long amt) throws IOException;
}
