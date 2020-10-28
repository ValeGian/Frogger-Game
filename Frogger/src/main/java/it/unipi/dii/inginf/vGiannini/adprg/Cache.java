package it.unipi.dii.inginf.vGiannini.adprg;

import java.io.*;
import java.util.*;
import javafx.util.*;

public class Cache implements Serializable{
    public String username;
    public int punteggio;
    public int raneRimanenti;
    public int secondiRimanenti;
    public double posizioneRanaX;
    public double posizioneRanaY;
    public ArrayList<Pair<String, Pair<Pair<Double, Double>, Double>>> oggettiDinamiciDiGioco; // (00)
    
    public Cache(String username, int punteggio, int raneRimanenti, int secondiRimanenti, double posizioneRanaX, double posizioneRanaY, ArrayList<Pair<String, Pair<Pair<Double, Double>, Double>>> oggettiDinamiciDiGioco){
        this.username = username;
        this.punteggio = punteggio;
        this.raneRimanenti = raneRimanenti;
        this.secondiRimanenti = secondiRimanenti;
        this.posizioneRanaX = posizioneRanaX;
        this.posizioneRanaY = posizioneRanaY;
        this.oggettiDinamiciDiGioco = oggettiDinamiciDiGioco;
    }
}

/*
(00)
    Lista contenente le informazioni relative agli oggetti dinamici di gioco
    necessarie per poter ripristinare una eventuale partita interrotta; nello
    specifico, ogni elemento della lista contiene 
    < nome dell'oggetto dinamico, < < coord X dell'oggetto, coord Y >, velocità dell'oggetto >
*/