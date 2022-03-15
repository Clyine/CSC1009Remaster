package controller.user;
import model.user.*;
import view.user.*;

public class UserController {
    private User model;
    private UserView view;

    public UserController(User model, UserView view) {
        this.model = model;
        this.view = view;
    }

    public String getUsername() {
        return model.getUsername();
    }

    public String getPin() {
        return model.getPin();
    }

    public String setPin(String pin) {
        model.setPin(pin);
        return model.getPin();
    }
}
