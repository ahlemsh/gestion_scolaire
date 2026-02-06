package model;

import java.time.LocalTime;

public class Seance {
    private int id;
    private Integer groupeId;      // FK -> groupe.id
    private Integer matiereId;     // FK -> matiere.id
    private Integer enseignantId;  // FK -> enseignant.id
    private String jour;           // ex : "LUN", "MAR", "MER", "JEU", ...
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;

    public Seance() {}

    public Seance(int id,
                  Integer groupeId,
                  Integer matiereId,
                  Integer enseignantId,
                  String jour,
                  LocalTime heureDebut,
                  LocalTime heureFin,
                  String salle) {
        this.id = id;
        this.groupeId = groupeId;
        this.matiereId = matiereId;
        this.enseignantId = enseignantId;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.salle = salle;
    }

    public Seance(Integer groupeId,
                  Integer matiereId,
                  Integer enseignantId,
                  String jour,
                  LocalTime heureDebut,
                  LocalTime heureFin,
                  String salle) {
        this(0, groupeId, matiereId, enseignantId, jour, heureDebut, heureFin, salle);
    }

    // Getters / Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getGroupeId() {       // <--- utilisé dans SeanceDAO
        return groupeId;
    }
    public void setGroupeId(Integer groupeId) {
        this.groupeId = groupeId;
    }

    public Integer getMatiereId() {      // <--- utilisé dans SeanceDAO
        return matiereId;
    }
    public void setMatiereId(Integer matiereId) {
        this.matiereId = matiereId;
    }

    public Integer getEnseignantId() {   // <--- utilisé dans SeanceDAO
        return enseignantId;
    }
    public void setEnseignantId(Integer enseignantId) {
        this.enseignantId = enseignantId;
    }

    public String getJour() {
        return jour;
    }
    public void setJour(String jour) {
        this.jour = jour;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getSalle() {
        return salle;
    }
    public void setSalle(String salle) {
        this.salle = salle;
    }

    @Override
    public String toString() {
        return jour + " " + heureDebut + "-" + heureFin + " | salle " + salle;
    }
}
