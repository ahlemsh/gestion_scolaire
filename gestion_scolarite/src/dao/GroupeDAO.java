package dao;

import model.Groupe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupeDAO implements DAO<Groupe> {

    @Override
    public void create(Groupe g) throws DaoException {
        if (g == null) throw new DaoException("Groupe null.");

        String nom = safeTrim(g.getNom());
        String niveau = safeTrim(g.getNiveau());

        // Validation champs obligatoires
        if (nom.isEmpty()) {
            throw new DaoException("Le nom du groupe est obligatoire.");
        }
        if (niveau.isEmpty()) {
            throw new DaoException("Le niveau du groupe est obligatoire.");
        }

        Integer eff = g.getEffectifMax();
        if (eff != null && eff <= 0) {
            throw new DaoException("L'effectif max doit être un nombre positif.");
        }

        String sql = "INSERT INTO groupe(nom, effectif_max, niveau) VALUES(?,?,?)";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            if (eff == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, eff);

            ps.setString(3, niveau);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw DaoException.wrap("create groupe", ex);
        }
    }

    @Override
    public Groupe read(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID groupe invalide.");

        String sql = "SELECT * FROM groupe WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("read groupe", ex);
        }
    }

    @Override
    public void update(Groupe g) throws DaoException {
        if (g == null) throw new DaoException("Groupe null.");
        if (g.getId() <= 0) throw new DaoException("ID groupe invalide (update).");

        String nom = safeTrim(g.getNom());
        String niveau = safeTrim(g.getNiveau());

        if (nom.isEmpty()) {
            throw new DaoException("Le nom du groupe est obligatoire.");
        }
        if (niveau.isEmpty()) {
            throw new DaoException("Le niveau du groupe est obligatoire.");
        }

        Integer eff = g.getEffectifMax();
        if (eff != null && eff <= 0) {
            throw new DaoException("L'effectif max doit être un nombre positif.");
        }

        String sql = "UPDATE groupe SET nom=?, effectif_max=?, niveau=? WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            if (eff == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, eff);

            ps.setString(3, niveau);
            ps.setInt(4, g.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucun groupe mis à jour (ID introuvable : " + g.getId() + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("update groupe", ex);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID groupe invalide (delete).");

        String sql = "DELETE FROM groupe WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new DaoException("Aucun groupe supprimé (ID introuvable : " + id + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("delete groupe", ex);
        }
    }

    @Override
    public List<Groupe> findAll() throws DaoException {
        List<Groupe> list = new ArrayList<>();
        String sql = "SELECT * FROM groupe ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException ex) {
            throw DaoException.wrap("findAll groupe", ex);
        }
    }

    private Groupe map(ResultSet rs) throws SQLException {
        Groupe g = new Groupe();
        g.setId(rs.getInt("id"));
        g.setNom(rs.getString("nom"));
        g.setEffectifMax((Integer) rs.getObject("effectif_max"));
        g.setNiveau(rs.getString("niveau"));
        return g;
    }

    // ===== Helpers =====
    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}
