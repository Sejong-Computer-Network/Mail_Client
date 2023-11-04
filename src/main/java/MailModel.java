
import java.io.*;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MailModel {
    private String senderEmail;
    private char[] password;
    private ArrayList<ViewObserver> list = new ArrayList<ViewObserver>();
    public void addObserver(ViewObserver o){
        list.add(o);
    }


    public void setSenderEmail(String email){
        senderEmail = email;
//        for (ViewObserver o : list){
//            // o.updateMail
//        }
    }

    public void setPassword(char[] password){
        this.password = password;
    }


}



