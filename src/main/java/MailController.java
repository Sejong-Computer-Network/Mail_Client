import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MailController implements ActionListener {
    private SendMail view;
    private MailModel model;
    private MailNetClient net;

    public MailController(SendMail view, MailModel model, MailNetClient net){
        this.model = model;
        this.view = view;
        this.net = net;

        this.view.setListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == view.Submit){
            // login, handshake, send, close?
            String senderEmail = view.getSenderEmail();
            char[] senderPassword = view.getPassword();
            String receiverEmail = view.getRecieverEmail();

            model.setSenderEmail(senderEmail);
            model.setPassword(senderPassword);
            try {
                net.SocketSetup(465, "smtp.naver.com");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                net.AuthLogin(senderEmail, senderPassword);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                net.sendMail(
                        receiverEmail,
                        view.getSubject(),
                        view.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            try {
                net.quit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
