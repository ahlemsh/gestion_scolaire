package controller;

import dao.EtudiantDAO;
import dao.GroupeDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.Etudiant;
import model.Groupe;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class EtudiantController {

    @FXML private TextField txtId;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private DatePicker dpNaissance;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;

    // ✅ ComboBox à la place de txtGroupeId
    @FXML private ComboBox<Groupe> cbGroupe;

    @FXML private TableView<Etudiant> tableEtudiant;
    @FXML private TableColumn<Etudiant, Number> colId;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, LocalDate> colNaissance;
    @FXML private TableColumn<Etudiant, String> colEmail;
    @FXML private TableColumn<Etudiant, String> colTel;

    // On affiche un libellé de groupe (plus clair que l'ID)
    @FXML private TableColumn<Etudiant, String> colGroupe;

    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final GroupeDAO groupeDAO = new GroupeDAO();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L} .'-]{2,}$"); // lettres + espaces + . ' -

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getNom())));
        colPrenom.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getPrenom())));
        colNaissance.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDateNaissance()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getEmail())));
        colTel.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getTelephone())));
        applyPhoneFilter(txtTelephone, 8);


        colGroupe.setCellValueFactory(data -> {
            Integer gid = data.getValue().getGroupeId();
            if (gid == null) return new SimpleStringProperty("");
            Groupe g = findGroupeById(gid);
            return new SimpleStringProperty(g != null ? formatGroupe(g) : String.valueOf(gid));
        });

        initComboGroupe();
        chargerTable();

        tableEtudiant.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> remplirFormulaire(newSel)
        );
    }

    private void initComboGroupe() {
        try {
            List<Groupe> groupes = groupeDAO.findAll();
            cbGroupe.setItems(FXCollections.observableArrayList(groupes));

            cbGroupe.setConverter(new StringConverter<>() {
                @Override public String toString(Groupe g) { return g == null ? "" : formatGroupe(g); }
                @Override public Groupe fromString(String s) { return null; }
            });

            cbGroupe.setCellFactory(list -> new ListCell<>() {
                @Override protected void updateItem(Groupe g, boolean empty) {
                    super.updateItem(g, empty);
                    setText(empty || g == null ? "" : formatGroupe(g));
                }
            });

        } catch (Exception e) {
            afficherErreur("Erreur chargement des groupes", e);
        }
    }

    private void applyPhoneFilter(TextField tf, int maxLen) {
        tf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > maxLen) return null;
            if (!newText.matches("\\d*")) return null;
            return change;
        }));
    }

    private String formatGroupe(Groupe g) {
        String nom = (g.getNom() == null) ? "" : g.getNom();
        String niv = (g.getNiveau() == null) ? "" : (" | " + g.getNiveau());
        return nom + niv;
    }


    private Groupe findGroupeById(int id) {
        if (cbGroupe.getItems() == null) return null;
        for (Groupe g : cbGroupe.getItems()) {
            if (g.getId() == id) return g;
        }
        return null;
    }

    private void chargerTable() {
        try {
            List<Etudiant> etudiants = etudiantDAO.findAll();
            tableEtudiant.setItems(FXCollections.observableArrayList(etudiants));
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des étudiants", e);
        }
    }

    private void remplirFormulaire(Etudiant e) {
        if (e == null) {
            return;
        }
        txtId.setText(String.valueOf(e.getId()));
        txtNom.setText(nullToEmpty(e.getNom()));
        txtPrenom.setText(nullToEmpty(e.getPrenom()));
        dpNaissance.setValue(e.getDateNaissance());
        txtEmail.setText(nullToEmpty(e.getEmail()));
        txtTelephone.setText(nullToEmpty(e.getTelephone()));

        Integer gid = e.getGroupeId();
        cbGroupe.getSelectionModel().select(gid == null ? null : findGroupeById(gid));
    }

    @FXML
    public void handleAjouter() {
        try {
            Etudiant e = new Etudiant();
            e.setNom(safeTrim(txtNom.getText()));
            e.setPrenom(safeTrim(txtPrenom.getText()));
            e.setDateNaissance(dpNaissance.getValue());
            e.setEmail(safeNullIfBlank(txtEmail.getText()));

            Groupe g = cbGroupe.getSelectionModel().getSelectedItem();
            e.setGroupeId(g == null ? null : g.getId());

            String validation = validateEtudiant(e, true);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            // ✅ Règle métier : ne pas dépasser effectif max
            if (g != null && g.getEffectifMax() != null) {
                int count = etudiantDAO.countByGroupeId(g.getId());
                if (count >= g.getEffectifMax()) {
                    afficherInfo("Impossible : effectif maximum du groupe atteint (" + g.getEffectifMax() + ").");
                    return;
                }
            }

            etudiantDAO.create(e);
            afficherInfo("Étudiant ajouté avec succès.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de l'ajout de l'étudiant", ex);
        }
    }

    @FXML
    public void handleModifier() {
        Etudiant selection = tableEtudiant.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un étudiant.");
            return;
        }

        try {
            selection.setNom(safeTrim(txtNom.getText()));
            selection.setPrenom(safeTrim(txtPrenom.getText()));
            selection.setDateNaissance(dpNaissance.getValue());
            selection.setEmail(safeNullIfBlank(txtEmail.getText()));

            Groupe g = cbGroupe.getSelectionModel().getSelectedItem();
            selection.setGroupeId(g == null ? null : g.getId());

            String validation = validateEtudiant(selection, false);
            if (validation != null) {
                afficherInfo(validation);
                return;
            }

            // ✅ si changement vers un groupe plein
            if (g != null && g.getEffectifMax() != null) {
                int count = etudiantDAO.countByGroupeId(g.getId());
                // si l'étudiant était déjà dans ce groupe, le count inclut déjà cet étudiant -> OK
                // si l'étudiant change vers un autre groupe, on vérifie le max
                if (count >= g.getEffectifMax() && (selection.getGroupeId() == null || selection.getGroupeId() != g.getId())) {
                    afficherInfo("Impossible : effectif maximum du groupe atteint (" + g.getEffectifMax() + ").");
                    return;
                }
            }

            etudiantDAO.update(selection);
            afficherInfo("Étudiant mis à jour.");
            chargerTable();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la mise à jour", ex);
        }
    }

    @FXML public void handleVider() { viderFormulaire(); }

    @FXML
    public void handleSupprimer() {
        Etudiant selection = tableEtudiant.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'étudiant");
        confirm.setContentText("Supprimer : " + selection.getNom() + " " + selection.getPrenom() + " ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            etudiantDAO.delete(selection.getId());
            afficherInfo("Étudiant supprimé.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la suppression", ex);
        }
    }

    @FXML
    public void handleRafraichir() {
        Etudiant selected = tableEtudiant.getSelectionModel().getSelectedItem();
        Integer selectedId = (selected == null) ? null : selected.getId();

        chargerTable();

        if (selectedId != null) {
            for (Etudiant e : tableEtudiant.getItems()) {
                if (e.getId() == selectedId) {
                    tableEtudiant.getSelectionModel().select(e);
                    remplirFormulaire(e); // remet l'état DB dans le formulaire
                    break;
                }
            }
        }
    }

    private void viderFormulaire() {
        viderFormulaireSansTable();
        tableEtudiant.getSelectionModel().clearSelection();
    }

    private void viderFormulaireSansTable() {
        txtId.clear();
        txtNom.clear();
        txtPrenom.clear();
        dpNaissance.setValue(null);
        txtEmail.clear();
        txtTelephone.clear();
        cbGroupe.getSelectionModel().clearSelection();
    }

    // ========= Validation =========

    private String validateEtudiant(Etudiant e, boolean isCreate) {
        if (e.getNom() == null || e.getNom().isBlank()) return "Nom obligatoire.";
        if (e.getPrenom() == null || e.getPrenom().isBlank()) return "Prénom obligatoire.";

        if (!NAME_PATTERN.matcher(e.getNom()).matches()) return "Nom invalide (min 2 caractères, lettres uniquement).";
        if (!NAME_PATTERN.matcher(e.getPrenom()).matches()) return "Prénom invalide (min 2 caractères, lettres uniquement).";

        if (e.getEmail() != null && !EMAIL_PATTERN.matcher(e.getEmail()).matches()) {
            return "Email invalide.";
        }

        if (e.getTelephone() != null) {
            // ici e.getTelephone() = uniquement chiffres
            int len = e.getTelephone().length();
            if (len !=8) return "Téléphone invalide (8 chiffres).";
        }

        if (e.getGroupeId() == null) return "Veuillez choisir un groupe.";

        return null;
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }
    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private String safeNullIfBlank(String s) {
        String t = safeTrim(s);
        return t.isEmpty() ? null : t;
    }

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
