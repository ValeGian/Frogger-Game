
import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;

public class GestoreEventiDiLog {
    private String ipServerLog;
    private int portaServerLog;
    private String ipClient;
    
    public GestoreEventiDiLog(ParametriDiConfigurazione parametri){
        ipServerLog = parametri.ipServerLog;
        portaServerLog = parametri.portaServerLog;
        ipClient = parametri.ipClient;
    }
    
    public void creaLog(String etichetta){
        EventoDiNavigazione log = new EventoDiNavigazione(ipClient, etichetta); // (00)
        
        try (Socket socket = new Socket(ipServerLog, portaServerLog);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            dataOutputStream.writeUTF((new XStream()).toXML(log));  // (01)
        } catch (IOException e) { e.printStackTrace(); }
    }
}

/*
(00)
    Genero l'evento di navigazione da inviare al server dei log
(01)
    Invio l'evento di navigazione al server in formato XML
*/