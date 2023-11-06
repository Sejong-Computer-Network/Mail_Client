import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Base64;

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
    public JButton loadBtn;
    private JScrollPane maillist;
    private JLabel maillistname;
    private JTable mailTable;

    // 2.2.2 Home/LoginPanel > ContentPanel/FormPanel
    private JPanel FormPanel;
    private JTextField receiverEmail;
    private JTextField subject;
    private JTextArea text;
    public JButton Submit;
    public JButton attachFile; // 파일 선택 버튼
    public JLabel attachedFileName;
    public JButton deleteAttachment;
    public File attachedFile;


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
        attachFile.addActionListener(listener);
        deleteAttachment.addActionListener(listener);
        loadBtn.addActionListener(listener);
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

    public File getAttachedFile() {
        return attachedFile;
    }

    public void changeContentCard(String name){
        ContentCard.show(ContentCardPanel, name);
    }
    public void changeMainCard(String name){
        MainCard.show(MainCardPanel, name);
    }


    @Override
    public void reset(){
        senderEmail.setText("");
        password.setText("");
        subject.setText("");
        text.setText("");
        receiverEmail.setText("");
        attachedFile = null;
        attachedFileName.setText("");
        deleteAttachment.setVisible(false);
    }

    public void showMailList(String[] mailText){
        String[] col = {"Category", "Contents"};
        Object[][] data = new Object[mailText.length*20][2];

        int idx = 0;
        for (int i = 0; i < mailText.length; i++) {
            String[] lines = mailText[i].split("\n");
            int flag = 0;

            for (int j = 0; j < lines.length; j++) {
                String line = lines[j].trim();
                if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2); // Only split on the first ':'

                    if (parts[0].startsWith("DATE") || parts[0].startsWith("SUBJECT") || parts[0].startsWith("FROM")) {
                        data[idx][0] = parts[0].trim();
                        data[idx][1] = parts[1].trim();
                        idx++;
                    } else if (parts[0].startsWith("BODY[TEXT]") || flag == 1) {
                        if (flag == 1) {
                            data[idx][1] += new String(Base64.getDecoder().decode(parts[0])) + "\n";
                        } else {
                            data[idx][0] = parts[0].trim();
                            data[idx][1] = new String(Base64.getDecoder().decode(parts[1]));
                        }
                        flag = 1;
                    }
                } else if(flag == 1) { // If the line doesn't contain ':', and we are in the 'TEXT' section.
                    data[idx][1] += new String(Base64.getDecoder().decode(line)) + "\n";
                }
            }

            idx++;
            data[idx][0] = "";
            data[idx][1] = "";
            idx++;
        }

        mailTable = new JTable(data, col);
        maillist.setViewportView(mailTable);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
}
