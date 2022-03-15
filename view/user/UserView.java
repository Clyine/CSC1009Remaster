package view.user;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class UserView {
    private DataOutputStream dos;

    public UserView(DataOutputStream dos) {
        this.dos = dos;
    }
}
