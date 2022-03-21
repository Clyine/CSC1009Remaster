package exceptions;

public class OverdraftLimitExceedException extends Exception{
    String msg = "Balance after withdrawal exceeds designated overdraft limit ($";
    long amt;

    public OverdraftLimitExceedException(long amt){
        this.amt = amt;
        this.msg += String.format("%.2f", (double)amt/100) + ")";
    }

    @Override
    public String getMessage(){
        return msg;
    }
}
