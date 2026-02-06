package controller;

import dao.GroupeDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Groupe;

import java.util.List;
import java.util.regex.Pattern;

public class GroupeController {

    @FXML private TextField txtId;
    @FXML private TextField txtNom;
    @FXML private TextField txtEffectifMax;

    // ✅ ComboBox niveau
    @FXML private ComboBox<String> cbNiveau;

    @FXML private TableView<Groupe> tableGroupe;
    @FXML private TableColumn<Groupe, Number> colId;
    @FXML private TableColumn<Groupe, String> colNom;
    @FXML private TableColumn<Groupe, Number> colEffectif;
    @FXML private TableColumn<Groupe, String> colNiveau;

    private final GroupeDAO dao = new GroupeDAO();

    private static final Pattern TEXT_PATTERN = Pattern.compile("^[\\p{L}0-9 .'-]{2,}$");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colEffectif.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEffectifMax()));
        colNiveau.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNiveau()));

        cbNiveau.setItems(FXCollections.observableArrayList(
                "L1", "L2", "L3", "M1", "M2", "ING1", "ING2", "ING3"
        ));

        chargerTable();

        tableGroupe.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> remplirFormulaire(newSel)
        );
    }

    private void chargerTable() {
        try {
            List<Groupe> groupes = dao.findAll();
            tableGroupe.setItems(FXCollections.observableArrayList(groupes));
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des groupes", e);
        }
    }

    private void remplirFormulaire(Groupe g) {
        if (g == null) {
            return;
        }
        txtId.setText(String.valueOf(g.getId()));
        txtNom.setText(nullToEmpty(g.getNom()));
        txtEffectifMax.setText(g.getEffectifMax() == null ? "" : g.getEffectifMax().toString());
        cbNiveau.getSelectionModel().select(g.getNiveau());
        if (cbNiveau.getSelectionModel().getSelectedItem() == null && g.getNiveau() != null) {
            cbNiveau.getEditor().setText(g.getNiveau());
        }
    }

    @FXML
    public void handleAjouter() {
        try {
            Groupe g = new Groupe();
            g.setNom(safeTrim(txtNom.getText()));
            g.setEffectifMax(parsePositiveIntOrNull(txtEffectifMax.getText(), "effectif max"));
            g.setNiveau(getNiveau());

            String validation = validateGroupe(g);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            dao.create(g);
            afficherInfo("Groupe ajouté avec succès.");
            chargerTable();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ajout du groupe", e);
        }
    }

    @FXML
    public void handleModifier() {
        Groupe selection = tableGroupe.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un groupe dans le tableau.");
            return;
        }

        try {
            selection.setNom(safeTrim(txtNom.getText()));
            selection.setEffectifMax(parsePositiveIntOrNull(txtEffectifMax.getText(), "effectif max"));
            selection.setNiveau(getNiveau());

            String validation = validateGroupe(selection);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            dao.update(selection);
            afficherInfo("Groupe mis à jour.");
            chargerTable();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la mise à jour du groupe", e);
        }
    }

    private String getNiveau() {
        String selected = cbNiveau.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isBlank()) return selected.trim();
        String typed = cbNiveau.getEditor() != null ? cbNiveau.getEditor().getText() : "";
        typed = safeTrim(typed);
        return typed.isEmpty() ? null : typed;
    }

    private String validateGroupe(Groupe g) {
        if (g.getNom() == null || g.getNom().isBlank()) return "Nom obligatoire.";
        if (!TEXT_PATTERN.matcher(g.getNom()).matches()) return "Nom invalide (min 2 caractères).";

        if (g.getNiveau() == null || g.getNiveau().isBlank()) return "Niveau obligatoire.";
        if (g.getEffectifMax() == null) return "Effectif max obligatoire.";
        if (g.getEffectifMax() <= 0) return "Effectif max doit être > 0.";
        return null;
    }

    @FXML public void handleVider() { viderFormulaire(); }

    @FXML
    public void handleSupprimer() {
        Groupe selection = tableGroupe.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un groupe à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le groupe");
        confirm.setContentText("Voulez-vous vraiment supprimer : " + selection.getNom() + " ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            dao.delete(selection.getId());
            afficherInfo("Groupe supprimé.");
            chargerTable();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la suppression du groupe", e);
        }
    }

    @FXML
    public void handleRafraichir() {
        Groupe selected = tableGroupe.getSelectionModel().getSelectedItem();
        Integer selectedId = (selected == null) ? null : selected.getId();

        chargerTable();

        if (selectedId != null) {
            for (Groupe g : tableGroupe.getItems()) {
                if (g.getId() == selectedId) {
                    tableGroupe.getSelectionModel().select(g);
                    remplirFormulaire(g); // remet l'état DB dans le formulaire
                    break;
                }
            }
        }
    }
    private void viderFormulaire() {
        viderFormulaireSansTable();
        tableGroupe.getSelectionModel().clearSelection();
    }

    private void viderFormulaireSansTable() {
        txtId.clear();
        txtNom.clear();
        txtEffectifMax.clear();
        cbNiveau.getSelectionModel().clearSelection();
        if (cbNiveau.getEditor() != null) cbNiveau.getEditor().clear();
    }

    private Integer parsePositiveIntOrNull(String txt, String label) {
        String t = safeTrim(txt);
        if (t.isEmpty()) return null;
        try {
            int v = Integer.parseInt(t);
            if (v <= 0) {
                afficherInfo("Veuillez saisir un " + label + " > 0.");
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            afficherInfo("Veuillez saisir un nombre valide pour " + label + ".");
            return null;
        }
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
