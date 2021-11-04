import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class Receive_Mail {
	public static void main(String[] args) throws Exception {
		fetchMail();
	}

	static Session session;

	public static void fetchMail() {
		try {
			Properties props = System.getProperties();
			props.put("mail.pop3.host", "localhost");
			props.put("mail.store.protocol", "pop3");
			session = Session.getInstance(props);

			Store sessionStore = session.getStore();
			sessionStore.connect("localhost", "labrat", "kn1lab");

			Folder inbox = sessionStore.getFolder("INBOX");
			inbox.open(1);
			Message[] messages = inbox.getMessages();

			for (Message m : messages) {
				for (Address a : m.getFrom()) {
					System.out.println("From: " + a.toString());
				}
				for (Address a : m.getAllRecipients()) {
					System.out.println("To: " + a.toString());
				}
				System.out.println("Sent on: " + m.getSentDate().toString());
				System.out.println("Subject: " + m.getSubject());
				System.out.println(m.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
