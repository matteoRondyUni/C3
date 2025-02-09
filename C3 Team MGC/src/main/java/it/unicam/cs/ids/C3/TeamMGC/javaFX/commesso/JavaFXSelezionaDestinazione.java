
package it.unicam.cs.ids.C3.TeamMGC.javaFX.commesso;

import it.unicam.cs.ids.C3.TeamMGC.corriere.GestoreCorrieri;
import it.unicam.cs.ids.C3.TeamMGC.javaFX.JavaFXController;
import it.unicam.cs.ids.C3.TeamMGC.negozio.Negozio;
import it.unicam.cs.ids.C3.TeamMGC.ordine.GestoreOrdini;
import it.unicam.cs.ids.C3.TeamMGC.ordine.StatoOrdine;
import it.unicam.cs.ids.C3.TeamMGC.puntoPrelievo.GestoreMagazzini;
import it.unicam.cs.ids.C3.TeamMGC.puntoPrelievo.PuntoPrelievo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

/**
 * Controller della pagina per selezionare il Punto di Prelievo.
 *
 * @author Matteo Rondini, Greta Sorritelli, Clarissa Albanese
 */
public class JavaFXSelezionaDestinazione implements JavaFXController {
    private final GestoreOrdini gestoreOrdini = GestoreOrdini.getInstance();
    private final GestoreMagazzini gestoreMagazzini = GestoreMagazzini.getInstance();
    private final GestoreCorrieri gestoreCorrieri = GestoreCorrieri.getInstance();
    private final Negozio negozio;
    private final int IDOrdine;


    public JavaFXSelezionaDestinazione(int IDOrdine, Negozio negozio) {
        this.IDOrdine = IDOrdine;
        this.negozio = negozio;
    }

    /**
     * TabPane della finestra
     */
    //todo levare
    @FXML
    TabPane tab;

    @FXML
    Tab consegnaPP;

    @FXML
    Tab consegnaResidenza;

    @FXML
    Tab ritiro;

    @FXML
    ChoiceBox<PuntoPrelievo> choicePuntoPrelievo = new ChoiceBox<>();

    @FXML
    TextField residenza;


    public void addResidenza() {
        try {
            if (!residenza.getText().isEmpty()) {
                gestoreCorrieri.getCorrieriDisponibili();
                gestoreOrdini.addResidenza(IDOrdine, residenza.getText());
                successWindow("Residenza salvata con successo!", "Ora potrai scegliere il corriere da avvisare.", 2);
                sceltaCorriere();
                closeWindow((Stage) residenza.getScene().getWindow());
            } else
                throw new NullPointerException("Dati non presenti.");
        } catch (NullPointerException exception) {
            alertWindow("Alert!", "Inserire la residenza.");
        } catch (IllegalArgumentException exception) {
            errorWindow("Non ci sono corrieri disponibili.", "Scegliere di nuovo una modalita' di consegna.");
            residenza.clear();
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    public void sceltaCorriere() {
        openWindow("/commesso/SelezionaCorriere.fxml", "SelezionaCorriere", new JavaFXSelezionaCorriere(residenza.getText(), negozio, IDOrdine));
    }


    public void setPuntoPrelievo() {
        try {
            if (choicePuntoPrelievo.getValue() != null) {
                gestoreOrdini.setPuntoPrelievo(IDOrdine, choicePuntoPrelievo.getValue().getID());
                mandaAlert();
                closeWindow((Stage) residenza.getScene().getWindow());
            } else
                throw new IllegalArgumentException("Dati non presenti.");
        } catch (IllegalArgumentException exception) {
            if(exception.getMessage().equals("Dati non presenti."))
            alertWindow("Alert!", "Inserire tutti i dati richiesti.");
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    public void mandaAlert() {
        try {
                gestoreMagazzini.mandaAlert(choicePuntoPrelievo.getValue().getID(), negozio);
            successWindow("Alert mandato con successo!", "L' alert e' stato inviato al magazziniere.", 2);
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    public void setStatoOrdine() {
        try {
            gestoreOrdini.setStatoOrdine(IDOrdine, StatoOrdine.RITIRATO);
            successWindow("Ordine ritirato con successo!", "Lo stato dell' ordine e' stato impostato a ritirato.", 2);
            closeWindow((Stage) residenza.getScene().getWindow());
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    @FXML
    public void showMagazzini() {
        try {
            choicePuntoPrelievo.getItems().clear();
            choicePuntoPrelievo.setItems(FXCollections.observableArrayList(gestoreMagazzini.getItems()));
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    @FXML
    public void updateMagazziniChoiceBox() {
        showMagazzini();
        choicePuntoPrelievo.getItems().clear();
    }

    @FXML
    public void clearText() {
        residenza.clear();
    }
}
