
import java.text.*;
import java.util.*;

public class EventoDiNavigazione {
    public String ipClient;
    public String timestamp;
    public String etichetta;
    public String nomeApplicazione;
    
    public EventoDiNavigazione(String ipClient, String etichetta){
        this.ipClient = ipClient;
        this.etichetta = etichetta;
        nomeApplicazione = "Frogger";
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // (00)
    }
}

/*
(00)
    Ottengo il timestamp corrente nel formato specificato
*/
