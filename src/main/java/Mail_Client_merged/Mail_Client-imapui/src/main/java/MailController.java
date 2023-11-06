import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MailController implements ActionListener {
    private final NaverMailClient view;
    private final MailModel model;
    private final MailNet net;

    public MailController(NaverMailClient view, MailModel model, MailNet net){
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
                net.IMAPSocketSetup(993, "imap.naver.com");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                boolean result = net.AuthLogin(senderEmail, senderPassword);
//                result = net.sendMail_login("kochinko11@senderEmail", null, senderPassword.toString());

                if (result){
                    // 성공!
                    model.setSenderEmail(senderEmail);
                    model.setPassword(senderPassword);
                    view.changeMainCard(NaverMailClient.cardLoginPanel);
                    net.quit();
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
            view.changeContentCard(NaverMailClient.cardMailPanel);
        }
        else if(o == view.MailSendBtn){
            view.changeContentCard(NaverMailClient.cardFormPanel);
        }
        else if(o == view.Submit){
            // login, handshake, send, close?
            String receiverEmail = view.getRecieverEmail();
            String subject = view.getSubject();
            String text = view.getText();

            try {
                System.out.println("submit pressed");
                System.out.println("email, password:" + model.getSenderEmail() +" "+ new String(model.getPassword()));
                net.SocketSetup(465, "smtp.naver.com");
                net.AuthLogin(model.getSenderEmail(), model.getPassword());
                net.sendMail(
                        receiverEmail,
                        subject,
                        text);
                net.quit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        else if(o == view.LogoutBtn){
//          net.quit();
            // view 초기화, model 초기화, Net 연결끊기+초기화
            model.setPassword(null);
            model.setSenderEmail(null);
            view.reset();
            view.changeMainCard(NaverMailClient.cardLogoutPanel);

        }
        else if (o == view.loadBtn) {
            try{
                net.IMAPGetMSG();
                view.showMailList(net.mailText);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}
