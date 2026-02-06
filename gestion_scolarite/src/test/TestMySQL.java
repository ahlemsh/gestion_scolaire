package test;

import dao.DatabaseConnection;

import java.sql.*;

public class TestMySQL {

    public static void main(String[] args) {

        System.out.println("=== DEBUT TEST MYSQL ===");

        try (Connection c = DatabaseConnection.getInstance().getConnection()) {

            System.out.println("✅ Connexion MySQL OK");

            // 1) Créer table si elle n’existe pas
            try (Statement st = c.createStatement()) {
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS matiere (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nom VARCHAR(120),
                        coefficient INT,
                        volume_horaire INT
                    )
                """);
            }
            System.out.println("✅ Table 'matiere' OK");

            // 2) Insérer un exemple
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO matiere(nom, coefficient, volume_horaire) VALUES (?, ?, ?)")) {
                ps.setString(1, "Algorithmique");
                ps.setInt(2, 2);
                ps.setInt(3, 30);
                ps.executeUpdate();
            }
            System.out.println("✅ Insert OK");

            // 3) Afficher les matières
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, nom, coefficient, volume_horaire FROM matiere")) {

                System.out.println("📋 Matières :");
                while (rs.next()) {
                    System.out.println(" - " + rs.getInt("id")
                            + " | " + rs.getString("nom")
                            + " | coef: " + rs.getInt("coefficient")
                            + " | heures: " + rs.getInt("volume_horaire"));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL / Connexion DB : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST MYSQL ===");
    }
}
