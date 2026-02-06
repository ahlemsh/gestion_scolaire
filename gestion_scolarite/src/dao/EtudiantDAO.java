package dao;

import model.Etudiant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO implements DAO<Etudiant> {

    @Override
    public void create(Etudiant e) throws DaoException {
        // Validation (champs obligatoires)
        if (e == null) throw new DaoException("Etudiant null.");
        String nom = safeTrim(e.getNom());
        String prenom = safeTrim(e.getPrenom());
        if (nom.isEmpty() || prenom.isEmpty()) {
            throw new DaoException("Nom et Prénom sont obligatoires (Etudiant).");
        }

        String sql = """
                INSERT INTO etudiant(nom, prenom, date_naissance, email, telephone, id_groupe)
                VALUES(?,?,?,?,?,?)
                """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);

            if (e.getDateNaissance() == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, Date.valueOf(e.getDateNaissance()));

            ps.setString(4, safeNullIfBlank(e.getEmail()));
            ps.setString(5, safeNullIfBlank(e.getTelephone()));

            Integer gid = e.getGroupeId();
            if (gid == null) {
                ps.setNull(6, Types.INTEGER);
            } else if (gid <= 0) {
                throw new DaoException("ID Groupe invalide (doit être > 0).");
            } else {
                ps.setInt(6, gid);
            }

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw DaoException.wrap("create etudiant", ex);
        }
    }

    @Override
    public Etudiant read(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID étudiant invalide.");
        String sql = "SELECT * FROM etudiant WHERE id = ?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("read etudiant", ex);
        }
    }

    @Override
    public void update(Etudiant e) throws DaoException {
        // Validation
        if (e == null) throw new DaoException("Etudiant null.");
        if (e.getId() <= 0) throw new DaoException("ID étudiant invalide (update).");

        String nom = safeTrim(e.getNom());
        String prenom = safeTrim(e.getPrenom());
        if (nom.isEmpty() || prenom.isEmpty()) {
            throw new DaoException("Nom et Prénom sont obligatoires (Etudiant).");
        }

        String sql = """
                UPDATE etudiant
                   SET nom=?, prenom=?, date_naissance=?, email=?, telephone=?, id_groupe=?
                 WHERE id=?
                """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);

            if (e.getDateNaissance() == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, Date.valueOf(e.getDateNaissance()));

            ps.setString(4, safeNullIfBlank(e.getEmail()));
            ps.setString(5, safeNullIfBlank(e.getTelephone()));

            Integer gid = e.getGroupeId();
            if (gid == null) {
                ps.setNull(6, Types.INTEGER);
            } else if (gid <= 0) {
                throw new DaoException("ID Groupe invalide (doit être > 0).");
            } else {
                ps.setInt(6, gid);
            }

            ps.setInt(7, e.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucun étudiant mis à jour (ID introuvable : " + e.getId() + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("update etudiant", ex);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID étudiant invalide (delete).");

        String sql = "DELETE FROM etudiant WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new DaoException("Aucun étudiant supprimé (ID introuvable : " + id + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("delete etudiant", ex);
        }
    }

    @Override
    public List<Etudiant> findAll() throws DaoException {
        List<Etudiant> list = new ArrayList<>();
        String sql = "SELECT * FROM etudiant ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException ex) {
            throw DaoException.wrap("findAll etudiant", ex);
        }
    }

    private Etudiant map(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));

        Date d = rs.getDate("date_naissance");
        e.setDateNaissance(d == null ? null : d.toLocalDate());

        e.setEmail(rs.getString("email"));
        e.setTelephone(rs.getString("telephone"));

        e.setGroupeId((Integer) rs.getObject("id_groupe"));
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

    public int countByGroupeId(int groupeId) throws DaoException {
        if (groupeId <= 0) throw new DaoException("ID groupe invalide (count).");
        String sql = "SELECT COUNT(*) FROM etudiant WHERE id_groupe=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw DaoException.wrap("countByGroupeId", ex);
        }
    }

}
