package model;

public class CurrentAcc extends Account{
    private long overdraftLimit = 300000;

    public CurrentAcc(String accNo) {
        super(accNo);
    }

    public long getOverdraftLimit(){
        return -this.overdraftLimit;
    }

    public long setOverdraftLimit(long limit){
        this.overdraftLimit = limit;
        return -this.overdraftLimit;
    }

}
