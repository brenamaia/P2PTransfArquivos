package pjtfinalsd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 *
 * @author brena
 */
public class PeerServidor {
    private static ServerSocket welcomeSocket;
    private static Socket connectionSocket;
    
    private static Integer port = 2000;
    
    public static void main(String[] args) throws Exception {
        
        
        while(true){
            PeerServidor ps = new PeerServidor();
        System.out.println("Port "+port+" opened!");
        ps.createConnection();
        System.out.println("Peer Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());
        
        String received1 = receive();
        //System.out.println(received1);
        welcomeSocket.close();
        connectionSocket.close();
        
        ps.createConnection();
        disponibilidade(received1);
        	
            
            ps.createConnection();            		

            String received = receive();
            
            String numb = received.split(";")[1];
            int numBloco = new Integer(numb);
            
            //LER PDF
            
            String texto = ps.lerPDF(received);
            
            String bloco = texto.split(numBloco + ".")[1]; // pega o texto após do bloco que foi definido
            bloco = bloco.replace("\n"," ");
            bloco = bloco.trim().replaceAll(" +", " ");
            bloco = numBloco + ". " + bloco;
            
            System.out.println(bloco);
            
            List<String> listPalavras = Arrays.asList(bloco.split(" "));
                        
            bloco = "";
            String np = received.split(";")[2];
            int numP = new Integer(np);

            int j=0;
            while(j < numP){
                if(numP - j == 1){
                    bloco += listPalavras.get(j) + "\n";
                }else{
                    bloco += listPalavras.get(j) + " ";
                }
                
                j++;
            }
                        
            welcomeSocket.close();
            connectionSocket.close();
            
            //talvez remover dps
            
            ps.createConnection();
            
            //

            OutputStream socketStream = connectionSocket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
            objectOutput.writeObject(bloco);
            objectOutput.close();
            socketStream.close();
            
            welcomeSocket.close();
            connectionSocket.close();
        }
        
    }
    
    public static void disponibilidade(String file) throws Exception {
    	String a = lerPDF(file);
    	//System.out.println(a);
    	    	
    	if(a == "inexistente") {
    		DataOutputStream dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
    		dataOutputStream.writeBytes("inexistente");
    		dataOutputStream.flush();
            dataOutputStream.close();
    	}else {
    		DataOutputStream dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
    		dataOutputStream.writeBytes("existente");
    		dataOutputStream.flush();
            dataOutputStream.close();
    	}
    	
        welcomeSocket.close();
        connectionSocket.close();
    	
    }
    
    
    private static final String FILE_NAME = "tcc.pdf";

	public static String lerPDF(String received) throws Exception {

        PdfReader reader;
        String textFromPage=null;
        try {

            reader = new PdfReader(received.split(";")[0]);

            // pageNumber = 1
            
            textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
            
            //System.out.println(getHash(textFromPage));
            
            //System.out.println(textFromPage);

            reader.close();
            
        } catch (IOException e) {
        	textFromPage = "inexistente"; 
        }
        return textFromPage;

    }
    
    
    public static String lerPDF1(String received){
    	PDFManager pdfManager = new PDFManager();
        pdfManager.setFilePath(System.getProperty("user.dir") + System.getProperty("file.separator")  +received.split(";")[0]);
        String text=null;
        try {
            text = pdfManager.toText();
            //System.out.println(text);
        } catch (IOException ex) {
            text = "inexistente"; 
        }
		return text;
    	
    }
    
    private void createConnection() throws IOException {
            welcomeSocket = new ServerSocket(port);
            //System.out.println("Port "+port+" opened!");
            connectionSocket = welcomeSocket.accept();
            //System.out.println("Peer Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());		
    }
    
    private static String receive() throws IOException {
        String received = null;
        DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());
        while(true) {
                if (dataInputStream.available() > 0) {
                    System.out.println("entrou receive");
                    received = dataInputStream.readLine();
                    System.out.println(received);
                    return received;
                }
        }
        
    }
    
    public static String getHash(String texto) throws Exception {

        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(texto.getBytes(),0,texto.length());
        return new BigInteger(1,m.digest()).toString(16);
     }
    
}