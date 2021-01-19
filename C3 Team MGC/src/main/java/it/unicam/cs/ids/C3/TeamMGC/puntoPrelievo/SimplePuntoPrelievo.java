package it.unicam.cs.ids.C3.TeamMGC.puntoPrelievo;

import it.unicam.cs.ids.C3.TeamMGC.ordine.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.*;

/**
 * Classe per la creazione di un {@link SimplePuntoPrelievo}
 *
 * @author Matteo Rondini, Greta Sorritelli, Clarissa Albanese
 */
public class SimplePuntoPrelievo implements PuntoPrelievo {
    private int ID = 0;
    private String indirizzo = "";
    private String nome = "";

    /**
     * Costruttore per importare i dati dal DB.
     *
     * @param ID ID del PuntoPrelievo
     *
     * @throws SQLException Errore causato da una query SQL
     */
    public SimplePuntoPrelievo(int ID) throws SQLException {
        ResultSet rs = executeQuery("select * from punti_prelievo where ID ='" + ID + "';");
        if (rs.next()) {
            this.ID = ID;
            this.indirizzo = rs.getString("indirizzo");
            this.nome = rs.getString("nome");
            disconnectToDB(rs);
        } else {
            disconnectToDB(rs);
            throw new IllegalArgumentException("ID non valido.");
        }
    }

    /**
     * Costruttore per inserire i dati nel DB
     *
     * @throws SQLException eccezione causata da una query SQL
     */
    public SimplePuntoPrelievo(String indirizzo, String nome) throws SQLException {
        updateData("INSERT INTO sys.punti_prelievo (nome,indirizzo) \n" +
                "VALUES ('" + nome + "' , '" + indirizzo + "');");
        ResultSet rs = executeQuery("SELECT MAX(ID) as ID from punti_prelievo;");
        rs.next();
        this.ID = rs.getInt("ID");
        this.nome = nome;
        this.indirizzo = indirizzo;
        disconnectToDB(rs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuntoPrelievo that = (SimplePuntoPrelievo) o;
        return getID() == that.getID();
    }

    /**
     * Ritorna un arraylist con i dettagli del {@link SimplePuntoPrelievo}.
     *
     * @return ArrayList dei dettagli
     *
     * @throws SQLException eccezione causa da una query SQL
     */
    @Override
    public ArrayList<String> getDettagli() throws SQLException {
        update();
        ArrayList<String> dettagli = new ArrayList<>();
        dettagli.add((String.valueOf(getID())));
        dettagli.add(getNome());
        dettagli.add(getIndirizzo());
        return dettagli;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Ritorna la lista di tutte le merci appartenenti a tale ordine e presenti nel {@link SimplePuntoPrelievo}.
     *
     * @param IDOrdine ID dell' ordine
     *
     * @throws SQLException Errore causato da una query SQL
     */
    @Override
    public ArrayList<MerceOrdine> getMerceMagazzino(int IDOrdine) throws SQLException {
        ArrayList<MerceOrdine> lista = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * from merci\n" +
                "where IDOrdine = " + IDOrdine + " and stato = '" + StatoOrdine.IN_DEPOSITO.toString() + "';");
        while (rs.next()) {
            MerceOrdine simpleMerceOrdine = new SimpleMerceOrdine(rs.getInt("ID"));
            lista.add(simpleMerceOrdine);
        }
        disconnectToDB(rs);
        return lista;
    }

    @Override
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna l' insieme degli ordini effettuati dal cliente e presenti in tale {@link SimplePuntoPrelievo}.
     *
     * @param IDCliente ID del cliente
     *
     * @throws SQLException Errore causato da una query SQL
     */
    @Override
    public ArrayList<Ordine> getOrdini(int IDCliente) throws SQLException {
        ArrayList<Ordine> lista = new ArrayList<>();
        ResultSet rs = executeQuery("SELECT * from ordini WHERE IDCliente = '" + IDCliente +
                "' AND IDPuntoPrelievo = '" + this.ID + "' and stato = '" + StatoOrdine.IN_DEPOSITO + "' ;");
        while (rs.next()) {
            Ordine simpleOrdine = new SimpleOrdine(rs.getInt("ID"));
            for (MerceOrdine merciToAdd : getMerceMagazzino(simpleOrdine.getID()))
                simpleOrdine.addMerce(merciToAdd);
            lista.add(simpleOrdine);
        }
        disconnectToDB(rs);
        return lista;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @Override
    public String toString() {
        return "ID=" + ID +
                ", indirizzo='" + indirizzo + '\'' +
                ", nome='" + nome;
    }

    /**
     * Aggiorna i valori all' interno dell' oggetto prendendo i dati dal DB.
     *
     * @throws SQLException Errore causato da una query SQL
     */
    @Override
    public void update() throws SQLException {
        ResultSet rs = executeQuery("select * from sys.punti_prelievo where ID= '" + this.ID + "';");
        if (rs.next()) {
            this.indirizzo = rs.getString("indirizzo");
            this.nome = rs.getString("nome");
        }
        disconnectToDB(rs);
    }

}