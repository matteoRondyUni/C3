package it.unicam.cs.ids.C3.TeamMGC.javaModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.executeQuery;
import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.updateData;

public class Ordine {

    private int ID;
    private int IDCliente;
    private String nomeCliente;
    private String cognomeCliente;
    private double totalePrezzo;
    private StatoOrdine stato;
    private PuntoPrelievo puntoPrelievo = null;
    private String residenza = null;
    private ArrayList<Merce> merci = new ArrayList<>();


    public Ordine(int ID, int IDCliente, String nomeCliente, String cognomeCliente, double totalePrezzo, StatoOrdine stato, PuntoPrelievo puntoPrelievo) {
        this.ID = ID;
        this.IDCliente = IDCliente;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.totalePrezzo = totalePrezzo;
        this.stato = stato;
        this.puntoPrelievo = puntoPrelievo;
    }

    public Ordine(int IDCliente, String nomeCliente, String cognomeCliente, double totalePrezzo, StatoOrdine stato, PuntoPrelievo puntoPrelievo) {
        try {
            updateData("INSERT INTO `sys`.`ordini` (`IDCliente`, `nomeCliente`,`cognomeCliente`,`totalePrezzo`,`stato`,`puntoPrelievo`,`residenza`) " +
                    "VALUES ('" + IDCliente + "', '" + nomeCliente + "', '" + cognomeCliente + "', '" + totalePrezzo + "', '" + stato + "'," +
                    "'" + puntoPrelievo.getNome() + "', \"null\");");
            ResultSet rs = executeQuery("SELECT MAX(ID) as ID from ordini;");
            rs.next();
            ID = rs.getInt("ID");
            this.IDCliente = IDCliente;
            this.nomeCliente = nomeCliente;
            this.cognomeCliente = cognomeCliente;
            this.totalePrezzo = totalePrezzo;
            this.puntoPrelievo = puntoPrelievo;
            this.residenza = null;
        } catch (SQLException exception) {
            //todo
            exception.printStackTrace();
        }
    }

    public int getID() {
        return ID;
    }

    public int getIDCliente() {
        return IDCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public double getTotalePrezzo() {
        return totalePrezzo;
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public PuntoPrelievo getPuntoPrelievo() {
        return puntoPrelievo;
    }

    public String getResidenza() {
        return residenza;
    }

    public ArrayList<Merce> getMerci() {
        return merci;
    }

    /**
     * Aggiunge l'indirizzo della residenza all'ordine.
     *
     * @param indirizzo Indirizzo residenza del cliente
     */
    public void addResidenza(String indirizzo) {
        try {
            updateData("UPDATE `sys`.`ordini` SET `puntoPrelievo` =  \"null\" WHERE (`ID` = '" + this.ID + "');");
            updateData("UPDATE `sys`.`ordini` SET `residenza` = '" + indirizzo + "' WHERE (`ID` = '" + this.ID + "');");
            puntoPrelievo = null;
            residenza = indirizzo;
        } catch (SQLException exception) {
            //TODO
            exception.printStackTrace();
        }
    }

    /**
     * Aggiunge la merce all'ordine del cliente.
     *
     * @param merce    Merce da aggiungere
     * @param quantita Quantita della merce da aggiungere
     */
    public void aggiungiMerce(Merce merce, int quantita) {
        merce.setQuantita(quantita);
        merci.add(merce);
        merce.setIDOrdine(this.getID());
    }

    /**
     * Ritorna un arraylist con i dettagli dell'ordine in stringa.
     * @return      ArrayList dei dettagli
     */
    public ArrayList<String> getDettagli() {
        ArrayList<String> ordini = new ArrayList<>();
        ordini.add(String.valueOf(getID()));
        ordini.add(String.valueOf(getIDCliente()));
        ordini.add(getNomeCliente());
        ordini.add(getCognomeCliente());
        ordini.add(String.valueOf(getTotalePrezzo()));
        ordini.add(getStato().toString());

        if (puntoPrelievo != null)
            ordini.add(String.valueOf(getPuntoPrelievo()));
        else
            ordini.add(String.valueOf(getResidenza()));

        ordini.add(String.valueOf(getMerci()));
        return ordini;
    }

    public void riceviPagamento() {
        // TODO - implement Ordine.riceviPagamento
        throw new UnsupportedOperationException();
    }

    public void setPuntoPrelievo(PuntoPrelievo magazzino) {
        try {
            updateData("UPDATE `sys`.`ordini` SET `puntoPrelievo` = '" + magazzino.getNome() + "' WHERE (`ID` = '" + this.ID + "');");
            this.puntoPrelievo = magazzino;
        } catch (SQLException exception) {
            //TODO
            exception.printStackTrace();
        }
    }

    public void setStato(StatoOrdine statoOrdine) {
        try {
            stato = statoOrdine;
            updateData("UPDATE `sys`.`ordini` SET `stato` = '" + statoOrdine + "' WHERE (`ID` = '" + this.ID + "');");
            this.stato = statoOrdine;
        } catch (SQLException exception) {
            //TODO
            exception.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ordine ordine = (Ordine) o;
        return ID == ordine.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }


}

