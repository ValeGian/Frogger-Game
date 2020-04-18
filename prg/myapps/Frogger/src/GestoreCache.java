
import java.io.*;

public class GestoreCache {
    private final InterfacciaFrogger interfaccia ;
    private final File fileCache;
    private final int numRaneDefault;
    private final int secondiPerRanaDefault;
    
    public GestoreCache(ParametriDiConfigurazione parametri, InterfacciaFrogger interfaccia, File fileCache){
        this.interfaccia = interfaccia;
        this.fileCache = fileCache;
        numRaneDefault = parametri.numeroRaneIniziale;
        secondiPerRanaDefault = parametri.secondiPerRana;
    }
    
    public void carica(){
        Cache cache = null;
        
        try ( FileInputStream fileInputStream = new FileInputStream(fileCache);
              ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream); )
        {
            cache = (Cache)objectInputStream.readObject();
            
            // (00)
            interfaccia.getUsername().setText(cache.username);
            interfaccia.getPunteggio().setText(String.valueOf(cache.punteggio));
            interfaccia.getRaneRimanenti().setText(String.valueOf(cache.raneRimanenti));
            interfaccia.getTempoRimanente().setText(String.valueOf(cache.secondiRimanenti));
            
            // (01)
            interfaccia.getInterfacciaPannelloDiGioco().iniziaNuovaPartita();
            interfaccia.getInterfacciaPannelloDiGioco().caricaOggettiDinamiciDiGiocoDaCache(cache.oggettiDinamiciDiGioco);
            interfaccia.getInterfacciaPannelloDiGioco().riposizioneRana(cache.posizioneRanaX, cache.posizioneRanaY);
            interfaccia.getInterfacciaPannelloDiGioco().interrompiPartita();
            interfaccia.getStatoDelGioco().setText("PARTITA INTERROTTA");
            
        } catch (IOException | ClassNotFoundException ex) { // (
            System.out.println("E' stato impossibile caricare la cache; l'applicazione verrà inizializzata con i valori di default");
            interfaccia.getUsername().setText("-");
            interfaccia.getPunteggio().setText("0");
            interfaccia.getRaneRimanenti().setText(String.valueOf(numRaneDefault));
            interfaccia.getTempoRimanente().setText(String.valueOf(secondiPerRanaDefault));
            interfaccia.getStatoDelGioco().setText("NESSUNA PARTITA IN CORSO");
        }
    }
    
    public void salva(){
        String username = interfaccia.getUsername().getText();
        int punteggio = Integer.valueOf(interfaccia.getPunteggio().getText());
        int raneRimanenti = Integer.valueOf(interfaccia.getRaneRimanenti().getText());
        int secondiRimanenti = Integer.valueOf(interfaccia.getTempoRimanente().getText());
        double ranaXCoord = interfaccia.getInterfacciaPannelloDiGioco().getRanaX();
        double ranaYCoord = interfaccia.getInterfacciaPannelloDiGioco().getRanaY();
        
        Cache cache = new Cache(username, punteggio, raneRimanenti, secondiRimanenti, ranaXCoord, ranaYCoord, 
                                interfaccia.getInterfacciaPannelloDiGioco().getOggettiDinamiciDiGiocoPerCache()); // (02)
        
        try ( FileOutputStream fileOutputStream = new FileOutputStream(fileCache);
              ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream); )
        {
            objectOutputStream.writeObject(cache); // (03)
        } catch (IOException ex) {
            System.out.println("Errore: impossibile salvare la cache");
            ex.printStackTrace();
        }
    }
}

/*
(00)
    Imposto username, punteggio, rane rimanenti e secondi rimanenti letti da file
    di cache nell'interfaccia applicativa
(01)
    Eseguo le operazioni necessarie per ristabilire lo stato della partita interrotta
(02)
    Creo l'istanza di Cache usando i valori letti dall'interfaccia applicativa 
(03)
    Scrivo l'istanza di Cache su file binario
*/
