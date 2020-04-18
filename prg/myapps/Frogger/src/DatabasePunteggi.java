
import java.sql.*;
import java.time.*;
import java.util.*;

public class DatabasePunteggi {
    private String ipDatabase;
    private int portaDatabase;
    private String usernameDiAccesso;
    private String passwordDiAccesso;
    
    public DatabasePunteggi(ParametriDiConfigurazione parametri){
        ipDatabase = parametri.ipDatabase;
        portaDatabase = parametri.portaDatabase;
        usernameDiAccesso = parametri.usernameDatabase;
        passwordDiAccesso = parametri.passwordDatabase;
    }
    
    private Connection ottieniConnessione() throws SQLException{ // (00)
        return DriverManager.getConnection("jdbc:mysql://" + ipDatabase + ":" + portaDatabase + "/databasepunteggi", 
                usernameDiAccesso, passwordDiAccesso);
    }
    
    public List<Punteggio> ottieniPunteggiOrdinatiPosterioriAData(LocalDate dataIniziale){
        List<Punteggio> listaPunteggi = new ArrayList<>();
        Timestamp timestampIniziale = Timestamp.valueOf(dataIniziale.atStartOfDay()); // (01)
        int count = 0;
        int punteggioPrecedente = -1;
        
        try ( Connection connessione = ottieniConnessione();
              // (02)
              PreparedStatement statement = connessione.prepareStatement("SELECT username, punteggio FROM punteggi WHERE timestamp > ? ORDER BY punteggio DESC");
        ){
            statement.setTimestamp(1, timestampIniziale);
            ResultSet risultato = statement.executeQuery();
            
            while(risultato.next()){ // (03)
                if(punteggioPrecedente != risultato.getInt("punteggio")){ // (04)
                    count++;
                    punteggioPrecedente = risultato.getInt("punteggio");
                }
                
                listaPunteggi.add(new Punteggio(count, risultato.getString("username"), risultato.getInt("punteggio")));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        
        return listaPunteggi;
    }
    
    public int ottieniMigliorPunteggioPersonalePosterioriAData(String username, LocalDate dataIniziale){
        List<Punteggio> listaPunteggi = ottieniPunteggiOrdinatiPosterioriAData(dataIniziale);
        int migliorPunteggioPersonale = 0;
        
        for(Punteggio punteggio: listaPunteggi){ // (05)
            if(punteggio.getUsername().equals(username) && punteggio.getPunteggio() > migliorPunteggioPersonale)
                migliorPunteggioPersonale = punteggio.getPunteggio();
        }
        
        return migliorPunteggioPersonale;
    }
    
    public void inserisciPunteggio(String username, int punteggio){
        
        try ( Connection connessione = ottieniConnessione();
              PreparedStatement statement = connessione.prepareStatement("INSERT INTO punteggi (username, punteggio) VALUES (?,?)"); // (06)
        ){
            statement.setString(1, username);
            statement.setInt(2, punteggio);
            statement.executeUpdate();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }
}

/*
(00)
    Provo a connettermi al database; ritorno la connessione se l'operazione ha successo,
    altrimenti lancio una eccezione
(01)
    Converto la data di riferimento in un timestamp per poterlo usare nella query
(02)
    Preparo lo statement utile ad ottenere la lista dei punteggi con data successiva alla 
    data di riferimento e ordinati in ordine decrescente di punteggio
(03)
    Per ogni punteggio, lo aggiungo alla lista dei punteggi aggiungendo pure il suo numero 
    di ordine nel result set
(04)
    Nel caso ci siano risultati ex aequo, li considero con lo stesso numero di ordine in classifica
(05)
    Scorro tutta la lista dei punteggi e ne ricavo il miglior punteggio relativo
    allo username di riferimento
(06)
    Preparo lo statement utile per inserire un nuovo punteggio nel database
*/
