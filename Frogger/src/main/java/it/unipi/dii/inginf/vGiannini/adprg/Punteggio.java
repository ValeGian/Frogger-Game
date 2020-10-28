package it.unipi.dii.inginf.vGiannini.adprg;

import javafx.beans.property.*;

public class Punteggio {
    private final SimpleIntegerProperty posizioneInClassifica;
    private final SimpleStringProperty username;
    private final SimpleIntegerProperty punteggio;
    
    public Punteggio(int posizioneInClassifica, String username, int punteggio){
        this.posizioneInClassifica = new SimpleIntegerProperty(posizioneInClassifica);
        this.username = new SimpleStringProperty(username);
        this.punteggio = new SimpleIntegerProperty(punteggio);
    }
    
    public int getPosizioneInClassifica() { return posizioneInClassifica.get(); }
    
    public String getUsername() { return username.get(); }
    
    public int getPunteggio() { return punteggio.get(); }
}
