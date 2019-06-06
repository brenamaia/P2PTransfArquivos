package pjtfinalsd;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author brena
 */
public class Servidor implements Comparable<Servidor> {
    
    private String ip;
    private long latencia;
        
    public Servidor() {
    	
    }
    
    public Servidor(String ip, long latencia){
        this.ip = ip;
        this.latencia = latencia;
    }
    
    public List gerenciaList (List s){
        Collections.sort(s);
        
        return s;
        
    }

    @Override
    public int compareTo(Servidor s) {
         if (this.latencia > s.getLatencia()) {
              return -1;
         }
         if (this.latencia < s.getLatencia()) {
              return 1;
         }
         return -1;
    }
    

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLatencia() {
        return latencia;
    }

    public void setLatencia(long latencia) {
        this.latencia = latencia;
    }
    
    
}
