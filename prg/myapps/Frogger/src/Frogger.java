
import com.thoughtworks.xstream.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class Frogger extends Application {
    
    private static final int APP_WIDTH = 1275;
    private static final int APP_HEIGHT = 900;
    
    private InterfacciaFrogger interfaccia;
    private ParametriDiConfigurazione parametri;
    private GestoreEventiDiLog gestoreEventiLog;
    private DatabasePunteggi database;
    private GestoreCache gestoreCache;
    
    private Scene scena;
    
    public void start(Stage stage) {
        leggiParametriDiConfigurazione();
        
        gestoreEventiLog = new GestoreEventiDiLog(parametri); 
        gestoreEventiLog.creaLog("APPLICATION START"); // (00)
        
        database = new DatabasePunteggi(parametri);
        
        interfaccia = new InterfacciaFrogger(this);
        
        impostaParametriDiConfigurazione();
        
        gestoreCache = new GestoreCache(parametri, interfaccia, GestoreFiles.ottieniFile("./myFiles/cache.bin"));
        gestoreCache.carica();
        
        stage.setOnCloseRequest( (WindowEvent evento) -> { 
            interfaccia.getTimer().stop();
            gestoreEventiLog.creaLog("APPLICATION CLOSURE"); 
            gestoreCache.salva();
        }); // (01)
        
        aggiornaMigliorPunteggioPersonale();
        aggiornaTabellaPunteggiMigliori();
        
        Group root = new Group(getGraficaApplicazione());
        scena = new Scene(root, APP_WIDTH, APP_HEIGHT);
        
        impostaGestioneEventi();
        
        stage.setTitle("Frogger Game");
        stage.setScene(scena);
        stage.show();
    }
    
    private Pane getGraficaApplicazione(){
        Label titoloApplicazione = new Label("FROGGER");
        titoloApplicazione.setStyle("-fx-font-weight: bold; -fx-font-size: 36px");
        
        HBox contenitorePulsanti = new HBox(10, interfaccia.getNewGame(), interfaccia.getResume(), interfaccia.getStop(), interfaccia.getStatoDelGioco());
        
        Label tempoLabel = new Label("TIME: ");
        tempoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
        HBox contenitoreTempo = new HBox(0, tempoLabel, interfaccia.getTempoRimanente());
        
        Label punteggioLabel = new Label("SCORE: ");
        punteggioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
        HBox contenitorePunteggio = new HBox(0, punteggioLabel, interfaccia.getPunteggio());
        
        Label raneRimanentiLabel = new Label("REMAINING FROGS: ");
        raneRimanentiLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
        HBox contenitoreRaneRimanenti = new HBox(0, raneRimanentiLabel, interfaccia.getRaneRimanenti());
        
        VBox colonnaPannelloDiGioco = new VBox(10, contenitorePulsanti, interfaccia.getPannelloDiGioco(), contenitoreTempo, contenitorePunteggio, contenitoreRaneRimanenti);
        
        Label usernameLabel = new Label("USERNAME: ");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        HBox usernameBox = new HBox(usernameLabel, interfaccia.getUsername());
        
        Separator separatore1 = new Separator(Orientation.HORIZONTAL);
        
        Label instructionsLabel = new Label("INSTRUCTIONS:");
        instructionsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        Label instructions = new Label("- Use WASD keys to move the frog (W = UP, A = LEFT, S = DOWN, D = RIGHT)\n- The goal is to safely guide the frogs to the top of the board in the time limit");
        
        Separator separatore2 = new Separator(Orientation.HORIZONTAL);
        
        Label migliorPunteggioPersonaleLabel = new Label("PERSONAL BEST SCORE: ");
        migliorPunteggioPersonaleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px");
        HBox migliorPunteggioPersonaleLabelBox = new HBox(migliorPunteggioPersonaleLabel, interfaccia.getMigliorPunteggioPersonale());
        
        Separator separatore3 = new Separator(Orientation.HORIZONTAL);
        
        VBox colonnaInfo = new VBox(20, usernameBox, separatore1, instructionsLabel, instructions, separatore2, migliorPunteggioPersonaleLabelBox,
                                    separatore3, interfaccia.getTabellaPunteggiMigliori());
        
        HBox contenitoreColonne = new HBox(30, colonnaPannelloDiGioco, colonnaInfo);
        VBox contenitoreApplicazione = new VBox(20, titoloApplicazione, contenitoreColonne);
        contenitoreApplicazione.setMinWidth(APP_WIDTH); contenitoreApplicazione.setMinHeight(APP_HEIGHT);
        contenitoreApplicazione.setStyle("-fx-background-color: " + parametri.coloreBackgroundApplicazione);
        
        return contenitoreApplicazione;
    }
    
    private void leggiParametriDiConfigurazione(){
        if(validaParametriDiConfigurazione()){
            String testoXML = null;
            
            try{
                testoXML = new String(Files.readAllBytes(GestoreFiles.ottieniFile("./myFiles/config.xml").toPath()));
            } catch(Exception e) { 
                System.err.println(e.getMessage()); 
                System.exit(1);
            }
            
            parametri = (ParametriDiConfigurazione)(new XStream()).fromXML(testoXML);
        }
        else{
            System.err.println("Errore: è stato impossibile leggere il file di configurazione");
            System.exit(1);
        }
    }
    
    private boolean validaParametriDiConfigurazione(){
        try{
            DocumentBuilder builderDocumento = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document documento = builderDocumento.parse(GestoreFiles.ottieniFile("./myFiles/config.xml"));
            Schema schema = schemaFactory.newSchema(GestoreFiles.ottieniFile("./myFiles/config.xsd"));
            schema.newValidator().validate(new DOMSource(documento));
        } catch(Exception e){
            if(e instanceof SAXException)
                System.err.println("Errore di validazione: " + e.getMessage());
            else
                System.out.println(e.getMessage());
            
            return false;
        }
        
        return true;
    }
    
    private void impostaParametriDiConfigurazione(){
        interfaccia.impostaParametri(parametri);
    }
    
    private void impostaGestioneEventi(){
        interfaccia.getNewGame().setOnAction( (ActionEvent evento) -> { gestisciPulsanteNewGame(); });
        interfaccia.getStop().setOnAction( (ActionEvent evento) -> { gestisciPulsanteStop(); });
        interfaccia.getResume().setOnAction( (ActionEvent evento) -> { gestisciPulsanteResume(); });
        
        //(02)
        scena.setOnKeyPressed( (KeyEvent evento) -> { interfaccia.getInterfacciaPannelloDiGioco().gestisciKeyPressed(evento); });
        scena.setOnKeyReleased( (KeyEvent evento) -> { interfaccia.getInterfacciaPannelloDiGioco().gestisciKeyReleased(evento); } );
    }
                
    private void gestisciPulsanteNewGame(){
        gestoreEventiLog.creaLog("NEW GAME");
        
        interfaccia.getRaneRimanenti().setText(String.valueOf(parametri.numeroRaneIniziale));
        interfaccia.getTempoRimanente().setText(String.valueOf(parametri.secondiPerRana));
        interfaccia.getPunteggio().setText("0");
        interfaccia.getStatoDelGioco().setText("INIZIATA NUOVA PARTITA");
        
        interfaccia.getTimer().setCycleCount(Timeline.INDEFINITE);
        interfaccia.getTimer().play(); // (03)
        
        interfaccia.getInterfacciaPannelloDiGioco().iniziaNuovaPartita();
    }
                
    private void gestisciPulsanteStop(){
        gestoreEventiLog.creaLog("STOP");
        
        if(Integer.valueOf(interfaccia.getRaneRimanenti().getText()) >= 0){
            interfaccia.getTimer().pause(); // (04)
            interfaccia.getInterfacciaPannelloDiGioco().interrompiPartita();
            interfaccia.getStatoDelGioco().setText("PARTITA INTERROTTA");
        }
    }
                
    private void gestisciPulsanteResume(){
        gestoreEventiLog.creaLog("RESUME");
        
        if(Integer.valueOf(interfaccia.getRaneRimanenti().getText()) >= 0){
            interfaccia.getTimer().play();
            interfaccia.getInterfacciaPannelloDiGioco().riprendiPartita();
            interfaccia.getStatoDelGioco().setText("PARTITA RIPRESA");
        }
    }
    
    private void aggiornaMigliorPunteggioPersonale(){
        int migliorPunteggioPersonale = database.ottieniMigliorPunteggioPersonalePosterioriAData(interfaccia.getUsername().getText(), LocalDate.parse(parametri.dataIniziale));
        
        interfaccia.getMigliorPunteggioPersonale().setText(String.valueOf(migliorPunteggioPersonale));
    }
    
    private void aggiornaTabellaPunteggiMigliori(){
        List<Punteggio> listaPunteggi = database.ottieniPunteggiOrdinatiPosterioriAData(LocalDate.parse(parametri.dataIniziale)); // (05)
        
        interfaccia.getTabellaPunteggiMigliori().aggiornaListaPunteggi(listaPunteggi);
    }
    
    public void inserisciNuovoPunteggio(){
        String username = interfaccia.getUsername().getText();
        
        if(!username.equals("-")){ // (06)
            database.inserisciPunteggio(username, Integer.valueOf(interfaccia.getPunteggio().getText()));
            aggiornaMigliorPunteggioPersonale();
            aggiornaTabellaPunteggiMigliori();
        }
    }
}

/*
(00)
    Invio al Server dei log un log che segnala l'avvio dell'applicazione
(01)
    Imposto che alla chiusura dell'applicazione venga interrotto il timer relativo al 
    tempo di gioco, che quest'ultima venga segnalata con l'invio di un log specifico
    al server dei log e che vengano salvati su cache i dati utili al ripristino 
    dello stato dell'applicativo
(02)
    Imposto che alla pressione/rilascio di un qualsiasi tasto della tastiera, tale 
    evento venga inviato all'interfaccia che si occupa di gestire il pannello di gioco
(03)
    Avvio il timer di gioco
(04)
    Metto in pausa il timer di gioco
(05)
    Ottengo la lista dei punteggi successivi alla data iniziale specificata e in ordine 
    di punteggio decrescente
(06)
    Se lo username inserito dall'utente nell'interfaccia applicativa risulta diverso
    da quello di default, allora inserisco il punteggio conseguito e aggiorno miglior 
    punteggio personale e tabella dei punteggi migliori
*/
