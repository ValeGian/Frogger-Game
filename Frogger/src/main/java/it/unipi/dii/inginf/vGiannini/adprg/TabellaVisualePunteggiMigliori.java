package it.unipi.dii.inginf.vGiannini.adprg;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class TabellaVisualePunteggiMigliori extends TableView<Punteggio>{
    private final ObservableList<Punteggio> listaOsservabilePunteggi;
    private int numeroRighe;
    
    public TabellaVisualePunteggiMigliori(){
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY ); // (00)
        setMaxHeight(240);
        
        TableColumn colonnaPosizioneInClassifica = new TableColumn("POSIZIONE"); // (01)
        TableColumn colonnaUsername = new TableColumn("USERNAME");
        TableColumn colonnaPunteggio = new TableColumn("PUNTEGGIO");
        
        colonnaPosizioneInClassifica.setCellValueFactory(new PropertyValueFactory<>("posizioneInClassifica")); // (02)
        colonnaUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colonnaPunteggio.setCellValueFactory(new PropertyValueFactory<>("punteggio"));
        
        listaOsservabilePunteggi = FXCollections.observableArrayList(); // (03)
        setItems(listaOsservabilePunteggi);
        
        getColumns().addAll(colonnaPosizioneInClassifica, colonnaUsername, colonnaPunteggio); // (04)
    }
    
    public void impostaParametri(ParametriDiConfigurazione parametri){
        numeroRighe = parametri.numeroRigheTabella;
    }
    
    public void aggiornaListaPunteggi(List<Punteggio> listaPunteggi){
        listaOsservabilePunteggi.clear();
        for(int i = 0; i < numeroRighe && i < listaPunteggi.size(); i++) // (05)
            listaOsservabilePunteggi.add(listaPunteggi.get(i));
    }
}

/*
(00)
    Imposto l'altezza massima della tabella e che la somma della larghezza delle colonne
    della tabella resti uguale alla larghezza della tabella stessa anche in seguito a
    ridimensionamenti da parte dell'utente
(01)
    Genero le tre colonne che comporranno la tabella
(02)
    Specifico le fabbriche di celle per le colonne
(03)
    inizializzo la lista osservabile e la uso come base per riempire la tabella
(04)
    Aggiungo le colonne alla tabella
(05)
    Inserisco nella tabella al massimo tante righe quante decise da file di configurazione
*/
