package model;

public class Matiere {
    private int id;
    private String nom;
    private Integer coefficient;      // <-- doit être Integer
    private Integer volumeHoraire;    // <-- doit être Integer

    public Matiere() {}

    public Matiere(int id, String nom, Integer coefficient, Integer volumeHoraire) {
        this.id = id;
        this.nom = nom;
        this.coefficient = coefficient;
        this.volumeHoraire = volumeHoraire;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getCoefficient() { return coefficient; }      // <-- IMPORTANT
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }

    public Integer getVolumeHoraire() { return volumeHoraire; }  // <-- IMPORTANT
    public void setVolumeHoraire(Integer volumeHoraire) { this.volumeHoraire = volumeHoraire; }

    @Override
    public String toString() {
        return nom + " (coef: " + coefficient + ")";
    }
}
