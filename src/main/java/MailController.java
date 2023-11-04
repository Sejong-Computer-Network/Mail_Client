import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        }
    }
}
