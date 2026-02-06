package dao;

import model.Enseignant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO implements DAO<Enseignant> {

    @Override
    public void create(Enseignant e) throws DaoException {
        // Validation (champs obligatoires)
        if (e == null) throw new DaoException("Enseignant null.");
        String nom = safeTrim(e.getNom());
        String prenom = safeTrim(e.getPrenom());
        if (nom.isEmpty() || prenom.isEmpty()) {
            throw new DaoException("Nom et Prénom sont obligatoires (Enseignant).");
        }

        String sql = "INSERT INTO enseignant(nom, prenom, specialite, email, telephone) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, safeNullIfBlank(e.getSpecialite()));
            ps.setString(4, safeNullIfBlank(e.getEmail()));
            ps.setString(5, safeNullIfBlank(e.getTelephone()));

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw DaoException.wrap("create enseignant", ex);
        }
    }

    @Override
    public Enseignant read(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID enseignant invalide.");
        String sql = "SELECT * FROM enseignant WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("read enseignant", ex);
        }
    }

    @Override
    public void update(Enseignant e) throws DaoException {
        // Validation
        if (e == null) throw new DaoException("Enseignant null.");
        if (e.getId() <= 0) throw new DaoException("ID enseignant invalide (update).");

        String nom = safeTrim(e.getNom());
        String prenom = safeTrim(e.getPrenom());
        if (nom.isEmpty() || prenom.isEmpty()) {
            throw new DaoException("Nom et Prénom sont obligatoires (Enseignant).");
        }

        String sql = """
                UPDATE enseignant
                   SET nom=?, prenom=?, specialite=?, email=?, telephone=?
                 WHERE id=?
                """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, safeNullIfBlank(e.getSpecialite()));
            ps.setString(4, safeNullIfBlank(e.getEmail()));
            ps.setString(5, safeNullIfBlank(e.getTelephone()));
            ps.setInt(6, e.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucun enseignant mis à jour (ID introuvable : " + e.getId() + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("update enseignant", ex);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID enseignant invalide (delete).");

        String sql = "DELETE FROM enseignant WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new DaoException("Aucun enseignant supprimé (ID introuvable : " + id + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("delete enseignant", ex);
        }
    }

    @Override
    public List<Enseignant> findAll() throws DaoException {
        List<Enseignant> list = new ArrayList<>();
        String sql = "SELECT * FROM enseignant ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException ex) {
            throw DaoException.wrap("findAll enseignant", ex);
        }
    }

    private Enseignant map(ResultSet rs) throws SQLException {
        Enseignant e = new Enseignant();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setSpecialite(rs.getString("specialite"));
        e.setEmail(rs.getString("email"));
        e.setTelephone(rs.getString("telephone"));
        return e;
    }

    // ===== Helpers validation =====
    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }

    private String safeNullIfBlank(String s) {
        String t = safeTrim(s);
        return t.isEmpty() ? null : t;
    }
}
