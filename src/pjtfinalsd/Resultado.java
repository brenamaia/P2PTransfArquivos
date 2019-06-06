package pjtfinalsd;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author brena
 */
public class Resultado implements Comparable<Resultado> {
    
    private int num;
    private String bloco;
    
    public Resultado(int num, String bloco){
        this.num = num;
        this.bloco = bloco;
    }
    
    
    public List gerenciaList (List s){
        Collections.sort(s);
        return s;
    }
    
     @Override
    public int compareTo(Resultado s) {
         if (this.num > s.getNum()) {
              return 1;
         }
         if (this.num < s.getNum()) {
              return -1;
         }
         return 0;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }
    
}
