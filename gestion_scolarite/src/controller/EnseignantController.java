package controller;

import dao.EnseignantDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;
import model.Enseignant;

import java.util.List;
import java.util.regex.Pattern;

public class EnseignantController {

    @FXML private TextField txtId;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;

    @FXML private ComboBox<String> cbSpecialite;

    @FXML private TableView<Enseignant> tableEnseignant;
    @FXML private TableColumn<Enseignant, Number> colId;
    @FXML private TableColumn<Enseignant, String> colNom;
    @FXML private TableColumn<Enseignant, String> colPrenom;
    @FXML private TableColumn<Enseignant, String> colEmail;
    @FXML private TableColumn<Enseignant, String> colTel;
    @FXML private TableColumn<Enseignant, String> colSpecialite;

    private final EnseignantDAO dao = new EnseignantDAO();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L} .'-]{2,}$");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getNom())));
        colPrenom.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getPrenom())));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getEmail())));
        colTel.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getTelephone())));
        colSpecialite.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getSpecialite())));

        cbSpecialite.setItems(FXCollections.observableArrayList(
                "Data", "Réseaux", "Linux", "Web", "Open Source", "Programmation système", "Java", "Français", "Anglais", "IoT", "Python", "Cloud", "IA/ML", "Algorithmique", "Mathématiques"
        ));
        cbSpecialite.setEditable(true);

        // ✅ Empêche la saisie de lettres + limite longueur
        applyPhoneFilter(txtTelephone, 8);

        chargerTable();

        tableEnseignant.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> remplirFormulaire(newSel)
        );
    }

    private void applyPhoneFilter(TextField tf, int maxLen) {
        tf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > maxLen) return null;
            if (!newText.matches("\\d*")) return null; // chiffres uniquement
            return change;
        }));
    }

    private void chargerTable() {
        try {
            List<Enseignant> enseignants = dao.findAll();
            tableEnseignant.setItems(FXCollections.observableArrayList(enseignants));
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des enseignants", e);
        }
    }

    private void remplirFormulaire(Enseignant e) {
        if (e == null) {
            return;
        }
        txtId.setText(String.valueOf(e.getId()));
        txtNom.setText(nullToEmpty(e.getNom()));
        txtPrenom.setText(nullToEmpty(e.getPrenom()));
        txtEmail.setText(nullToEmpty(e.getEmail()));
        txtTelephone.setText(nullToEmpty(e.getTelephone()));

        cbSpecialite.getSelectionModel().select(e.getSpecialite());
        if (cbSpecialite.getSelectionModel().getSelectedItem() == null && e.getSpecialite() != null) {
            cbSpecialite.getEditor().setText(e.getSpecialite());
        }
    }

    @FXML
    public void handleAjouter() {
        try {
            Enseignant e = buildFromForm();

            String validation = validateEnseignant(e);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            dao.create(e);
            afficherInfo("Enseignant ajouté avec succès.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de l'ajout de l'enseignant", ex);
        }
    }

    @FXML
    public void handleModifier() {
        Enseignant selection = tableEnseignant.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un enseignant.");
            return;
        }

        try {
            Enseignant e = buildFromForm();
            selection.setNom(e.getNom());
            selection.setPrenom(e.getPrenom());
            selection.setEmail(e.getEmail());
            selection.setTelephone(e.getTelephone());
            selection.setSpecialite(e.getSpecialite());

            String validation = validateEnseignant(selection);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            dao.update(selection);
            afficherInfo("Enseignant mis à jour.");
            chargerTable();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la mise à jour", ex);
        }
    }

    private Enseignant buildFromForm() {
        Enseignant e = new Enseignant();
        e.setNom(safeTrim(txtNom.getText()));
        e.setPrenom(safeTrim(txtPrenom.getText()));
        e.setEmail(safeNullIfBlank(txtEmail.getText()));
        e.setTelephone(safeNullIfBlank(txtTelephone.getText())); // déjà digits-only via formatter
        e.setSpecialite(getSpecialite());
        return e;
    }

    private String getSpecialite() {
        String selected = cbSpecialite.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isBlank()) return selected.trim();
        String typed = cbSpecialite.getEditor() != null ? cbSpecialite.getEditor().getText() : "";
        typed = safeTrim(typed);
        return typed.isEmpty() ? null : typed;
    }

    private String validateEnseignant(Enseignant e) {
        if (e.getNom() == null || e.getNom().isBlank()) return "Nom obligatoire.";
        if (e.getPrenom() == null || e.getPrenom().isBlank()) return "Prénom obligatoire.";
        if (!NAME_PATTERN.matcher(e.getNom()).matches()) return "Nom invalide (min 2 caractères).";
        if (!NAME_PATTERN.matcher(e.getPrenom()).matches()) return "Prénom invalide (min 2 caractères).";

        if (e.getEmail() != null && !EMAIL_PATTERN.matcher(e.getEmail()).matches()) return "Email invalide.";

        if (e.getTelephone() != null) {
            int len = e.getTelephone().length();
            if (len !=8) return "Téléphone invalide (8 chiffres).";
        }

        if (e.getSpecialite() == null || e.getSpecialite().isBlank()) return "Spécialité obligatoire.";
        return null;
    }

    @FXML public void handleVider() { viderFormulaire(); }

    @FXML
    public void handleSupprimer() {
        Enseignant selection = tableEnseignant.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un enseignant à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'enseignant");
        confirm.setContentText("Supprimer : " + selection.getNom() + " " + selection.getPrenom() + " ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            dao.delete(selection.getId());
            afficherInfo("Enseignant supprimé.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la suppression", ex);
        }
    }

    @FXML
    public void handleRafraichir() {
        Enseignant selected = tableEnseignant.getSelectionModel().getSelectedItem();
        Integer selectedId = (selected == null) ? null : selected.getId();

        chargerTable();

        if (selectedId != null) {
            for (Enseignant e : tableEnseignant.getItems()) {
                if (e.getId() == selectedId) {
                    tableEnseignant.getSelectionModel().select(e);
                    remplirFormulaire(e); // remet l'état DB dans le formulaire
                    break;
                }
            }
        }
    }

    private void viderFormulaire() {
        viderFormulaireSansTable();
        tableEnseignant.getSelectionModel().clearSelection();
    }

    private void viderFormulaireSansTable() {
        txtId.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTelephone.clear();
        cbSpecialite.getSelectionModel().clearSelection();
        if (cbSpecialite.getEditor() != null) cbSpecialite.getEditor().clear();
    }

    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private String safeNullIfBlank(String s) {
        String t = safeTrim(s);
        return t.isEmpty() ? null : t;
    }

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
