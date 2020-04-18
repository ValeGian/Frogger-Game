
public class OggettoDinamicoDiGioco {
    public String nome;
    public String urlImmagine;
    public int larghezzaInPixels;
    
    public OggettoDinamicoDiGioco(OggettoDinamicoDiGioco oggetto){
        urlImmagine = oggetto.urlImmagine;
        nome = oggetto.nome;
        larghezzaInPixels = oggetto.larghezzaInPixels;
    }
}
