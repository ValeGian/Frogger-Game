package it.unipi.dii.inginf.vGiannini.adprg;

import java.io.*;

public class GestoreFiles {
    
    public static File ottieniFile(String nomeFile){
        File file = new File(nomeFile); // (00)
        
        if(file.exists() && !file.isDirectory()) { // (01)
            return file;
        }
        
        String nomeFileCommandLine = "../" + nomeFile; // (02)
        file = new File(nomeFileCommandLine);
        return file;
    }
}

/*
(00)
    Leggo il file senza cambiarne il nome
(01)
    Se tale file esiste (programma eseguito da NetBeans), allora lo ritorno
(02)
    Leggo il file aggiungendo il prefisso "../" per saltare una directory
    in dietro (programma eseguito da command line) e lo ritorno
*/
