import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Die "Klasse" Sender liest einen String von der Konsole und zerlegt ihn in einzelne Worte. Jedes Wort wird in ein
 * einzelnes {@link Packet} verpackt und an das Medium verschickt. Erst nach dem Erhalt eines entsprechenden
 * ACKs wird das nächste {@link Packet} verschickt. Erhält der Sender nach einem Timeout von 5 Sekunden kein ACK,
 * überträgt er das {@link Packet} erneut.
 */
public class Sender {
    /**
     * Hauptmethode, erzeugt Instanz des {@link Sender} und führt {@link #send()} aus.
     * @param args Argumente, werden nicht verwendet.
     */
    public static void main(String[] args) {
        Sender sender = new Sender();
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt neuen Socket. Liest Text von Konsole ein und zerlegt diesen. Packt einzelne Worte in {@link Packet}
     * und schickt diese an Medium. Nutzt {@link SocketTimeoutException}, um 5 Sekunden auf ACK zu
     * warten und das {@link Packet} ggf. nochmals zu versenden.
     * @throws IOException Wird geworfen falls Sockets nicht erzeugt werden können.
     */
    private void send() throws IOException {
        //Text einlesen und in Worte zerlegen
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        input += " EOT";
        String[] words = input.split(" ");
        
        // Socket erzeugen auf Port 9998 und Timeout auf eine Sekunde setzen
        DatagramSocket clientSocket = new DatagramSocket(9998);
        clientSocket.setSoTimeout(1000);
        
        // Iteration über den Konsolentext
        int i = 0;
        int j = 0;
        while (j != words.length) {
            // Pakete an Port 9997 senden
            Packet packetPayload = new Packet(i, i, false, words[j].getBytes());
        
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(packetPayload);
            byte[] bufSend = b.toByteArray();
        
            DatagramPacket packetSend = new DatagramPacket(bufSend, bufSend.length, InetAddress.getByName("localhost"), 9997);
            clientSocket.send(packetSend);
        
            try {
                // Auf ACK warten und erst dann Schleifenzähler inkrementieren
                byte[] bufRec = new byte[256]; // magic number
                DatagramPacket packetAcked = new DatagramPacket(bufRec, bufRec.length);
                clientSocket.receive(packetAcked);
            
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(packetAcked.getData()));
                Packet packetIn = (Packet) is.readObject();
            
                if (packetIn.isAckFlag() && packetIn.getAckNum() != i + words[j].length()) {
                    continue; // Re-send last word
                }
            
                i += words[j].length();
                j++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                System.out.println("Receive timed out, retrying...");
            }
        }
        
        // Wenn alle Packete versendet und von der Gegenseite bestätigt sind, Programm beenden
        clientSocket.close();
        
        if(System.getProperty("os.name").equals("Linux")) {
            clientSocket.disconnect();
        }
        
        System.exit(0);
    }
}
