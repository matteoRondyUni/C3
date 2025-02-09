package it.unicam.cs.ids.C3.TeamMGC.personale;

import it.unicam.cs.ids.C3.TeamMGC.negozio.Negozio;

import java.sql.SQLException;

/**
 * La classe estende la classe astratta {@link Personale} ed ha la responsabilità di gestire un Commerciante.
 *
 * @author Matteo Rondini, Greta Sorritelli, Clarissa Albanese
 */
public class Commerciante extends Personale {

    /**
     * Costruttore per importare i dati dal DB.
     *
     * @param ID ID del Commerciante
     *
     * @throws SQLException Errore causato da una query SQL
     */
    public Commerciante(int ID) throws SQLException {
        super(ID);
    }

    /**
     * Costruttore per inserire i dati nel DB.
     *
     * @param IDNegozio ID del {@link Negozio}
     * @param nome      Nome del Commerciante
     * @param cognome   Cognome del Commerciante
     *
     * @throws SQLException Errore causato da una query SQL
     */
    public Commerciante(int IDNegozio, String nome, String cognome) throws SQLException {
        super(Ruolo.COMMERCIANTE, IDNegozio, nome, cognome);
    }
}