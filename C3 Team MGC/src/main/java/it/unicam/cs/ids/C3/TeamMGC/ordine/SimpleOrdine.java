package it.unicam.cs.ids.C3.TeamMGC.ordine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.*;

/**
 * Classe per la creazione di un {@link SimpleOrdine}
 *
 * @author Matteo Rondini, Greta Sorritelli, Clarissa Albanese
 */
public class SimpleOrdine implements Ordine {
    private final int ID;
    private final int IDCliente;
    private final int IDNegozio;
    private final ArrayList<MerceOrdine> merci = new ArrayList<>();
    private String nomeCliente;
    private String cognomeCliente;
    private double totalePrezzo;
    private StatoOrdine stato = StatoOrdine.DA_PAGARE;
    private int IDPuntoPrelievo = -1;
    private String residenza = "";

    /**
     * Costruttore per importare i dati dal DB.
     *
     * @param ID ID dell' Ordine
     *
     * @throws SQLException Errore causato da una query SQL
     */
    public SimpleOrdine(int ID) throws SQLException {
        ResultSet rs = executeQuery("select * from ordini where ID ='" + ID + "';");
        if (rs.next()) {
            this.ID = ID;
            this.IDCliente = rs.getInt("IDCliente");
            this.nomeCliente = rs.getString("nomeCliente");
            this.cognomeCliente = rs.getString("cognomeCliente");
            this.totalePrezzo = rs.getDouble("totalePrezzo");
            this.stato = StatoOrdine.valueOf(rs.getString("stato"));
            this.IDPuntoPrelievo = rs.getInt("IDPuntoPrelievo");
            this.residenza = rs.getString("residenza");
            this.IDNegozio = rs.getInt("IDNegozio");

            rs = executeQuery("select ID from merci where IDOrdine ='" + ID + "';");
            while (rs.next())
                addMerceToOrdine(new SimpleMerceOrdine(rs.getInt("ID")));
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
    public SimpleOrdine(int IDCliente, String nomeCliente, String cognomeCliente, int IDNegozio) throws SQLException {
        updateData("INSERT INTO sys.ordini (IDCliente, nomeCliente, cognomeCliente, stato, IDNegozio, IDPuntoPrelievo) " +
                "VALUES ('" + IDCliente + "', '" + nomeCliente + "', '" + cognomeCliente + "', '" + "DA_PAGARE', '" + IDNegozio + "', '" + IDPuntoPrelievo + "');");
        ResultSet rs = executeQuery("SELECT MAX(ID) as ID from ordini;");
        rs.next();
        ID = rs.getInt("ID");
        this.IDCliente = IDCliente;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.IDNegozio = IDNegozio;
        disconnectToDB(rs);
    }

    /**
     * Aggiunge la {@link SimpleMerceOrdine} all'{@link SimpleOrdine}, prendendo i dati dal Database.
     *
     * @param merce Merce da aggiungere
     */
    //todo rivedere il commento e test
    @Override
    public void addMerce(MerceOrdine merce) throws SQLException {
        addMerceToOrdine(merce);
        update();
    }

    /**
     * @param rs
     *
     * @throws SQLException
     */
    private void addMerce(ResultSet rs) throws SQLException {
        while (rs.next())
            addMerceToOrdine(new SimpleMerceOrdine(rs.getInt("ID")));
    }

    /**
     * @param simpleMerceOrdine
     */
    private void addMerceToOrdine(MerceOrdine simpleMerceOrdine) {
        if (!merci.contains(simpleMerceOrdine))
            merci.add(simpleMerceOrdine);
    }

    /**
     * Aggiunge l'{@code indirizzo} della residenza all'{@link SimpleOrdine}.
     *
     * @param indirizzo Indirizzo residenza del cliente
     *
     * @throws SQLException Errore causato da una query SQL
     */
    @Override
    public void addResidenza(String indirizzo) throws SQLException {
        updateData("UPDATE sys.ordini SET IDPuntoPrelievo = -1 WHERE (ID = '" + this.ID + "');");
        updateData("UPDATE sys.ordini SET residenza = '" + indirizzo + "' WHERE (ID = '" + this.ID + "');");
        IDPuntoPrelievo = -1;
        residenza = indirizzo;
    }

    /**
     * Aggiunge la {@link SimpleMerceOrdine} all'{@link SimpleOrdine} del cliente.
     *
     * @param merce    Merce da aggiungere
     * @param quantita Quantita della merce da aggiungere
     *
     * @throws SQLException Errore causato da una query SQL
     */
    //todo
    @Override
    public void aggiungiMerce(MerceOrdine merce, int quantita) throws SQLException {
        merce.setQuantita(quantita);
        merci.add(merce);
        this.totalePrezzo += (merce.getPrezzo() * quantita);
        updateData("UPDATE sys.ordini SET totalePrezzo = '" + this.totalePrezzo + "' WHERE (ID = '" + this.ID + "');");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleOrdine simpleOrdine = (SimpleOrdine) o;
        return ID == simpleOrdine.ID;
    }

    @Override
    public String getCognomeCliente() {
        return cognomeCliente;
    }

    /**
     * Ritorna un arraylist con i dettagli dell' {@link SimpleOrdine}.
     *
     * @return ArrayList dei dettagli dell' ordine
     * @throws SQLException Errore causato da una query SQL
     */
    @Override
    public ArrayList<String> getDettagli() throws SQLException {
        update();
        ArrayList<String> ordini = new ArrayList<>();
        ordini.add(String.valueOf(getID()));
        ordini.add(String.valueOf(getIDCliente()));
        ordini.add(getNomeCliente());
        ordini.add(getCognomeCliente());
        ordini.add(String.valueOf(getTotalePrezzo()));
        ordini.add(getStato().toString());

        if (IDPuntoPrelievo != -1 && stato != StatoOrdine.RITIRATO)
            ordini.add(String.valueOf(getPuntoPrelievo()));
        else
            ordini.add(String.valueOf(getResidenza()));

        ordini.add(String.valueOf(IDNegozio));

        for (MerceOrdine merce : merci)
            merce.update();

        ordini.add(String.valueOf(getMerci()));
        return ordini;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public int getIDCliente() {
        return IDCliente;
    }

    @Override
    public int getIDNegozio() {
        return IDNegozio;
    }

    @Override
    public ArrayList<MerceOrdine> getMerci() throws SQLException {
        return new ArrayList<>(merci);
    }

    @Override
    public String getNomeCliente() {
        return nomeCliente;
    }

    @Override
    public int getPuntoPrelievo() {
        return IDPuntoPrelievo;
    }

    @Override
    public void setPuntoPrelievo(int IDPuntoPrelievo) throws SQLException {
        updateData("UPDATE sys.ordini SET IDPuntoPrelievo = " + IDPuntoPrelievo + " WHERE ID = " + this.ID + ";");
        this.IDPuntoPrelievo = IDPuntoPrelievo;
    }

    @Override
    public String getResidenza() {
        return residenza;
    }

    @Override
    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }

    @Override
    public StatoOrdine getStato() {
        return stato;
    }

    @Override
    public void setStato(StatoOrdine statoOrdine) throws SQLException {
        updateData("UPDATE sys.ordini SET stato = '" + statoOrdine + "' WHERE (ID = '" + this.ID + "');");
        this.stato = statoOrdine;
    }

    @Override
    public double getTotalePrezzo() {
        return totalePrezzo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
        return "ID=" + ID +
                ", IDCliente=" + IDCliente +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", cognomeCliente='" + cognomeCliente + '\'' +
                ", totalePrezzo=" + totalePrezzo +
                ", stato=" + stato +
                ", IDPuntoPrelievo=" + IDPuntoPrelievo +
                ", residenza='" + residenza + '\'' +
                ", IDNegozio=" + IDNegozio +
                ", merci=" + merci;
    }

    /**
     * Aggiorna i valori all' interno dell' oggetto prendendo i dati dal DB.
     *
     * @throws SQLException Errore causato da una query SQL
     */
    //todo rivedere test
    @Override
    public void update() throws SQLException {
        ResultSet rs = executeQuery("select * from sys.ordini where ID= '" + this.ID + "';");
        if (rs.next()) {
            this.nomeCliente = rs.getString("nomeCliente");
            this.cognomeCliente = rs.getString("cognomeCliente");
            this.totalePrezzo = rs.getInt("totalePrezzo");
            if (!rs.getString("stato").isEmpty())
                this.stato = StatoOrdine.valueOf(rs.getString("stato"));
            this.IDPuntoPrelievo = rs.getInt("IDPuntoPrelievo");
            this.residenza = rs.getString("residenza");

            addMerce(executeQuery("select ID from merci where IDOrdine ='" + ID + "';"));
            for (MerceOrdine simpleMerceOrdine : merci)
                simpleMerceOrdine.update();
        }
        disconnectToDB(rs);
    }
}
