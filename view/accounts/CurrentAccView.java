package view.accounts;

import java.io.DataOutputStream;
import java.io.IOException;

public class CurrentAccView extends AccountView{
    public CurrentAccView(DataOutputStream dos) {
        super(dos);
    }

    public String printOverdraftLimit(long amt) throws IOException {
        String str = "The current overdraft limit is : $" + String.format("%.2f", (double)amt/100);
        return str;
    }
}
