package dao;

import model.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO implements DAO<Matiere> {

    @Override
    public void create(Matiere m) throws DaoException {
        if (m == null) throw new DaoException("Matière null.");

        String nom = safeTrim(m.getNom());
        Integer coef = m.getCoefficient();
        Integer vol = m.getVolumeHoraire();

        // ===== Validation métier =====
        if (nom.isEmpty()) {
            throw new DaoException("Le nom de la matière est obligatoire.");
        }
        if (coef != null && coef <= 0) {
            throw new DaoException("Le coefficient doit être un nombre positif.");
        }
        if (vol != null && vol <= 0) {
            throw new DaoException("Le volume horaire doit être un nombre positif.");
        }

        String sql = "INSERT INTO matiere(nom, coefficient, volume_horaire) VALUES(?,?,?)";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            if (coef == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, coef);

            if (vol == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, vol);

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw DaoException.wrap("create matière", ex);
        }
    }

    @Override
    public Matiere read(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID matière invalide.");

        String sql = "SELECT * FROM matiere WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("read matière", ex);
        }
    }

    @Override
    public void update(Matiere m) throws DaoException {
        if (m == null) throw new DaoException("Matière null.");
        if (m.getId() <= 0) throw new DaoException("ID matière invalide (update).");

        String nom = safeTrim(m.getNom());
        Integer coef = m.getCoefficient();
        Integer vol = m.getVolumeHoraire();

        if (nom.isEmpty()) {
            throw new DaoException("Le nom de la matière est obligatoire.");
        }
        if (coef != null && coef <= 0) {
            throw new DaoException("Le coefficient doit être un nombre positif.");
        }
        if (vol != null && vol <= 0) {
            throw new DaoException("Le volume horaire doit être un nombre positif.");
        }

        String sql = "UPDATE matiere SET nom=?, coefficient=?, volume_horaire=? WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            if (coef == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, coef);

            if (vol == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, vol);

            ps.setInt(4, m.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucune matière mise à jour (ID introuvable : " + m.getId() + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("update matière", ex);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID matière invalide (delete).");

        String sql = "DELETE FROM matiere WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new DaoException("Aucune matière supprimée (ID introuvable : " + id + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("delete matière", ex);
        }
    }

    @Override
    public List<Matiere> findAll() throws DaoException {
        List<Matiere> list = new ArrayList<>();
        String sql = "SELECT * FROM matiere ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException ex) {
            throw DaoException.wrap("findAll matière", ex);
        }
    }

    private Matiere map(ResultSet rs) throws SQLException {
        Matiere m = new Matiere();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setCoefficient((Integer) rs.getObject("coefficient"));
        m.setVolumeHoraire((Integer) rs.getObject("volume_horaire"));
        return m;
    }

    // ===== Helper =====
    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}
