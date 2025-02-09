package it.unicam.cs.ids.C3.TeamMGC.corriere;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.executeQuery;
import static it.unicam.cs.ids.C3.TeamMGC.javaPercistence.DatabaseConnection.updateData;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCorriereTest {

    @BeforeAll
    static void clearDB() throws SQLException {
        updateData("DELETE from sys.corrieri;");
        updateData("alter table corrieri AUTO_INCREMENT = 1;");
    }

    @BeforeAll
    static void creazioneCorriere() throws SQLException {
        Corriere simpleCorriere = new SimpleCorriere("Clarissa", "Albanese", true);
        assertEquals(1, simpleCorriere.getID());
        assertEquals("Clarissa", simpleCorriere.getNome());
        assertEquals("Albanese", simpleCorriere.getCognome());
        assertTrue(simpleCorriere.getDisponibilita());
        Exception e1 = assertThrows(IllegalArgumentException.class, () -> new SimpleCorriere(1000));
        assertEquals("ID non valido.", e1.getMessage());
    }

    @Test
    void compareTo() throws SQLException {
        Corriere corriere = new SimpleCorriere("Clarissa", "Albanese", true);
        Corriere corriereCopia = new SimpleCorriere(corriere.getID());
        Corriere corriere2 = new SimpleCorriere("Matteo", "Rondini", true);

        assertEquals(0, corriere.compareTo(corriereCopia));
        assertEquals(-1, corriere.compareTo(corriere2));
        assertEquals(1, corriere2.compareTo(corriere));

        assertThrows(NullPointerException.class, () -> corriere.compareTo(null));
    }

    @Test
    void setNome() throws SQLException {
        Corriere simpleCorriereTest = new SimpleCorriere("Greta", "Sorritelli", true);
        assertEquals("Greta",simpleCorriereTest.getNome());
        simpleCorriereTest.setNome("Clarissa");
        assertEquals("Clarissa",simpleCorriereTest.getNome());

        ResultSet rs = executeQuery("SELECT nome FROM sys.corrieri where ID = " + simpleCorriereTest.getID() + ";");
        if (rs.next())
            assertEquals("Clarissa", rs.getString("nome"));
    }

    @Test
    void setCognome() throws SQLException {
        Corriere simpleCorriereTest = new SimpleCorriere("Giuseppe", "Biondi", true);
        assertEquals("Biondi",simpleCorriereTest.getCognome());
        simpleCorriereTest.setCognome("Rossi");
        assertEquals("Rossi",simpleCorriereTest.getCognome());

        ResultSet rs = executeQuery("SELECT cognome FROM sys.corrieri where ID = " + simpleCorriereTest.getID() + ";");
        if (rs.next())
            assertEquals("Rossi", rs.getString("cognome"));
    }


    @Test
    void getDettagli() throws SQLException {
        Corriere simpleCorriereTest = new SimpleCorriere("Mario", "Rossi", true);
        ArrayList<String> corriereLista = new ArrayList<>();
        corriereLista.add(String.valueOf(simpleCorriereTest.getID()));
        corriereLista.add("Mario");
        corriereLista.add("Rossi");
        corriereLista.add("true");
        assertEquals(corriereLista, simpleCorriereTest.getDettagli());

        simpleCorriereTest.setDisponibilita(false);
        corriereLista.clear();
        corriereLista.add(String.valueOf(simpleCorriereTest.getID()));
        corriereLista.add("Mario");
        corriereLista.add("Rossi");
        corriereLista.add("false");
        assertEquals(corriereLista, simpleCorriereTest.getDettagli());
    }

    @Test
    void setDisponibilita() throws SQLException {
        Corriere simpleCorriereTest = new SimpleCorriere("Greta", "Sorritelli", true);
        assertTrue(simpleCorriereTest.getDisponibilita());
        simpleCorriereTest.setDisponibilita(false);
        assertFalse(simpleCorriereTest.getDisponibilita());

        ResultSet rs = executeQuery("SELECT stato FROM sys.corrieri where ID = " + simpleCorriereTest.getID() + ";");
        if (rs.next())
            assertEquals("false", rs.getString("stato"));
    }

    @Test
    void testEquals_toString_hashCode() throws SQLException {
        Corriere simpleCorriere = new SimpleCorriere("Fry", "Philip", true);
        Corriere simpleCorriereCopia = new SimpleCorriere(simpleCorriere.getID());
        Corriere simpleCorriere2 = new SimpleCorriere("Leela", "Turanga", true);
        assertEquals(simpleCorriere, simpleCorriereCopia);
        assertNotEquals(simpleCorriere, simpleCorriere2);
        assertEquals(simpleCorriere.hashCode(),simpleCorriereCopia.hashCode());
        assertEquals("ID=" + simpleCorriere.getID() + ", nome='Fry', cognome='Philip', disponibilita=true",simpleCorriere.toString());
        assertEquals("ID=" + simpleCorriere2.getID() + ", nome='Leela', cognome='Turanga', disponibilita=true",simpleCorriere2.toString());
    }

    @Test
    void update() throws SQLException {
        Corriere simpleCorriereTest = new SimpleCorriere("Mario", "Rossi", true);
        int IDTest = simpleCorriereTest.getID();
        assertEquals(simpleCorriereTest.getNome(), "Mario");
        assertEquals(simpleCorriereTest.getCognome(), "Rossi");
        assertTrue(simpleCorriereTest.getDisponibilita());

        updateData("UPDATE sys.corrieri SET nome = 'Giovanni' WHERE (ID = '" + IDTest + "');");
        updateData("UPDATE sys.corrieri SET cognome = 'Bazzi' WHERE (ID = '" + IDTest + "');");
        updateData("UPDATE sys.corrieri SET stato = 'false' WHERE (ID = '" + IDTest + "');");

        simpleCorriereTest.update();
        assertEquals(simpleCorriereTest.getNome(), "Giovanni");
        assertEquals(simpleCorriereTest.getCognome(), "Bazzi");
        assertFalse(simpleCorriereTest.getDisponibilita());
    }
}