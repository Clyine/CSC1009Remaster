package exceptions;

public class NegativeInputException extends Exception{
    String message = "Inputs must be greater than 0.";

    @Override
    public String getMessage(){
        return this.message;
    }
}
