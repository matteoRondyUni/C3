package it.unicam.cs.ids.C3.TeamMGC.cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.executeQuery;
import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.updateData;
import static org.junit.jupiter.api.Assertions.*;

class SimpleClienteTest {

    @BeforeEach
    void clearDB() throws SQLException {
        updateData("delete from sys.clienti;");
        updateData("alter table clienti AUTO_INCREMENT = 1;");
    }

    @Test
    void creazioneCliente() throws SQLException {
        Cliente simpleCliente = new SimpleCliente("Mario", "Rossi");
        assertEquals("Mario", simpleCliente.getNome());
        assertEquals("Rossi", simpleCliente.getCognome());
        assertEquals("", simpleCliente.getCodiceRitiro());
        assertEquals("", simpleCliente.getDataCreazioneCodice());
        Exception e1 = assertThrows(IllegalArgumentException.class, () -> new SimpleCliente(1000));
        assertEquals("ID cliente non valido.", e1.getMessage());
    }

    @Test
    void compareTo() throws SQLException {
        Cliente simpleCliente = new SimpleCliente("Mario", "Rossi");
        Cliente simpleClienteCopia = new SimpleCliente(simpleCliente.getID());
        Cliente simpleCliente2 = new SimpleCliente("Giuseppe", "Verdi");

        assertEquals(0, simpleCliente.compareTo(simpleClienteCopia));
        assertEquals(-1, simpleCliente.compareTo(simpleCliente2));
        assertEquals(1, simpleCliente2.compareTo(simpleCliente));

        assertThrows(NullPointerException.class, () -> simpleCliente.compareTo(null));
    }

    @Test
    void getDettagli() throws SQLException {
        Cliente simpleClienteTest = new SimpleCliente("Greta", "Sorritelli");
        ArrayList<String> clienteLista = new ArrayList<>();
        clienteLista.add(String.valueOf(simpleClienteTest.getID()));
        clienteLista.add("Greta");
        clienteLista.add("Sorritelli");
        clienteLista.add("");
        clienteLista.add("");
        assertEquals(clienteLista, simpleClienteTest.getDettagli());

        simpleClienteTest.setCodiceRitiro("123456789");
        String data = new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now()));
        clienteLista.clear();
        clienteLista.add(String.valueOf(simpleClienteTest.getID()));
        clienteLista.add("Greta");
        clienteLista.add("Sorritelli");
        clienteLista.add("123456789");
        clienteLista.add(data);
        assertEquals(clienteLista, simpleClienteTest.getDettagli());
    }

    @Test
    void setCodiceRitiro() throws SQLException {
        Cliente simpleCliente = new SimpleCliente("Matteo", "Rondini");
        String data = new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now()));
        assertEquals("", simpleCliente.getDataCreazioneCodice());
        simpleCliente.setCodiceRitiro("85963214");
        assertEquals("85963214", simpleCliente.getCodiceRitiro());
        assertEquals(data, simpleCliente.getDataCreazioneCodice());

        ResultSet rs = executeQuery("SELECT codiceRitiro FROM sys.clienti where ID = " + simpleCliente.getID() + ";");
        if (rs.next())
            assertEquals("85963214", rs.getString("codiceRitiro"));
    }

    @Test
    void testEquals() throws SQLException {
        Cliente simpleCliente = new SimpleCliente("Carole", "Stanley");
        Cliente simpleClienteCopia = new SimpleCliente(simpleCliente.getID());
        Cliente simpleCliente2 = new SimpleCliente("Tuesday", "Simmons");
        assertEquals(simpleCliente, simpleClienteCopia);
        assertNotEquals(simpleCliente, simpleCliente2);
        assertEquals(simpleCliente.hashCode(), simpleClienteCopia.hashCode());
    }

    @Test
    void update() throws SQLException {
        Cliente simpleClienteTest = new SimpleCliente("Luigi", "Bianchi");
        int IDTest = simpleClienteTest.getID();
        assertEquals("Luigi", simpleClienteTest.getNome());
        assertEquals("Bianchi", simpleClienteTest.getCognome());
        assertEquals("", simpleClienteTest.getCodiceRitiro());
        assertEquals("", simpleClienteTest.getDataCreazioneCodice());

        updateData("UPDATE sys.clienti SET nome = 'Waluigi' WHERE (ID = '" + IDTest + "');");
        updateData("UPDATE sys.clienti SET cognome = 'Neri' WHERE (ID = '" + IDTest + "');");
        updateData("UPDATE sys.clienti SET codiceRitiro = '123456789' WHERE (ID = '" + IDTest + "');");
        String data = new SimpleDateFormat("yyyy-MM-dd").format(java.sql.Date.from(Instant.now()));
        updateData("UPDATE sys.clienti SET dataCreazione = '" + data + "' WHERE (ID = '" + IDTest + "');");

        simpleClienteTest.update();
        assertEquals("Waluigi", simpleClienteTest.getNome());
        assertEquals("Neri", simpleClienteTest.getCognome());
        assertEquals("123456789", simpleClienteTest.getCodiceRitiro());
        assertEquals(data, simpleClienteTest.getDataCreazioneCodice());
    }
}