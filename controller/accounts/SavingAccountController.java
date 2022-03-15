package controller.accounts;

import java.io.IOException;

import model.accounts.SavingsAcc;
import view.accounts.SavingAccView;

public class SavingAccountController extends AccountController{

    private final SavingsAcc account = (SavingsAcc) super.model;
    private final SavingAccView view = (SavingAccView) super.view;

    public SavingAccountController(SavingsAcc account, SavingAccView view) {
        super(account, view);
    }

    public String printOverdraftLimit() throws IOException{
        return view.printOverdraftLimit(getOverdraftLimit());
    }

    public long getOverdraftLimit() {
        return 0;
    }

    public long setOverdraftLimit(long amt) {
        return 0;
    }
}
