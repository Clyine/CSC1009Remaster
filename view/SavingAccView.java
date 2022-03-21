package view;

public class SavingAccView extends AccountView {
    public SavingAccView() {
        super();
    }

    public String printOverdraftLimit(long amt){
        return "This is a savings account. Overdraft is not available.";
    }
    
}
