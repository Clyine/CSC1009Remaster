package exceptions;

public class InsufficientBalanceException extends Exception{
    String msg = "Your account has insuffient balance";

    @Override
    public String getMessage(){
        return this.msg;
    }
}
