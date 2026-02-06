package model;

public class Groupe {
    private int id;
    private String nom;
    private Integer effectifMax;   // Integer permet null
    private String niveau;

    public Groupe() {}

    public Groupe(int id, String nom, Integer effectifMax, String niveau) {
        this.id = id;
        this.nom = nom;
        this.effectifMax = effectifMax;
        this.niveau = niveau;
    }

    public Groupe(String nom, Integer effectifMax, String niveau) {
        this.nom = nom;
        this.effectifMax = effectifMax;
        this.niveau = niveau;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getEffectifMax() {          // <-- IMPORTANT !
        return effectifMax;
    }
    public void setEffectifMax(Integer effectifMax) {
        this.effectifMax = effectifMax;
    }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    @Override
    public String toString() {
        return nom + " (" + niveau + ")";
    }
}
