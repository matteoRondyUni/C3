package it.unicam.cs.ids.C3.TeamMGC.cliente;

import it.unicam.cs.ids.C3.TeamMGC.ordine.Ordine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.updateData;
import static org.junit.jupiter.api.Assertions.*;

class GestoreClientiTest {

    @BeforeAll
    static void clearDB() throws SQLException {
        updateData("delete from sys.clienti;");
        updateData("alter table clienti AUTO_INCREMENT = 1;");
        updateData("INSERT INTO `sys`.`clienti` (`nome`, `cognome`) VALUES ('Clarissa', 'Albanese');");
        updateData("INSERT INTO `sys`.`clienti` (`nome`, `cognome`) VALUES ('Matteo', 'Rondini');");
    }

    @Test
    void getClienti() throws SQLException {
        GestoreClienti gestoreClienti = new GestoreClienti();
        ArrayList<Cliente> test = gestoreClienti.getItems();
        assertEquals(1, test.get(0).getID());
        assertEquals(2, test.get(1).getID());
        assertEquals("Clarissa", test.get(0).getNome());
        assertEquals("Matteo", test.get(1).getNome());
        assertEquals("Albanese", test.get(0).getCognome());
        assertEquals("Rondini", test.get(1).getCognome());

    }

    @Test
    void getCliente() throws SQLException {
        GestoreClienti gestoreClienti = new GestoreClienti();
        assertEquals("Clarissa", gestoreClienti.getItem(1).getNome());
        assertEquals("Matteo", gestoreClienti.getItem(2).getNome());
        assertEquals("Albanese", gestoreClienti.getItem(1).getCognome());
        assertEquals("Rondini", gestoreClienti.getItem(2).getCognome());
    }

    @Test
    void getDettagliClienti() throws SQLException {
        GestoreClienti gestoreClienti = new GestoreClienti();
        ArrayList<ArrayList<String>> test = gestoreClienti.getDettagliItems();
        assertEquals(test.get(0).get(0), "1");
        assertEquals(test.get(0).get(1),"Clarissa");
        assertEquals(test.get(0).get(2), "Albanese");
        assertEquals(test.get(1).get(0), "2");
        assertEquals(test.get(1).get(1),"Matteo");
        assertEquals(test.get(1).get(2), "Rondini");
    }

    @Test
    //todo controllare
    void verificaEsistenzaCodiceTest() throws SQLException {
        GestoreClienti gestoreClienti = new GestoreClienti();
        ArrayList<String> dettagli = gestoreClienti.inserisciDati("Mario","Rossi");
        Ordine ordine = new Ordine(Integer.parseInt(dettagli.get(0)), "Mario", "Rossi");
        String codice = gestoreClienti.verificaEsistenzaCodice(Integer.parseInt(dettagli.get(0)), ordine.getID());
        Cliente cliente = gestoreClienti.getItem(Integer.parseInt(dettagli.get(0)));
        assertNotNull(cliente.getCodiceRitiro());
        assertEquals(codice, cliente.getCodiceRitiro());
        assertTrue(gestoreClienti.verificaCodice(cliente.getID(), codice));
    }
}