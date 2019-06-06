package pjtfinalsd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.*;
import java.math.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pjtfinalsd.Servidor;
import java.io.FileOutputStream;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.*;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 *
 * @author brena
*/
public class PeerRequerente{

    //private static ServerSocket welcomeSocket;
    //private static Socket connectionSocket;
    private static Socket clientSocket; 

    private Integer port = 1000;
    
    private static String nomeArq = "";
    private static String nomeB = "";
       

    public static int principal(String arquivo) throws Exception {
        
        PeerRequerente pr = new PeerRequerente();
        
        nomeArq = arquivo;
        
        //PEGA O IP E PORTA DO TRACKER     
        String info = getIpTracker();
        
        //ENVIA A SOLICITAÇÃO DE CONEXÃO PARA O TRACKER
        String porta = info.split(";")[1];
        int p = new Integer(porta);
        clientSocket = new Socket(info.split(";")[0], p);
        
        //System.out.println(arquivo);
        
        //RECEBE OS DADOS DO ARQUIVO .TORRENT E FECHA A CONEXÃO COM O TRACKER
        
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
    	dataOutputStream.writeBytes(arquivo);
        dataOutputStream.flush();
        dataOutputStream.close();
        
        clientSocket.close();
        
        try { Thread.sleep (5000); } catch (InterruptedException ex) {}
        clientSocket = new Socket(info.split(";")[0], p);
            
        String received = receive();
        clientSocket.close();
        
        System.out.println("received = "+received);
        
        //SEPARA OS DADOS RECEBIDOS
        String ip, npalav, hash=null;
        
        nomeB = received.split(";")[0];
        npalav = received.split(";")[1];
        hash = received.split(";")[2];
        ip = received.split(";")[3];
        
        List<String> ipsServidores = Arrays.asList(ip.split("\\s*,\\s*"));
        List<String> palavBlocos = Arrays.asList(npalav.split("\\s*,\\s*"));
        
        /*
        CRIA LIST PARA ORDENAR AS INFORMAÇÕES SOBRE OS SERVIDORES BLOCOS.
        COM ISSO, OS SERVIDORES FICAM ORDENADOS DE ACORDO COM A LATÊNCIA EM ORDEM DECRESCENTE,
        ENQUANTO OS BLOCOS SÃO ARMAZENADOS EM ORDEM CRESCENTE, DE ACORDO COM A QUANTIDADE DE PALAVRAS.
        */
        List<Servidor> servidor = new ArrayList();
        List<Bloco> blocoA = new ArrayList();
        List<Resultado> blocos = new ArrayList();
        
        System.out.println("size = " + ipsServidores.size());
        
        for(String k : ipsServidores){
            long start  = System.currentTimeMillis();
            getPing(k.split(":")[0]);
            long latencia = (System.currentTimeMillis() - start);
            System.out.println(latencia);
            Servidor serv = new Servidor(k, latencia);
            servidor.add(serv);
            serv.gerenciaList(servidor);
        }
          
        
        int n = 1;
        for(String e : palavBlocos){
            int qtdp = new Integer(e);
            //System.out.println(l +" "+ a);
            Bloco bloco = new Bloco(n, qtdp);
            blocoA.add(bloco);
            bloco.gerenciaList(blocoA);
            n++;
        }   
        for(Servidor a : servidor) {
        	System.out.println(a.getIp());
        }
        
        
        List<String> paragrafos = new ArrayList();
        String w = "";
        
        int i=0, j=0;
        for(String e : palavBlocos){
            int port = new Integer (servidor.get(j).getIp().split(":")[1]);
            System.out.println(servidor.get(j).getIp().split(":")[0] + " port: " + servidor.get(j).getIp().split(":")[1]);
            clientSocket = new Socket(servidor.get(j).getIp().split(":")[0], port);
            
            String q = nomeB + ";" + blocoA.get(i).getNum() + ";" + blocoA.get(i).getQtdPalav();
            //System.out.println("hjhj= " + blocoA.get(i).getNum());
            DataOutputStream dataOutputStream1 = new DataOutputStream(clientSocket.getOutputStream());
            
            dataOutputStream1.writeBytes(q);
            
            //talvez remover dps
            clientSocket.close();
            try { Thread.sleep (3000); } catch (InterruptedException ex) {}
            clientSocket = new Socket(servidor.get(j).getIp().split(":")[0], port);
            //
            
            DataInputStream c = new DataInputStream(clientSocket.getInputStream());
            w = pr.readData() + "\n";
            
            System.out.println(w);
                       
            //paragrafos.add(w);
            
            Resultado resul = new Resultado(blocoA.get(i).getNum(), w);
            blocos.add(resul);
            resul.gerenciaList(blocos);
            
            dataOutputStream1.close();
            clientSocket.close();
            i++;
            j++;
            if(j==servidor.size()) {
            	j=0;
            }
        }
        
        
        String result = "";
        
        for(Resultado k : blocos) {
        	paragrafos.add(k.getBloco());
        	result += k.getBloco();
        	//System.out.println(k.getBloco());
        }
        pr.geraPDF(paragrafos);
        
        String g = lerPDF(nomeB);
        
        String r = getHash(g);
        System.out.print("Hash Original = "  + hash + "\nHash Criado = "+ r);
        if( r.equals(hash)) {
        	System.out.println("\nDownload Concluído!");
                return 0;
        }else {
        	System.out.println("\nDownload Conclúido com Erro!");
                return 1;
        }
        
    }
    
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
            e.printStackTrace();
        }
        return textFromPage;

    }
    
    private void geraPDF(List pdf) throws Exception{
        Document document = new Document();
		try {
                // step 2:
                // we create a writer that listens to the document
                // and directs a PDF-stream to a file
                PdfWriter.getInstance(document,
                                new FileOutputStream(nomeB));
                // step 3: we open the document
                document.open();
                
                for(Object a : pdf){
                    document.add(new Paragraph((String) a));
                }
                
                
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		// step 5: we close the document
		document.close();
		
		
		
    }
    
    private static String getIpTracker() throws FileNotFoundException, IOException{
        File f = new File("ipTracker.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String st, info=""; 

        while ((st = br.readLine()) != null && st.contains(";")) {
            info = st;
        }
        return info;
    }
    
    private static void getPing(String host){
        try{
            InetAddress address = InetAddress.getByName(host);
            boolean reachable = address.isReachable(5000);

            System.out.println("Is host reachable? " + reachable);
        } catch (Exception e){
            e.printStackTrace();
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
    
    private String readData() {
	    try {
                InputStream is = clientSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }
	}
    
    public static String getHash(String texto) throws Exception {

       MessageDigest m = MessageDigest.getInstance("MD5");
       m.update(texto.getBytes(),0,texto.length());
       return new BigInteger(1,m.digest()).toString(16);
    }
}