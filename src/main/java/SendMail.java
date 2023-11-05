import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.Normalizer;

public class SendMail extends JFrame implements ViewObserver {
    private JPanel MainPanel;
    private JPanel MenuPanel;
    private JPanel ContentPanel; // CardLayout
    private JPanel FormPanel;
    private JPanel MailListPanel;

    private CardLayout ContentCard;
//    private LayoutManager layout;


    // MenuPanel
    public JButton MailListBtn;
    public JButton MailSendBtn;


    // FormPanel
    private JTextField receiverEmail;
    private JTextField senderEmail;
    private JPasswordField password;
    private JTextField subject;
    private JTextArea text;
    public JButton Submit;








    public SendMail() {
        setContentPane(MainPanel);
        setTitle("Sju_mail");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
//        setLayout(null);
//        setVisible(true);
//        System.out.println(ContentPanel.getLayout().getClass().getName()); -> java.awt.CardLayout
//        ContentCard = new CardLayout();

//        layout = ContentPanel.getLayout();


        ContentCard = (CardLayout) ContentPanel.getLayout();
        if(ContentPanel.getLayout() !=ContentCard){
            System.out.println("False");
        }
//        ContentCard.
//        CardLayout cardLayout = (CardLayout) yourPanel.getLayout();
        Component[] components = ContentPanel.getComponents();

        for (Component component : components) {
            String name = component.getName();
            if (name != null) {
                System.out.println("Component name: " + name);
            }
        }

    }

    public void setListener(ActionListener listener){
        Submit.addActionListener(listener);
        MailListBtn.addActionListener(listener);
        MailSendBtn.addActionListener(listener);
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

    public void changeCard(String name){
        System.out.println("changeCard called: "+name);
        ContentCard.show(ContentPanel, name);
//        ContentCard.next(ContentPanel);
        ContentPanel.validate();
    }
}
