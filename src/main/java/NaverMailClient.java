import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NaverMailClient extends JFrame implements ViewObserver {
    public static final String cardLogoutPanel = "LogoutPanel";
    public static final String cardLoginPanel = "LoginPanel";
    public static final String cardMailPanel = "MailPanel";
    public static final String cardFormPanel = "FormPanel";


    // Home
    private JPanel MainPanel;
    private JPanel MainCardPanel; // CardLayout
    private CardLayout MainCard;
    private JPanel LogoutPanel;
    private JPanel LoginPanel;

    // 1. Home/LogoutPanel
    private JTextField senderEmail;
    private JPasswordField password;
    public JButton LoginBtn;


    // 2. Home/LoginPanel
    private JPanel MenuPanel;
    private JPanel ContentCardPanel; // CardLayout
    private CardLayout ContentCard;

    // 2.1 Home/LoginPanel > MenuPanel
    public JButton MailListBtn;
    public JButton MailSendBtn;
    public JButton LogoutBtn;

    // 2.2.1 Home/LoginPanel > ContentPanel/MailListPanel
    private JPanel MailListPanel;

    // 2.2.2 Home/LoginPanel > ContentPanel/FormPanel
    private JPanel FormPanel;
    private JTextField receiverEmail;
    private JTextField subject;
    private JTextArea text;
    public JButton Submit;
    //






    public NaverMailClient() {
        setContentPane(MainPanel);
        setTitle("Sju_mail");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
//        setLayout(null);
//        setVisible(true);

        ContentCard = (CardLayout) ContentCardPanel.getLayout();
        MainCard = (CardLayout) MainCardPanel.getLayout();
    }

    public void setListener(ActionListener listener){
        Submit.addActionListener(listener);
        MailListBtn.addActionListener(listener);
        MailSendBtn.addActionListener(listener);
        LoginBtn.addActionListener(listener);
        LogoutBtn.addActionListener(listener);
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
        return text.getText();
    }

    public void changeContentCard(String name){
        ContentCard.show(ContentCardPanel, name);
    }
    public void changeMainCard(String name){
        MainCard.show(MainCardPanel, name);
    }
}
