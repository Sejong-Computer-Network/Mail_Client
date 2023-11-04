import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendMail extends JFrame implements ViewObserver {
    private JTextField senderEmail;
    public JButton Submit;
    private JTextArea textArea1;
    private JTextField receiverEmail;
    private JPanel MainPanel;
    private JPasswordField password;
    private JTextField Title;

    public SendMail() {
        setContentPane(MainPanel);
        setTitle("Sju_mail");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
//        setLayout(null);
//        setVisible(true);

        Submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void setListener(ActionListener listener){
        Submit.addActionListener(listener);
    }

    public String getSenderEmail(){
        return
    }
}
