import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MailController implements ActionListener {
    private final NaverMailClient view;
    private final MailModel model;
    private final MailNetClient net;

    public MailController(NaverMailClient view, MailModel model, MailNetClient net){
        this.model = model;
        this.view = view;
        this.net = net;

        this.view.setListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o==view.LoginBtn){
            String senderEmail = view.getSenderEmail();
            char[] senderPassword = view.getPassword();
            try {
                net.SocketSetup(465, "smtp.naver.com");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                boolean result = true;

                net.AuthLogin(senderEmail, senderPassword);

                if (result){
                    // 성공!
                    model.setSenderEmail(senderEmail);
                    model.setPassword(senderPassword);
                    view.changeMainCard(NaverMailClient.cardLoginPanel);

                }
                else {
                    // 실패!
                    javax.swing.JOptionPane.showMessageDialog(view, "로그인에 실패했습니다. 올바른 정보를 입력하세요.", "로그인 실패", javax.swing.JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }



        }
        else if (o == view.MailListBtn){
//            System.out.println(e.get);
            view.changeContentCard(NaverMailClient.cardMailPanel);
        }
        else if(o == view.MailSendBtn){
            view.changeContentCard(NaverMailClient.cardFormPanel);
        }
        else if(o == view.Submit){
            // login, handshake, send, close?
            String senderEmail = view.getSenderEmail();
            char[] senderPassword = view.getPassword();
            String receiverEmail = view.getRecieverEmail();

            model.setSenderEmail(senderEmail);
            model.setPassword(senderPassword);




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
