import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SendMail sendMailView = new SendMail();
                MailModel model = new MailModel();
                MailNetClient netClient = new MailNetClient();
                model.addObserver(sendMailView);

//                model.reset();
                MailController controller = new MailController(sendMailView, model, netClient);
                sendMailView.setVisible(true);
            }
        });
//    }
//        new Sju_mail();
//    }
    }
}
