package it.unicam.cs.ids.C3.TeamMGC.javaFX.corriere;

import it.unicam.cs.ids.C3.TeamMGC.cliente.GestoreClienti;
import it.unicam.cs.ids.C3.TeamMGC.corriere.GestoreCorrieri;
import it.unicam.cs.ids.C3.TeamMGC.javaFX.JavaFXController;
import it.unicam.cs.ids.C3.TeamMGC.negozio.SimpleMerce;
import it.unicam.cs.ids.C3.TeamMGC.ordine.GestoreOrdini;
import it.unicam.cs.ids.C3.TeamMGC.ordine.SimpleMerceOrdine;
import it.unicam.cs.ids.C3.TeamMGC.ordine.SimpleOrdine;
import it.unicam.cs.ids.C3.TeamMGC.ordine.StatoOrdine;
import it.unicam.cs.ids.C3.TeamMGC.puntoPrelievo.GestoreMagazzini;
import it.unicam.cs.ids.C3.TeamMGC.puntoPrelievo.SimplePuntoPrelievo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class JavaFXConsegnareMerceADestinazione implements JavaFXController {

    private final GestoreOrdini gestoreOrdini = GestoreOrdini.getInstance();
    private final GestoreMagazzini gestoreMagazzini = GestoreMagazzini.getInstance();
    private final GestoreCorrieri gestoreCorrieri = GestoreCorrieri.getInstance();
    private final GestoreClienti gestoreClienti = GestoreClienti.getInstance();
    private final ArrayList<ArrayList<String>> merceSelezionata = new ArrayList<>();
    private final int IDCorriere;

    public JavaFXConsegnareMerceADestinazione(int IDCorriere) {
        this.IDCorriere = IDCorriere;
    }

    /**
     * TabPane della finestra
     */
    @FXML
    TabPane tab = new TabPane();

    @FXML
    Tab ricerca = new Tab();

    @FXML
    Tab merce = new Tab();

    /**
     * Tabella della merce.
     */
    @FXML
    TableView<ArrayList<String>> merceTable;

    @FXML
    TableColumn<ArrayList<String>, String> IDMerce;

    @FXML
    TableColumn<ArrayList<String>, String> IDOrdineMerce;

    @FXML
    TableColumn<ArrayList<String>, String> PrezzoMerce;

    @FXML
    TableColumn<ArrayList<String>, String> DescrizioneMerce;

    @FXML
    TableColumn<ArrayList<String>, String> QuantitaMerce;

    @FXML
    ChoiceBox<SimplePuntoPrelievo> magazziniChoiceBox = new ChoiceBox<>();

    private SimpleMerceOrdine selectedMerce;

    /**
     * todo
     */
    @FXML
    public void showMagazzini() {
        try {
            magazziniChoiceBox.getItems().clear();
            magazziniChoiceBox.setItems(FXCollections.observableArrayList(gestoreMagazzini.ricercaMagazziniVicini()));
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    @FXML
    public void updateMagazziniChoiceBox() {
        if (Objects.isNull(magazziniChoiceBox.getValue())) {
            showMagazzini();
        }
    }

    /**
     * Collega i campi della merce alle colonne della tabella.
     */
    private void setMerceCellValueFactory() {
        merceTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        IDMerce.setCellValueFactory(merce -> new SimpleObjectProperty<>(merce.getValue().get(0)));
        IDOrdineMerce.setCellValueFactory(merce -> new SimpleObjectProperty<>(merce.getValue().get(1)));
        PrezzoMerce.setCellValueFactory(merce -> new SimpleObjectProperty<>(merce.getValue().get(2)));
        DescrizioneMerce.setCellValueFactory(merce -> new SimpleObjectProperty<>(merce.getValue().get(3)));
        QuantitaMerce.setCellValueFactory(merce -> new SimpleObjectProperty<>(merce.getValue().get(4)));
    }

    public void confermaConsegna() {
        selezionaMerce();
        try {
            if (selectedMerce != null) {
                if (gestoreOrdini.getOrdine(selectedMerce.getIDOrdine()).getResidenza() == null) {
                    SimpleOrdine ordine = gestoreOrdini.getOrdine(selectedMerce.getIDOrdine());
                    gestoreOrdini.setStatoMerce(selectedMerce.getID(), StatoOrdine.IN_DEPOSITO);
                    gestoreClienti.mandaAlertPuntoPrelievo(ordine.getIDCliente(), gestoreMagazzini.getItem(ordine.getPuntoPrelievo()), selectedMerce);
                    successWindow("Merce consegnata con successo!", "La merce e' stata consegnata al punto di prelievo.");
                } else {
                    gestoreOrdini.setStatoMerce(selectedMerce.getID(), StatoOrdine.RITIRATO);
                    successWindow("Merce consegnata con successo!", "La merce e' stata consegnata al cliente.");
                }
                selectedMerce = null;
                merceTable.getSelectionModel().select(null);
                visualizzaMerci();
            }
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }

    }

    private void selezionaMerce() {
        try {
            if (!merceTable.getSelectionModel().isEmpty()) {
                int ID = Integer.parseInt(merceTable.getSelectionModel().getSelectedItem().get(0));
                if (gestoreOrdini.getMerceOrdine(ID) != null) {
                    this.selectedMerce = gestoreOrdini.getMerceOrdine(ID);
                    merceSelezionata.add(selectedMerce.getDettagli());
                }
            } else
                alertWindow("Impossibile proseguire", "Selezionare la merce consegnata.");
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }

    public void ricercaMagazzini() {
        selezionaMerce();
        if (selectedMerce != null) {
            tab.getSelectionModel().select(ricerca);
            ricerca.setDisable(false);
            merce.setDisable(true);
        } else
            alertWindow("Impossibile proseguire", "Selezionare la merce.");
    }

    public void mandaAlert() {
        try {
            if (magazziniChoiceBox.getValue() != null) {
                SimpleOrdine ordine = gestoreOrdini.getOrdine(selectedMerce.getIDOrdine());
                gestoreClienti.mandaAlertResidenza(ordine.getIDCliente(), magazziniChoiceBox.getValue(), selectedMerce);
                successWindow("Destinazione cambiata con successo!", "La merce dovra' essere consegnata al punto di prelievo.");
                magazziniChoiceBox.getItems().clear();
                backToMerci();
            } else
                alertWindow("Impossibile proseguire", "Selezionare il punto di prelievo.");
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }

    }

    @FXML
    public void backToMerci() {
        if(magazziniChoiceBox.getValue() == null) {
            tab.getSelectionModel().select(merce);
            merce.setDisable(false);
            ricerca.setDisable(true);
        } else
            alertWindow("Impossibile proseguire", "Prima cambiare destinazione.");

    }

    /**
     * Inserisce i dati delle {@link SimpleMerceOrdine merci}  nella tabella.
     */
    @FXML
    public void visualizzaMerci() {
        try {
            setMerceCellValueFactory();
            merceTable.getItems().clear();
            merceTable.getItems().addAll(gestoreOrdini.getDettagliMerciOfCorriere(IDCorriere, StatoOrdine.IN_TRANSITO));
            merceTable.getItems().removeIf(merceSelezionata::contains);
        } catch (SQLException exception) {
            errorWindow("Error!", "Errore nel DB.");
        }
    }
}



