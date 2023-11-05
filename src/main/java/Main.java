import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NaverMailClient sendMailView = new NaverMailClient();
                MailModel model = new MailModel();
                MailNet netClient = new MailNet();
                model.addObserver(sendMailView);

//                model.reset();
                MailController controller = new MailController(sendMailView, model, netClient);
                sendMailView.setVisible(true);
            }
        });

    }
}
