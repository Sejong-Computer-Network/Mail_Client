import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MailController implements ActionListener {
    private final NaverMailClient view;
    private final MailModel model;
    private final MailNet net;

    private boolean errorDisplayed = false;

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

                if (result){
                    // 성공!
                    javax.swing.JOptionPane.showMessageDialog(null, "로그인되었습니다.", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
        else if (o == view.attachFile) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(view.getContentPane());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                view.attachedFileName.setText(file.getName());
                view.attachedFile = file;
                view.deleteAttachment.setVisible(true);
            }
        } else if (o==view.deleteAttachment) {
           view.attachedFile = null;
           view.attachedFileName.setText("");
           view.deleteAttachment.setVisible(false);
        } else if(o == view.Submit){
            // login, handshake, send, close?
            String receiverEmail = view.getRecieverEmail();
            String subject = view.getSubject();
            String text = view.getText();
            File file = view.getAttachedFile();
            String response ;

            try {
                System.out.println("submit pressed");
                System.out.println("email, password:" + model.getSenderEmail() +" "+ new String(model.getPassword()));
                net.SocketSetup(465, "smtp.naver.com");
                net.AuthLogin(model.getSenderEmail(), model.getPassword());
                response=net.sendMail(
                        receiverEmail,
                        subject,
                        text,
                        file
                        );
                if(response.startsWith("5") || response.startsWith("4"))
                {
                    if (!errorDisplayed) {
                        javax.swing.JOptionPane.showMessageDialog(null, response, "error_message", javax.swing.JOptionPane.ERROR_MESSAGE);
                        // 상태 변수를 true로 설정하여 더 이상 오류 메시지가 출력되지 않도록 합니다.
                        errorDisplayed = true;
                    }
                }
                else if(!errorDisplayed)
                {
                    javax.swing.JOptionPane.showMessageDialog(null, "이메일이 정상적으로 전송되었습니다!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
                net.quit();
                errorDisplayed=false;
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
