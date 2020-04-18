
import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class ServerLogEventi {
    private static int PORTA = 8080;
    
    public static void main(String[] args){
        try( ServerSocket server = new ServerSocket(PORTA) ){
             while(true){
                 try(Socket socket = server.accept();
                     DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                ) { 
                    EventoDiNavigazione evento = (EventoDiNavigazione)(new XStream()).fromXML(inputStream.readUTF()); // (00)
                    System.out.println("- ricevuto\n" + (new XStream()).toXML(evento) + "\n");
                    
                    String eventoTestuale = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (new XStream()).toXML(evento) + "\n\n"; // (01)
                    if(validaEventoDiNavigazione(eventoTestuale)) // (02)
                        scrivi(eventoTestuale);
                }
             }
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    private static void scrivi(String evento){
            try{
                Files.write(ottieniFile("logs.xml").toPath(), evento.getBytes(), StandardOpenOption.APPEND); // (03)
            } catch(Exception e) { 
                System.err.println(e.getMessage());
            }
    }
    
    private static boolean validaEventoDiNavigazione(String evento){
        try{
            DocumentBuilder builderDocumento = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document documento = builderDocumento.parse(new InputSource(new StringReader(evento)));
            Schema schema = schemaFactory.newSchema(ottieniFile("log.xsd"));
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
    
    private static File ottieniFile(String nomeFile){
        File file = new File(nomeFile); // (04)
        
        if(file.exists() && !file.isDirectory()) { // (05)
            return file;
        }
        
        String nomeFileCommandLine = "../" + nomeFile; // (06)
        file = new File(nomeFileCommandLine);
        return file;
    }
}

/*
(00)
    Ricevo un log in formato XML dal socket e lo converto in un oggetto
    di tipo EventoDiNavigazione
(01)
    Compongo la stringa da validare
(02)
    Se l'evento viene validato allora lo scrivo sul file dei log
(03)
    Appendo l'evento nel file testuale dei log
(04)
    Leggo il file relativo al percorso indicato in "nomeFile"
(05)
    Se tale file esiste (programma eseguito da NetBeans), allora lo ritorno
(06)
    Leggo il file aggiungendo il prefisso "../" per saltare una directory
    indietro (programma eseguito da command line) e lo ritorno
*/
