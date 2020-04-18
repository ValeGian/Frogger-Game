
import java.util.*;
import javafx.animation.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;


public class InterfacciaPannelloDiGioco {
    public static final int GRANDEZZA_CASELLA = 50;
    public static final int GAME_WIDTH = GRANDEZZA_CASELLA * 11;
    public static final int GAME_HEIGHT = GRANDEZZA_CASELLA * 13;
    
    private final InterfacciaFrogger interfacciaFrogger;
    
    private AnchorPane pannelloDiGioco;
    
    private String urlPannelloDiGioco;
    private String urlParete;
    private int larghezzaPareteInPixels;
    private String urlRana;
    
    private Map<String, OggettoDinamicoDiGioco> mappaOggettiGenerabili; // (00)
    
    private ImageView rana;
    private int larghezzaRanaInPixels;
    private boolean isTroncoAttaccato = false;
    private double velocitaTroncoAttaccato = 0.0;
    
    private ArrayList<Pair<String, Pair<ImageView, Double>>> oggettiDinamiciDiGioco; // (01)
    
    private AnimationTimer gameTimer = null;
    
    private boolean isUpKeyPressed = false;
    private boolean isRightKeyPressed = false;
    private boolean isDownKeyPressed = false;
    private boolean isLeftKeyPressed = false;
    
    public InterfacciaPannelloDiGioco(ParametriDiConfigurazione parametri, InterfacciaFrogger interfaccia){
        interfacciaFrogger = interfaccia;
        
        pannelloDiGioco = new AnchorPane();
        
        oggettiDinamiciDiGioco = new ArrayList<>();
        
        impostaParametri(parametri);
        caricaNuovoLivello();
    }
    
    private void impostaParametri(ParametriDiConfigurazione parametri){
        urlPannelloDiGioco = parametri.urlPannelloDiGioco;
        urlParete = parametri.urlParete;
        larghezzaPareteInPixels = parametri.larghezzaPareteInPixels;
        urlRana = parametri.urlRana;
        larghezzaRanaInPixels = parametri.larghezzaRanaInPixels;
        
        mappaOggettiGenerabili = new HashMap<String, OggettoDinamicoDiGioco>(); 
        for(OggettoDinamicoDiGioco oggetto: parametri.listaOggettiDiGiocoGenerabili){ // (02)
            mappaOggettiGenerabili.put(oggetto.nome, oggetto);
        }
    }
    
    public void posizionaNuovaRana(){
        settaPosizione(rana, GAME_WIDTH/2 - GRANDEZZA_CASELLA/2, GAME_HEIGHT - GRANDEZZA_CASELLA); // (03)
    }
    
    public void riposizioneRana(double nuovaX, double nuovaY){
        if(nuovaX <= GAME_WIDTH - GRANDEZZA_CASELLA && nuovaX >= 0 && nuovaY <= GAME_HEIGHT - GRANDEZZA_CASELLA && nuovaY >= 0)
            settaPosizione(rana, nuovaX, nuovaY);
    }
    
    public double getRanaX() { return rana.getLayoutX(); }
    
    public double getRanaY() { return rana.getLayoutY(); }
    
    private void caricaOggettiDinamiciDiGioco(){ // (04)
        aggiungiOggettoDinamico("troncoX3", 10, 1, 1.3);
        aggiungiOggettoDinamico("troncoX3", 5, 1, 1.3);
        aggiungiOggettoDinamico("troncoX3", 0, 1, 1.3);
        
        aggiungiOggettoDinamico("troncoX2", 10, 2, -2);
        aggiungiOggettoDinamico("troncoX2", 7, 2, -2);
        aggiungiOggettoDinamico("troncoX3", 1, 2, -2);
        
        aggiungiOggettoDinamico("troncoX4", 7, 3, 1.8);
        aggiungiOggettoDinamico("troncoX4", 0, 3, 1.8);
        
        aggiungiOggettoDinamico("troncoX2", 11, 4, 1.2);
        aggiungiOggettoDinamico("troncoX3", 7, 4, 1.2);
        aggiungiOggettoDinamico("troncoX2", 5, 4, 1.2);
        aggiungiOggettoDinamico("troncoX2", 1, 4, 1.2);
        
        aggiungiOggettoDinamico("troncoX2", 11, 5, -1.5);
        aggiungiOggettoDinamico("troncoX2", 8, 5, -1.5);
        aggiungiOggettoDinamico("troncoX3", 2, 5, -1.5);
        
        aggiungiOggettoDinamico("camion", 3, 7, -1.75);
        
        aggiungiOggettoDinamico("macchina verde", 6, 8, 2.5);
        
        aggiungiOggettoDinamico("macchina rossa", 1, 9, -1.3);
        aggiungiOggettoDinamico("macchina rossa", 7, 9, -1.3);
        aggiungiOggettoDinamico("macchina rossa", 11, 9, -1.3);
        
        
        aggiungiOggettoDinamico("macchina blu", 3, 10, 1.5);
        aggiungiOggettoDinamico("macchina blu", 6, 10, 1.5);
        
        
        aggiungiOggettoDinamico("macchina gialla", 2, 11, -1.15);
        aggiungiOggettoDinamico("macchina gialla", 6, 11, -1.15);
    }
    
    private void aggiungiOggettoDinamico(String nomeOggetto, int caselleOffsetX, int caselleOffsetY, double velocita){ // (05)
        
        ImageView oggettoDinamico = new ImageView("file:" + GestoreFiles.ottieniFile(mappaOggettiGenerabili.get(nomeOggetto).urlImmagine).toPath());
        if(velocita < 0)
            oggettoDinamico.setRotate(180);
        
        settaPosizione(oggettoDinamico, caselleOffsetX * GRANDEZZA_CASELLA, caselleOffsetY * GRANDEZZA_CASELLA + 5);
        oggettiDinamiciDiGioco.add(new Pair(nomeOggetto, new Pair(oggettoDinamico, velocita)));
    }
    
    private void caricaNuovoLivello(){
        pannelloDiGioco.getChildren().clear();
        oggettiDinamiciDiGioco.clear(); 
        
        ImageView immaginePannelloDiGioco = new ImageView("file:" + GestoreFiles.ottieniFile(urlPannelloDiGioco).toPath());
        pannelloDiGioco.getChildren().add(immaginePannelloDiGioco);
        
        // (06)
        caricaOggettiDinamiciDiGioco();
        for(Pair<String, Pair<ImageView, Double>> oggetto: oggettiDinamiciDiGioco){
            ImageView oggettoDinamico = oggetto.getValue().getKey();
            pannelloDiGioco.getChildren().add(oggettoDinamico);
        }
        
        // (07)
        rana = new ImageView("file:" + GestoreFiles.ottieniFile(urlRana).toPath()); 
        posizionaNuovaRana();
        pannelloDiGioco.getChildren().add(rana);
        
        // (08)
        ImageView parete = new ImageView("file:" + GestoreFiles.ottieniFile(urlParete).toPath()); 
        parete.setLayoutX(GAME_WIDTH - 5);
        pannelloDiGioco.getChildren().add(parete);
    }
    
    public AnchorPane getPannelloDiGioco(){ return pannelloDiGioco; }
    
    public void gestisciKeyPressed(KeyEvent evento){ // (09)
        if(evento.getCode().toString().equals("W")) 
            isUpKeyPressed = true;
        else if(evento.getCode().toString().equals("A"))
            isLeftKeyPressed = true;
        else if(evento.getCode().toString().equals("S"))
            isDownKeyPressed = true;
        else if(evento.getCode().toString().equals("D"))
            isRightKeyPressed = true;
    }
    
    public void gestisciKeyReleased(KeyEvent evento){ // (10)
        if(evento.getCode().toString().equals("W")) 
            isUpKeyPressed = false;
        else if(evento.getCode().toString().equals("A"))
            isLeftKeyPressed = false;
        else if(evento.getCode().toString().equals("S"))
            isDownKeyPressed = false;
        else if(evento.getCode().toString().equals("D"))
            isRightKeyPressed = false;
    }
    
    public void iniziaNuovaPartita(){
        caricaNuovoLivello();
        
        if(gameTimer != null) // (11)
            gameTimer.stop();
        
        gameTimer = new AnimationTimer() { // (12)

            public void handle(long now) {
                muoviOggettiDinamiciDiGioco();
                muoviRana();
                gestisciCollisioni();
                
                if(rana.getLayoutY() == 0){ // (13)
                    interfacciaFrogger.getStatoDelGioco().setText("RAGGIUNTA L'OBBIETTIVO");
                    isTroncoAttaccato = false;
                    velocitaTroncoAttaccato = 0.0;
                    interfacciaFrogger.incrementaPunteggio(100);
                    interfacciaFrogger.topLaneRaggiunta();
                }
                else if(rana.getLayoutY() < 6 * GRANDEZZA_CASELLA && !isTroncoAttaccato){ // (14)
                    interfacciaFrogger.getStatoDelGioco().setText("RANA CADUTA NEL FIUME");
                    isTroncoAttaccato = false;
                    velocitaTroncoAttaccato = 0.0;
                    interfacciaFrogger.ranaMorta();
                }
            }
        };
        
        gameTimer.start();
    }
    
    public void interrompiPartita(){
        gameTimer.stop();
    }
    
    public void riprendiPartita(){
        gameTimer.start();
    }
    
    private void muoviRana(){
        if(isTroncoAttaccato){ // (15)
            settaPosizione(rana, rana.getLayoutX() + velocitaTroncoAttaccato, rana.getLayoutY());
            
            if(rana.getLayoutX() < 0 || rana.getLayoutX() > GAME_WIDTH - GRANDEZZA_CASELLA) // (16)
                settaPosizione(rana, rana.getLayoutX() - velocitaTroncoAttaccato, rana.getLayoutY());
        }
        
        // (17)
        if(isUpKeyPressed && !(isRightKeyPressed || isDownKeyPressed || isLeftKeyPressed)){ 
            if(rana.getLayoutY() > 0){
                settaPosizione(rana, rana.getLayoutX(), rana.getLayoutY() - GRANDEZZA_CASELLA);
                rana.setRotate(0);
                isUpKeyPressed = false;
                interfacciaFrogger.incrementaPunteggio(2);
            }
        }
        else if(isDownKeyPressed && !(isRightKeyPressed || isUpKeyPressed || isLeftKeyPressed)){
            if(rana.getLayoutY() < GAME_HEIGHT - GRANDEZZA_CASELLA){
                settaPosizione(rana, rana.getLayoutX(), rana.getLayoutY() + GRANDEZZA_CASELLA);
                rana.setRotate(180);
                isDownKeyPressed = false;
                interfacciaFrogger.incrementaPunteggio(2);
            }
        }
        else if(isLeftKeyPressed && !(isRightKeyPressed || isDownKeyPressed || isUpKeyPressed)){
            if(rana.getLayoutX() > 0){
                settaPosizione(rana, rana.getLayoutX() - GRANDEZZA_CASELLA, rana.getLayoutY());
                rana.setRotate(270);
                isLeftKeyPressed = false;
                interfacciaFrogger.incrementaPunteggio(2);
            }
        }
        else if(isRightKeyPressed && !(isUpKeyPressed || isDownKeyPressed || isLeftKeyPressed)){
            if(rana.getLayoutX() < GAME_WIDTH - GRANDEZZA_CASELLA){
                settaPosizione(rana, rana.getLayoutX() + GRANDEZZA_CASELLA, rana.getLayoutY());
                rana.setRotate(90);
                isRightKeyPressed = false;
                interfacciaFrogger.incrementaPunteggio(2);
            }
        }
        
        isTroncoAttaccato = false; // (18)
    }
    
    private void muoviOggettiDinamiciDiGioco(){ // (19)
        for(Pair<String, Pair<ImageView, Double>> oggettoDinamico: oggettiDinamiciDiGioco){ 
            ImageView immagineOggettoDinamico = oggettoDinamico.getValue().getKey();
            double velocitaOggettoDinamico = oggettoDinamico.getValue().getValue().doubleValue();
            String nomeOggettoDinamico = oggettoDinamico.getKey();
            
            settaPosizione(immagineOggettoDinamico, immagineOggettoDinamico.getLayoutX() + velocitaOggettoDinamico, immagineOggettoDinamico.getLayoutY());
            
            if(velocitaOggettoDinamico > 0 
                && immagineOggettoDinamico.getLayoutX() > GAME_WIDTH - 5)
            {
                settaPosizione(immagineOggettoDinamico, -3 * GRANDEZZA_CASELLA, immagineOggettoDinamico.getLayoutY());
            }
            else if(velocitaOggettoDinamico < 0
                     && immagineOggettoDinamico.getLayoutX() < -1 * mappaOggettiGenerabili.get(nomeOggettoDinamico).larghezzaInPixels)
            {
                settaPosizione(immagineOggettoDinamico, GAME_WIDTH + larghezzaPareteInPixels - mappaOggettiGenerabili.get(nomeOggettoDinamico).larghezzaInPixels - 6, immagineOggettoDinamico.getLayoutY());
            }
        }
    }
    
    private void settaPosizione(ImageView oggetto, double nuovaX, double nuovaY){
        oggetto.setLayoutX(nuovaX);
        oggetto.setLayoutY(nuovaY);
    }
    
    private void gestisciCollisioni(){
        for(Pair<String, Pair<ImageView, Double>> oggettoDinamico: oggettiDinamiciDiGioco){
            if(ranaCollide(oggettoDinamico)){
                if(oggettoDinamico.getKey().startsWith("tronco")){ // (20)
                    isTroncoAttaccato = true;
                    velocitaTroncoAttaccato = oggettoDinamico.getValue().getValue().doubleValue();
                }
                else{ // (21)
                    interfacciaFrogger.getStatoDelGioco().setText("AVVENUTA UNA COLLISIONE");
                    interfacciaFrogger.ranaMorta();
                }
            }
        }
    }
    
    private boolean ranaCollide(Pair<String, Pair<ImageView, Double>> oggettoDinamico){
        ImageView immagineOggettoDinamico = oggettoDinamico.getValue().getKey();
        String nomeOggettoDinamico = oggettoDinamico.getKey();
            
        int corsiaRana = (int)rana.getLayoutY()/GRANDEZZA_CASELLA;
        int corsiaOggettoDinamico = (int)immagineOggettoDinamico.getLayoutY()/GRANDEZZA_CASELLA;
        
        double coordBordoSinistroRana = rana.getLayoutX();
        double coordBordoDestroRana = rana.getLayoutX() + larghezzaRanaInPixels;
        
        double coordBordoSinistroOggettoDinamico = immagineOggettoDinamico.getLayoutX();
        double coordBordoDestroOggettoDinamico = immagineOggettoDinamico.getLayoutX() + mappaOggettiGenerabili.get(nomeOggettoDinamico).larghezzaInPixels;
        
        // (22)
        return !(corsiaRana != corsiaOggettoDinamico ||
                coordBordoDestroRana <= coordBordoSinistroOggettoDinamico ||
                coordBordoSinistroRana >= coordBordoDestroOggettoDinamico);
    }
    
    public ArrayList<Pair<String, Pair<Pair<Double, Double>, Double>>> getOggettiDinamiciDiGiocoPerCache(){ // (23)
        ArrayList<Pair<String, Pair<Pair<Double, Double>, Double>>> oggettiDinamiciFormatoCache = new ArrayList<>();
        
        for(Pair<String, Pair<ImageView, Double>> oggettoDinamico: oggettiDinamiciDiGioco){
            String nomeOggetto = oggettoDinamico.getKey();
            
            ImageView immagineOggetto = oggettoDinamico.getValue().getKey();
            double xOggetto = immagineOggetto.getLayoutX();
            double yOggetto = immagineOggetto.getLayoutY();
            
            double velocitaOggetto = oggettoDinamico.getValue().getValue();
            
            oggettiDinamiciFormatoCache.add(new Pair(nomeOggetto, new Pair(new Pair(xOggetto, yOggetto), velocitaOggetto)));
        }
        
        return oggettiDinamiciFormatoCache; 
    }
    // (24)
    public void caricaOggettiDinamiciDiGiocoDaCache(ArrayList<Pair<String, Pair<Pair<Double, Double>, Double>>> oggettiDinamiciDiGiocoDaCache){
        pannelloDiGioco.getChildren().clear();
        oggettiDinamiciDiGioco.clear(); 
        
        ImageView immaginePannelloDiGioco = new ImageView("file:" + GestoreFiles.ottieniFile(urlPannelloDiGioco).toPath());
        immaginePannelloDiGioco.setStyle("-fx-bacground-color: black");
        pannelloDiGioco.getChildren().add(immaginePannelloDiGioco);
        
        // (25)
        for(Pair<String, Pair<Pair<Double, Double>, Double>> oggettoDinamicoDaCache: oggettiDinamiciDiGiocoDaCache){
            String nomeOggetto = oggettoDinamicoDaCache.getKey();
            
            Pair<Double, Double> xyOggetto = oggettoDinamicoDaCache.getValue().getKey();
            double xOggetto = xyOggetto.getKey().doubleValue();
            double yOggetto = xyOggetto.getValue().doubleValue();
            
            String urlImmagineOggetto = mappaOggettiGenerabili.get(nomeOggetto).urlImmagine;
            ImageView immagineOggettoDinamico = new ImageView("file:" + GestoreFiles.ottieniFile(urlImmagineOggetto).toPath());
            immagineOggettoDinamico.setLayoutX(xOggetto);
            immagineOggettoDinamico.setLayoutY(yOggetto);
            
            double velocitaOggetto = oggettoDinamicoDaCache.getValue().getValue();
            
            if(velocitaOggetto < 0)
                immagineOggettoDinamico.setRotate(180);
            
            oggettiDinamiciDiGioco.add(new Pair(nomeOggetto, new Pair(immagineOggettoDinamico, velocitaOggetto)));
            pannelloDiGioco.getChildren().add(immagineOggettoDinamico);
        }
        
        rana = new ImageView("file:" + GestoreFiles.ottieniFile(urlRana).toPath());
        posizionaNuovaRana();
        pannelloDiGioco.getChildren().add(rana);
        
        ImageView parete = new ImageView("file:" + GestoreFiles.ottieniFile(urlParete).toPath());
        parete.setLayoutX(GAME_WIDTH - 5);
        pannelloDiGioco.getChildren().add(parete);
    }
}

/*
(00)
    Mappa che conterrà gli oggetti di gioco generabili nel formato < Nome oggetto, oggetto >
(01)
    Lista che conterrà gli oggetti di gioco caricati nella partita corrente nel 
    formato < Nome oggetto, < Immagine oggetto, Velocità oggetto > >
(02)
    Imposto gli oggetti dinamici generabili sul pannello di gioco
(03)
    Imposto la posizione iniziale della rana, piazzandola a metà della riga più in basso
(04)
    Carico in "oggettiDinamiciDiGioco" gli oggetti dinamici che saranno presenti nella partita
(05)
    Imposto nome, immagine, posizione, direzione e velocità di un oggetto dinamico 
    e lo carico in "oggettiDinamiciDiGioco"
(06)
    Carico in "oggettiDinamiciDiGioco" gli oggetti dinamici che saranno presenti 
    nel nuovo livello e li aggiungo al pannello di gioco
(07)
    Carico l'immagine della rana, ne imposto la posizione di partenza e la 
    aggiungo al pannello di gioco
(08)
    Carico l'immagine della parete, ne imposto la posizione e la aggiungo al 
    pannello di gioco
(09)
    Gestisco la pressione di uno fra i tasti WASD, ponendo "true" la variabile 
    booleana corrispondente
(10)
    Gestisco il rilascio di uno fra i tasti WASD, ponendo "false" la variabile 
    booleana corrispondente
(11)
    Se c'è un timer di gioco attivo, lo disattivo
(12)
    Creo un nuovo timer di gioco
(13)
    Se la rana ha raggiunto la riga più in alto (riga obbiettivo) allora eseguo 
    le operazioni per notificare l'evento alla interfacciaFrogger e per resettare 
    le variabili relative al tronco al quale la rana era attaccata
(14)
    Se la rana cade nel fiume, allora eseguo le operazioni per notificare 
    l'evento alla interfacciaFrogger e per resettare le variabili relative al 
    tronco al quale la rana era attaccata
(15)
    Se la rana risulta attaccata ad un tronco, allora provo a muoverla insieme 
    a tale tronco
(16)
    Se la rana risulta essersi mossa fuori dai bordi del pannello di gioco, 
    allora ripristino la posizione precedente
(17)
    Se dall'ultimo frame è stato premuto uno e uno solo tra i pulsanti WASD, 
    allora muovo la rana nella direzione corrispondente se tale movimento non va a 
    portarla fuori dai bordi del pannello di gioco, ne imposto la direzione e notifico 
    tale spostamento alla interfacciaFrogger con un incremento del punteggio
(18)
    Stacco la rana dall'eventuale tronco alla quale risultava attaccata
(19)
    Scorro tutti gli oggetti dinamici presenti sul pannello di gioco, muovendoli e 
    gestendo "l'effetto pacman" nel caso in cui abbiano superato certe coordinate 
    orizzontali dipendenti dalla loro direzione di movimento
(20)
    Se la rana collide con un tronco, allora la attacco a quest'ultimo
(21)
    Se la rana collide con una macchina, allora segnalo alla interfacciaFrogger tale evento
(22)
    Ritorno true se la rana collide con l'oggetto dinamico, ritornando la negazione 
    della condizione di non collisione fra i due
(23)    
    Ritorna una lista contenente le informazioni utili alla cache per salvare lo 
    stato attuale degli oggetti dinamici sul pannello di gioco al fine di poterli ripristinare
(24)   
    Ripristina sul pannello di gioco gli oggetti dinamici nello stato salvato su 
    cache alla ultima chiusura, ricaricando il pannello di gioco nella sua interezza 
    per mantenere il giusto ordine fra background e foreground
(25)
    Per ogni oggetto dinamico caricato dalla cache, ne ripristino lo stato sul pannello di gioco
*/
