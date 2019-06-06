package pjtfinalsd;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author brena
 */
public class Bloco implements Comparable<Bloco> {
    
    private int num;
    private int qtdPalav;
    
    public Bloco(int num, int qtdPalav){
        this.num = num;
        this.qtdPalav = qtdPalav;
    }
    
    
    public List gerenciaList (List s){
        Collections.sort(s);
        return s;
    }
    
     @Override
    public int compareTo(Bloco s) {
         if (this.qtdPalav > s.getQtdPalav()) {
              return 1;
         }
         if (this.qtdPalav < s.getQtdPalav()) {
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

    public int getQtdPalav() {
        return qtdPalav;
    }

    public void setQtdPalav(int qtdPalav) {
        this.qtdPalav = qtdPalav;
    }
    
}
