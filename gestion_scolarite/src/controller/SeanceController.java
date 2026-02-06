package controller;

import dao.EnseignantDAO;
import dao.GroupeDAO;
import dao.MatiereDAO;
import dao.SeanceDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import model.Enseignant;
import model.Groupe;
import model.Matiere;
import model.Seance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeanceController {

    @FXML private TextField txtId;

    @FXML private ComboBox<Groupe> cbGroupe;
    @FXML private ComboBox<Matiere> cbMatiere;
    @FXML private ComboBox<Enseignant> cbEnseignant;

    @FXML private ComboBox<String> cbJour;

    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;

    // ✅ saisie رقم فقط
    @FXML private TextField txtSalle;

    @FXML private TableView<Seance> tableSeance;
    @FXML private TableColumn<Seance, Number> colId;

    // ✅ maintenant on affiche des NOMS donc String
    @FXML private TableColumn<Seance, String> colGroupeId;
    @FXML private TableColumn<Seance, String> colMatiereId;
    @FXML private TableColumn<Seance, String> colEnseignantId;

    @FXML private TableColumn<Seance, String> colJour;
    @FXML private TableColumn<Seance, LocalTime> colHeureDebut;
    @FXML private TableColumn<Seance, LocalTime> colHeureFin;
    @FXML private TableColumn<Seance, String> colSalle;

    private final SeanceDAO seanceDAO = new SeanceDAO();
    private final GroupeDAO groupeDAO = new GroupeDAO();
    private final MatiereDAO matiereDAO = new MatiereDAO();
    private final EnseignantDAO enseignantDAO = new EnseignantDAO();

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    // ✅ caches pour afficher rapidement les noms dans la table
    private final Map<Integer, Groupe> groupeById = new HashMap<>();
    private final Map<Integer, Matiere> matiereById = new HashMap<>();
    private final Map<Integer, Enseignant> enseignantById = new HashMap<>();

    @FXML
    public void initialize() {

        // ✅ salle : digits-only + max len
        applyDigitsOnlyFilter(txtSalle, 5);

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));

        // ✅ Affichage nom groupe/matiere/enseignant au lieu ID
        colGroupeId.setCellValueFactory(data -> {
            Integer gid = data.getValue().getGroupeId();
            Groupe g = (gid == null) ? null : groupeById.get(gid);
            return new SimpleStringProperty(g == null ? "" : formatGroupe(g));
        });

        colMatiereId.setCellValueFactory(data -> {
            Integer mid = data.getValue().getMatiereId();
            Matiere m = (mid == null) ? null : matiereById.get(mid);
            return new SimpleStringProperty(m == null ? "" : nullToEmpty(m.getNom()));
        });

        colEnseignantId.setCellValueFactory(data -> {
            Integer eid = data.getValue().getEnseignantId();
            Enseignant en = (eid == null) ? null : enseignantById.get(eid);
            if (en == null) return new SimpleStringProperty("");
            String full = (nullToEmpty(en.getNom()) + " " + nullToEmpty(en.getPrenom())).trim();
            return new SimpleStringProperty(full);
        });

        colJour.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getJour())));
        colHeureDebut.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHeureDebut()));
        colHeureFin.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHeureFin()));
        colSalle.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getSalle())));

        // ✅ IMPORTANT: on charge d'abord les combos (et caches), puis la table
        initCombos();
        chargerTable();

        tableSeance.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> remplirFormulaire(newSel)
        );
    }

    private void applyDigitsOnlyFilter(TextField tf, int maxLen) {
        tf.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > maxLen) return null;
            if (!newText.matches("\\d*")) return null;
            return change;
        }));
    }

    private void initCombos() {
        try {
            List<Groupe> groupes = groupeDAO.findAll();
            groupeById.clear();
            for (Groupe g : groupes) groupeById.put(g.getId(), g);

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

            List<Matiere> matieres = matiereDAO.findAll();
            matiereById.clear();
            for (Matiere m : matieres) matiereById.put(m.getId(), m);

            cbMatiere.setItems(FXCollections.observableArrayList(matieres));
            cbMatiere.setConverter(new StringConverter<>() {
                @Override public String toString(Matiere m) { return m == null ? "" : nullToEmpty(m.getNom()); }
                @Override public Matiere fromString(String s) { return null; }
            });
            cbMatiere.setCellFactory(list -> new ListCell<>() {
                @Override protected void updateItem(Matiere m, boolean empty) {
                    super.updateItem(m, empty);
                    setText(empty || m == null ? "" : nullToEmpty(m.getNom()));
                }
            });

            List<Enseignant> enseignants = enseignantDAO.findAll();
            enseignantById.clear();
            for (Enseignant e : enseignants) enseignantById.put(e.getId(), e);

            cbEnseignant.setItems(FXCollections.observableArrayList(enseignants));
            cbEnseignant.setConverter(new StringConverter<>() {
                @Override public String toString(Enseignant en) {
                    if (en == null) return "";
                    return (nullToEmpty(en.getNom()) + " " + nullToEmpty(en.getPrenom())).trim();
                }
                @Override public Enseignant fromString(String s) { return null; }
            });
            cbEnseignant.setCellFactory(list -> new ListCell<>() {
                @Override protected void updateItem(Enseignant en, boolean empty) {
                    super.updateItem(en, empty);
                    if (empty || en == null) setText("");
                    else setText((nullToEmpty(en.getNom()) + " " + nullToEmpty(en.getPrenom())).trim());
                }
            });

            cbJour.setItems(FXCollections.observableArrayList("LUN", "MAR", "MER", "JEU", "VEN", "SAM"));
        } catch (Exception e) {
            afficherErreur("Erreur chargement listes (groupe/matière/enseignant)", e);
        }
    }

    private void chargerTable() {
        try {
            tableSeance.setItems(FXCollections.observableArrayList(seanceDAO.findAll()));
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des séances", e);
        }
    }

    private void remplirFormulaire(Seance s) {
        if (s == null) {
            return;
        }
        txtId.setText(String.valueOf(s.getId()));

        cbGroupe.getSelectionModel().select((s.getGroupeId() == null) ? null : groupeById.get(s.getGroupeId()));
        cbMatiere.getSelectionModel().select((s.getMatiereId() == null) ? null : matiereById.get(s.getMatiereId()));
        cbEnseignant.getSelectionModel().select((s.getEnseignantId() == null) ? null : enseignantById.get(s.getEnseignantId()));
        cbJour.getSelectionModel().select(s.getJour());

        txtHeureDebut.setText(s.getHeureDebut() == null ? "" : s.getHeureDebut().format(HHMM));
        txtHeureFin.setText(s.getHeureFin() == null ? "" : s.getHeureFin().format(HHMM));

        // DB: "Salle 12" -> input: "12"
        String salle = s.getSalle() == null ? "" : s.getSalle().trim();
        if (salle.toLowerCase().startsWith("salle")) {
            salle = salle.replaceFirst("(?i)salle\\s*", "");
        }
        txtSalle.setText(salle);
    }

    @FXML
    public void handleAjouter() {
        try {
            Seance s = buildFromFormStrict();
            if (s == null) return;

            seanceDAO.create(s);
            afficherInfo("Séance ajoutée.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de l'ajout de la séance", ex);
        }
    }

    @FXML
    public void handleModifier() {
        Seance selection = tableSeance.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner une séance.");
            return;
        }

        try {
            Seance s = buildFromFormStrict();
            if (s == null) return;

            selection.setGroupeId(s.getGroupeId());
            selection.setMatiereId(s.getMatiereId());
            selection.setEnseignantId(s.getEnseignantId());
            selection.setJour(s.getJour());
            selection.setHeureDebut(s.getHeureDebut());
            selection.setHeureFin(s.getHeureFin());
            selection.setSalle(s.getSalle());

            seanceDAO.update(selection);
            afficherInfo("Séance mise à jour.");
            chargerTable();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la mise à jour de la séance", ex);
        }
    }

    private Seance buildFromFormStrict() {
        Groupe g = cbGroupe.getSelectionModel().getSelectedItem();
        if (g == null) { afficherInfo("Veuillez choisir un groupe."); return null; }

        Matiere m = cbMatiere.getSelectionModel().getSelectedItem();
        if (m == null) { afficherInfo("Veuillez choisir une matière."); return null; }

        Enseignant en = cbEnseignant.getSelectionModel().getSelectedItem();
        if (en == null) { afficherInfo("Veuillez choisir un enseignant."); return null; }

        String jour = cbJour.getSelectionModel().getSelectedItem();
        if (jour == null || jour.isBlank()) { afficherInfo("Veuillez choisir un jour."); return null; }

        LocalTime hd = parseHHmmOrNull(txtHeureDebut.getText());
        if (hd == null) { afficherInfo("Heure début obligatoire (format HH:mm)."); return null; }

        LocalTime hf = parseHHmmOrNull(txtHeureFin.getText());
        if (hf == null) { afficherInfo("Heure fin obligatoire (format HH:mm)."); return null; }

        if (!hd.isBefore(hf)) { afficherInfo("Heure début doit être < heure fin."); return null; }

        Integer salleNum = readPositiveIntStrict(txtSalle, "Salle");
        if (salleNum == null) return null;

        Seance s = new Seance();
        s.setGroupeId(g.getId());
        s.setMatiereId(m.getId());
        s.setEnseignantId(en.getId());
        s.setJour(jour);
        s.setHeureDebut(hd);
        s.setHeureFin(hf);
        s.setSalle("Salle " + salleNum);
        return s;
    }

    private LocalTime parseHHmmOrNull(String txt) {
        String t = safeTrim(txt);
        if (t.isEmpty()) return null;
        try {
            return LocalTime.parse(t, HHMM); // strict HH:mm
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer readPositiveIntStrict(TextField tf, String label) {
        String s = safeTrim(tf.getText());
        if (s.isEmpty()) {
            afficherInfo(label + " obligatoire (numéro).");
            return null;
        }
        if (!s.matches("\\d+")) {
            afficherInfo(label + " doit être un nombre entier positif (ex: 12).");
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
        Seance selection = tableSeance.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Veuillez sélectionner une séance à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la séance");
        confirm.setContentText("Supprimer la séance ID : " + selection.getId() + " ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            seanceDAO.delete(selection.getId());
            afficherInfo("Séance supprimée.");
            chargerTable();
            viderFormulaire();
        } catch (Exception ex) {
            afficherErreur("Erreur lors de la suppression de la séance", ex);
        }
    }

    @FXML
    public void handleRafraichir() {
        Seance selected = tableSeance.getSelectionModel().getSelectedItem();
        Integer selectedId = (selected == null) ? null : selected.getId();

        // si tu ajoutes/modifies groupe/matiere/enseignant dans une autre fenêtre
        initCombos();
        chargerTable();

        if (selectedId != null) {
            for (Seance s : tableSeance.getItems()) {
                if (s.getId() == selectedId) {
                    tableSeance.getSelectionModel().select(s);
                    remplirFormulaire(s); // remet l'état BD dans le formulaire
                    break;
                }
            }
        }
    }

    private void viderFormulaire() {
        viderFormulaireSansTable();
        tableSeance.getSelectionModel().clearSelection();
    }

    private void viderFormulaireSansTable() {
        txtId.clear();
        cbGroupe.getSelectionModel().clearSelection();
        cbMatiere.getSelectionModel().clearSelection();
        cbEnseignant.getSelectionModel().clearSelection();
        cbJour.getSelectionModel().clearSelection();
        txtHeureDebut.clear();
        txtHeureFin.clear();
        txtSalle.clear();
    }

    private String formatGroupe(Groupe g) {
        String nom = g.getNom() == null ? "" : g.getNom();
        String niv = g.getNiveau() == null ? "" : (" | " + g.getNiveau());
        return (nom + niv).trim(); // ✅ sans ID
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
