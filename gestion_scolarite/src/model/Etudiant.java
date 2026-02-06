package model;

import java.time.LocalDate;

public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String email;
    private String telephone;
    private Integer groupeId;

    public Etudiant() {}

    public Etudiant(int id, String nom, String prenom, LocalDate dateNaissance,
                    String email, String telephone, Integer groupeId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.telephone = telephone;
        this.groupeId = groupeId;
    }

    public Etudiant(String nom, String prenom, LocalDate dateNaissance,
                    String email, String telephone, Integer groupeId) {
        this(0, nom, prenom, dateNaissance, email, telephone, groupeId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Integer getGroupeId() { return groupeId; }
    public void setGroupeId(Integer groupeId) { this.groupeId = groupeId; }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}
