import javax.swing.*;
import java.awt.event.ActionListener;

public class SendMail extends JFrame implements ViewObserver {
    private JPanel MainPanel;
    private JTextField senderEmail;
    private JPasswordField password;

    private JTextField receiverEmail;
    private JTextField subject;

    private JTextArea textArea1;


    public JButton Submit;

    public SendMail() {
        setContentPane(MainPanel);
        setTitle("Sju_mail");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
//        setLayout(null);
//        setVisible(true);

    }

    public void setListener(ActionListener listener){
        Submit.addActionListener(listener);
    }

    public String getSenderEmail(){
        return senderEmail.getText();
    }
    public String getRecieverEmail(){
        return receiverEmail.getText();
    }
    public char[] getPassword(){
        return this.password.getPassword();
    }

    public String getSubject(){
        return subject.getText();
    }

    public String getText(){
        return textArea1.getText();
    }

}
