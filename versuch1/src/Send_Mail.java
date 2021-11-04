import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Send_Mail {
	public static void main(String[] args) {
		sendMail();   
	}
	
	public static void sendMail() {
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", "localhost");
			Session session = Session.getInstance(props);

			Message msg = new MimeMessage(session);
			msg.setSubject("First mail subject");
			msg.setSentDate(new Date());
			msg.setFrom(new InternetAddress("sender@localhost"));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress("labrat@localhost"));
			msg.setText("First mail text");

			Transport.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
