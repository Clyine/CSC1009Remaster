package view.accounts;

import java.io.DataOutputStream;

public class SavingAccView extends AccountView {
    public SavingAccView(DataOutputStream dos) {
        super(dos);
    }

    public String printOverdraftLimit(long amt){
        return "This is a savings account. Overdraft is not available.";
    }
    
}
