package controller;
import java.io.IOException;

import model.CurrentAcc;
import view.CurrentAccView;

public class CurrentAccountController extends AccountController{

    private final CurrentAcc model = (CurrentAcc) super.model;
    private final CurrentAccView view = (CurrentAccView) super.view;

    public CurrentAccountController(CurrentAcc model, CurrentAccView view) {
        super(model, view);
    }

    public long getOverdraftLimit() {
        return this.model.getOverdraftLimit();
    }

    public long setOverdraftLimit(long amt) {
        return this.model.setOverdraftLimit(amt);
    }

    //VIEW METHODS
    public String printOverdraftLimit() throws IOException {
        return view.printOverdraftLimit(getOverdraftLimit());
    }

}