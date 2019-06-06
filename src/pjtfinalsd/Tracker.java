package pjtfinalsd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author brena
*/
public class Tracker {

    private static ServerSocket welcomeSocket;
    private static Socket connectionSocket;
    private static Socket clientSocket;
    
    private Integer port = 1111;
    
    public static void main(String[] args) throws Exception {
        Tracker tracker = new Tracker();
        
        
        while(true){
            
            tracker.createConnection();

        String nomeArq = "";
        nomeArq = receiveClient();
        System.out.println(nomeArq);
        
        welcomeSocket.close();
        connectionSocket.close();
        
      //PEGA AS INFORMACOES .TORRENT    
        //System.out.println(nomeArq.split(".")[0] + ".torrent");
        File f = new File(nomeArq);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String st, info=""; 

        while ((st = br.readLine()) != null && st.contains(";")) {
            info = st;
        }
        System.out.println("inf = " + info);
        
        //PEGA OS IPS DOS SERVIDORES
        File f1 = new File("ipsServidores.txt");
        FileReader fr1 = new FileReader(f1);
        BufferedReader br1 = new BufferedReader(fr1);
        String st1, info1=""; 

        while ((st1 = br1.readLine()) != null && st1.contains(",")) {
            info1 = st1;
        }
        
        List<String> ips = Arrays.asList(info1.split("\\s*,\\s*"));
        
        String w = "";
        for(String l : ips) {
        	String port = l.split(":")[1];
        	int p = new Integer(port); 
        	clientSocket = new Socket(l.split(":")[0], p);
        	
        	DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeBytes(info.split(";")[0]);
            dataOutputStream.flush();
            dataOutputStream.close();
            
            //clientSocket.close();
            try { Thread.sleep (1000); } catch (InterruptedException ex) {}
            clientSocket = new Socket(l.split(":")[0], p);
            
            String r = receive();
            
        	if(r.equals("existente")) {
        		w+= l.split(":")[0] + ":"+p + ",";
        	}
        	clientSocket.close();
        }
        String i = w.substring(0, w.length()-1);  
        System.out.println(i);
        
            
        	//PEGA IPS QUE CONTÉM O ARQUIVO
        	
        	
            //SE DISPONIBILIZA PARA CONEXÃO
            tracker.createConnection();
            //ENVIA AS INFORMAÇÕES PARA O CLIENTE
            DataOutputStream dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
            String s = info +i;
            dataOutputStream.writeBytes(s);
            dataOutputStream.flush();
            dataOutputStream.close();
            //try { Thread.sleep (2000); } catch (InterruptedException ex) {}
            welcomeSocket.close();
            connectionSocket.close();
        }
    }
    
    private static String receive() throws IOException {
        String received = null;
        DataInputStream  dataInputStream = new DataInputStream(clientSocket.getInputStream());

        while(true) {
                if (dataInputStream.available() > 0) {
                        received = dataInputStream.readLine();
                        break;
                }
        }
        //dataInputStream.close();
        return received;
    }
    
    private static String receiveClient() throws IOException {
        String received = null;
        DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());

        while(true) {
                if (dataInputStream.available() > 0) {
                        received = dataInputStream.readLine();
                        break;
                }
        }
        //dataInputStream.close();
        return received;
    }
    
    private void createConnection() throws IOException {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Port "+port+" opened!");
            connectionSocket = welcomeSocket.accept();
            System.out.println("Tracker: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());		
    }
}
