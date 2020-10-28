package it.unipi.dii.inginf.vGiannini.adprg;

import javafx.animation.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;


public class InterfacciaFrogger{
      
    private final Frogger applicazione;
    private final TabellaVisualePunteggiMigliori tabellaPunteggiMigliori;
    private InterfacciaPannelloDiGioco interfacciaPannelloDiGioco;
    private final Button newGame;
    private final Button stop;
    private final Button resume;
    private final TextField statoDelGioco;
    private final TextField username;
    private final TextField migliorPunteggioPersonale;
    private final TextField tempoRimanente;
    private final TextField punteggio;
    private final TextField raneRimanenti;
    
    private int secondiPerRana;
    
    private Timeline timer;
    
    public InterfacciaFrogger(Frogger applicazione){
        this.applicazione = applicazione;
        
        tabellaPunteggiMigliori = new TabellaVisualePunteggiMigliori();
                
        newGame = new Button("NEW GAME");
        stop = new Button("STOP");
        resume = new Button("RESUME");
        
        statoDelGioco = new TextField("NESSUNA PARTITA IN CORSO");
        statoDelGioco.setAlignment(Pos.CENTER);
        statoDelGioco.setDisable(true);
        statoDelGioco.setPrefWidth(250);
        statoDelGioco.setStyle("-fx-font-weight: bold");
        
        username = new TextField("-");
        username.setAlignment(Pos.CENTER);
        
        migliorPunteggioPersonale = new TextField("0");
        migliorPunteggioPersonale.setDisable(true);
        migliorPunteggioPersonale.setAlignment(Pos.CENTER);
        migliorPunteggioPersonale.setPrefWidth(60);
        
        tempoRimanente = new TextField("0");
        tempoRimanente.setDisable(true);
        tempoRimanente.setAlignment(Pos.CENTER);
        tempoRimanente.setPrefWidth(50);
        
        punteggio = new TextField("0");
        punteggio.setDisable(true);
        punteggio.setAlignment(Pos.CENTER);
        punteggio.setPrefWidth(50);
        
        raneRimanenti = new TextField("0");
        raneRimanenti.setDisable(true);
        raneRimanenti.setAlignment(Pos.CENTER);
        raneRimanenti.setPrefWidth(50);
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                decrementaTempoRimanente();
            }
        })); // (00)
    }
    
    public void impostaParametri(ParametriDiConfigurazione parametri){
        interfacciaPannelloDiGioco = new InterfacciaPannelloDiGioco(parametri, this);
        
        tabellaPunteggiMigliori.impostaParametri(parametri);
        newGame.setStyle("-fx-font-size: 16px; -fx-background-color: " + parametri.colorePulsanteNewGame);
        stop.setStyle("-fx-font-size: 16px; -fx-background-color: " + parametri.colorePulsanteStop);
        resume.setStyle("-fx-font-size: 16px; -fx-background-color: " + parametri.colorePulsanteResume);
        
        secondiPerRana = parametri.secondiPerRana;
    }
    
    public void incrementaPunteggio(int incremento){
        int vecchioPunteggio = Integer.valueOf(punteggio.getText());
        punteggio.setText(String.valueOf(vecchioPunteggio + incremento));
    }
    
    public void topLaneRaggiunta(){
        nuovaRana();
    }
    
    public void ranaMorta(){
        tempoRimanente.setText("0"); // (01)
        nuovaRana();
    }
    
    private void nuovaRana(){
        int tRimanente = Integer.valueOf(tempoRimanente.getText());
        incrementaPunteggio(tRimanente);
        
        int numeroRaneRimanenti = Integer.valueOf(raneRimanenti.getText());
        
        if(numeroRaneRimanenti > 0){ // (02)
            raneRimanenti.setText(String.valueOf(numeroRaneRimanenti - 1));
            tempoRimanente.setText(String.valueOf(secondiPerRana));
            
            interfacciaPannelloDiGioco.posizionaNuovaRana();
            return;
        }
        
        raneRimanenti.setText("-1"); // (03)
        statoDelGioco.setText("PARTITA CONCLUSA");
        timer.stop();
        interfacciaPannelloDiGioco.interrompiPartita();
        applicazione.inserisciNuovoPunteggio();
    }
    
    private void decrementaTempoRimanente(){
        int tRimanente = Integer.valueOf(tempoRimanente.getText());
        
        if(tRimanente > 0){
            tempoRimanente.setText(String.valueOf(--tRimanente));
            
            if(tRimanente == 0) // (04)
                nuovaRana();
        }
    }
    
    public TabellaVisualePunteggiMigliori getTabellaPunteggiMigliori() { return tabellaPunteggiMigliori; }
    
    public Button getNewGame() { return newGame; }
    
    public Button getStop() { return stop; }
    
    public Button getResume() { return resume; }
    
    public TextField getStatoDelGioco() { return statoDelGioco; }
    
    public TextField getUsername() { return username; }
    
    public TextField getMigliorPunteggioPersonale() { return migliorPunteggioPersonale; }
    
    public TextField getTempoRimanente() { return tempoRimanente; }
    
    public TextField getPunteggio() { return punteggio; }
    
    public TextField getRaneRimanenti() { return raneRimanenti; }
    
    public InterfacciaPannelloDiGioco getInterfacciaPannelloDiGioco() { return interfacciaPannelloDiGioco; }
    
    public Pane getPannelloDiGioco() { return interfacciaPannelloDiGioco.getPannelloDiGioco(); }
    
    public Timeline getTimer() { return timer; }
}

/*
(00)
    Imposto il timer di gioco in maniera tale che, una volta avviato, decrementi
    il tempo rimasto alla rana corrente ogni secondo fino al raggiungimento dello 0
(01)
    Imposto il tempo rimanente a 0 e provo a far partire una nuova rana
(01)
    Se rimangono rane da far arrivare a destinazione, decremento il numero di rane,
    reimposto il tempo a disposizione e creo la nuova rana
(02)
    Quando le rane a disposizione sono finite e ne viene richiesta una nuova, setto
    il numero di rane rimaste ad un valore negativo per segnalare che la partita
    è terminata
(03)
    Quando il tempo a disposizione del giocatore scende a 0, provo a far partire
    la rana successiva (se ne rimangono)
*/