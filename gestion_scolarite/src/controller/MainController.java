package controller;

import app.MainApp;
import dao.EtudiantDAO;
import dao.EnseignantDAO;
import dao.MatiereDAO;
import dao.GroupeDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Label lblNbEtudiants;
    @FXML
    private Label lblNbEnseignants;
    @FXML
    private Label lblNbMatieres;
    @FXML
    private Label lblNbGroupes;

    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final EnseignantDAO enseignantDAO = new EnseignantDAO();
    private final MatiereDAO matiereDAO = new MatiereDAO();
    private final GroupeDAO groupeDAO = new GroupeDAO();

    @FXML
    public void initialize() {
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        try {
            lblNbEtudiants.setText(String.valueOf(etudiantDAO.findAll().size()));
        } catch (Exception e) {
            lblNbEtudiants.setText("-");
        }

        try {
            lblNbEnseignants.setText(String.valueOf(enseignantDAO.findAll().size()));
        } catch (Exception e) {
            lblNbEnseignants.setText("-");
        }

        try {
            lblNbMatieres.setText(String.valueOf(matiereDAO.findAll().size()));
        } catch (Exception e) {
            lblNbMatieres.setText("-");
        }

        try {
            lblNbGroupes.setText(String.valueOf(groupeDAO.findAll().size()));
        } catch (Exception e) {
            lblNbGroupes.setText("-");
        }
    }

    // ============ Ouverture des fenêtres de gestion ============

    @FXML
    public void handleEtudiants() {
        ouvrirFenetre("/view/EtudiantView.fxml", "Gestion des Étudiants");
    }

    @FXML
    public void handleEnseignants() {
        ouvrirFenetre("/view/EnseignantView.fxml", "Gestion des Enseignants");
    }

    @FXML
    public void handleMatieres() {
        ouvrirFenetre("/view/MatiereView.fxml", "Gestion des Matières");
    }

    @FXML
    public void handleGroupes() {
        ouvrirFenetre("/view/GroupeView.fxml", "Gestion des Groupes");
    }

    @FXML
    public void handleSeances() {
        ouvrirFenetre("/view/SeanceView.fxml", "Gestion des Emplois du Temps");
    }

    @FXML
    public void handleQuitter() {
        System.exit(0);
    }

    private void ouvrirFenetre(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.setWidth(900);
            stage.setHeight(600);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
