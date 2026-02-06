package controller;

import dao.MatiereDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Matiere;

import java.util.List;
import java.util.regex.Pattern;

public class MatiereController {

    @FXML private TextField txtId;
    @FXML private TextField txtNom;
    @FXML private TextField txtCoefficient;
    @FXML private TextField txtVolumeHoraire;

    @FXML private TableView<Matiere> tableMatiere;
    @FXML private TableColumn<Matiere, Number> colId;
    @FXML private TableColumn<Matiere, String> colNom;
    @FXML private TableColumn<Matiere, Number> colCoef;
    @FXML private TableColumn<Matiere, Number> colVol;

    private final MatiereDAO dao = new MatiereDAO();

    private static final Pattern TEXT_PATTERN = Pattern.compile("^[\\p{L}0-9 .'-]{2,}$");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colCoef.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCoefficient()));
        colVol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getVolumeHoraire()));

        chargerTable();

        tableMatiere.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> remplirFormulaire(newSel)
        );
    }

    private void chargerTable() {
        try {
            List<Matiere> matieres = dao.findAll();
            tableMatiere.setItems(FXCollections.observableArrayList(matieres));
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des matières", e);
        }
    }

    private void remplirFormulaire(Matiere m) {
        if (m == null) {
            return;
        }
        txtId.setText(String.valueOf(m.getId()));
        txtNom.setText(nullToEmpty(m.getNom()));
        txtCoefficient.setText(m.getCoefficient() == null ? "" : m.getCoefficient().toString());
        txtVolumeHoraire.setText(m.getVolumeHoraire() == null ? "" : m.getVolumeHoraire().toString());
    }

    @FXML
    public void handleAjouter() {
        try {
            Matiere m = buildFromFormStrict();
            if (m == null) return; // message déjà affiché

            dao.create(m);
            afficherInfo("Matière ajoutée avec succès.");
            chargerTable();
            viderFormulaire();

        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ajout de la matière", e);
        }
    }

    @FXML
    public void handleModifier() {
        Matiere selection = tableMatiere.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner une matière dans le tableau.");
            return;
        }
        try {
            Matiere m = buildFromFormStrict();
            if (m == null) return;

            selection.setNom(m.getNom());
            selection.setCoefficient(m.getCoefficient());
            selection.setVolumeHoraire(m.getVolumeHoraire());

            dao.update(selection);
            afficherInfo("Mise à jour effectuée.");
            chargerTable();

        } catch (Exception e) {
            afficherErreur("Erreur lors de la mise à jour", e);
        }
    }

    private Matiere buildFromFormStrict() {
        String nom = safeTrim(txtNom.getText());
        if (nom.isEmpty()) { afficherInfo("Nom obligatoire."); return null; }
        if (!TEXT_PATTERN.matcher(nom).matches()) { afficherInfo("Nom invalide."); return null; }

        Integer coef = readPositiveIntStrict(txtCoefficient, "Coefficient");
        if (coef == null) return null;

        Integer vol = readPositiveIntStrict(txtVolumeHoraire, "Volume horaire");
        if (vol == null) return null;

        Matiere m = new Matiere();
        m.setNom(nom);
        m.setCoefficient(coef);
        m.setVolumeHoraire(vol);
        return m;
    }

    private Integer readPositiveIntStrict(TextField tf, String label) {
        String s = safeTrim(tf.getText());
        if (s.isEmpty()) {
            afficherInfo(label + " obligatoire.");
            return null;
        }
        if (!s.matches("\\d+")) {
            afficherInfo(label + " doit être un nombre entier positif.");
            return null;
        }
        int v = Integer.parseInt(s);
        if (v <= 0) {
            afficherInfo(label + " doit être > 0.");
            return null;
        }
        return v;
    }

    @FXML public void handleVider() { viderFormulaire(); }

    @FXML
    public void handleSupprimer() {
        Matiere selection = tableMatiere.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner une matière à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la matière");
        confirm.setContentText("Voulez-vous vraiment supprimer : " + selection.getNom() + " ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            dao.delete(selection.getId());
            afficherInfo("Matière supprimée.");
            chargerTable();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la suppression", e);
        }
    }

    @FXML
    public void handleRafraichir() {
        Matiere selected = tableMatiere.getSelectionModel().getSelectedItem();
        Integer selectedId = (selected == null) ? null : selected.getId();

        chargerTable();

        if (selectedId != null) {
            for (Matiere m : tableMatiere.getItems()) {
                if (m.getId() == selectedId) {
                    tableMatiere.getSelectionModel().select(m);
                    remplirFormulaire(m); // remet l'état DB dans le formulaire
                    break;
                }
            }
        }
    }


    private void viderFormulaire() {
        viderFormulaireSansTable();
        tableMatiere.getSelectionModel().clearSelection();
    }

    private void viderFormulaireSansTable() {
        txtId.clear();
        txtNom.clear();
        txtCoefficient.clear();
        txtVolumeHoraire.clear();
    }

    private String safeTrim(String s) { return s == null ? "" : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s; }

    private void afficherInfo(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void afficherErreur(String message, Exception ex) {
        ex.printStackTrace();
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setHeaderText(message);
        a.setContentText(ex.getMessage());
        a.showAndWait();
    }
}
