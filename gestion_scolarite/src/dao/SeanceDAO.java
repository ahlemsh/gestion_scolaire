package dao;

import model.Seance;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO implements DAO<Seance> {

    @Override
    public void create(Seance s) throws DaoException {
        if (s == null) throw new DaoException("Séance null.");

        // ===== Validation (champs obligatoires + logique) =====
        validateSeanceForCreateOrUpdate(s, false);

        String sql = """
            INSERT INTO seance(id_groupe, id_matiere, id_enseignant, jour, heure_debut, heure_fin, salle)
            VALUES(?,?,?,?,?,?,?)
            """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // FK obligatoires (on les impose comme NOT NULL côté validation)
            ps.setInt(1, s.getGroupeId());
            ps.setInt(2, s.getMatiereId());
            ps.setInt(3, s.getEnseignantId());

            ps.setString(4, normalizeJour(s.getJour()));
            ps.setTime(5, Time.valueOf(s.getHeureDebut()));
            ps.setTime(6, Time.valueOf(s.getHeureFin()));
            ps.setString(7, safeTrim(s.getSalle()));

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw DaoException.wrap("create séance", ex);
        }
    }

    @Override
    public Seance read(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID séance invalide.");

        String sql = "SELECT * FROM seance WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("read séance", ex);
        }
    }

    @Override
    public void update(Seance s) throws DaoException {
        if (s == null) throw new DaoException("Séance null.");
        if (s.getId() <= 0) throw new DaoException("ID séance invalide (update).");

        validateSeanceForCreateOrUpdate(s, true);

        String sql = """
            UPDATE seance
               SET id_groupe=?, id_matiere=?, id_enseignant=?, jour=?, heure_debut=?, heure_fin=?, salle=?
             WHERE id=?
            """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getGroupeId());
            ps.setInt(2, s.getMatiereId());
            ps.setInt(3, s.getEnseignantId());

            ps.setString(4, normalizeJour(s.getJour()));
            ps.setTime(5, Time.valueOf(s.getHeureDebut()));
            ps.setTime(6, Time.valueOf(s.getHeureFin()));
            ps.setString(7, safeTrim(s.getSalle()));
            ps.setInt(8, s.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucune séance mise à jour (ID introuvable : " + s.getId() + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("update séance", ex);
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        if (id <= 0) throw new DaoException("ID séance invalide (delete).");

        String sql = "DELETE FROM seance WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new DaoException("Aucune séance supprimée (ID introuvable : " + id + ").");
            }

        } catch (SQLException ex) {
            throw DaoException.wrap("delete séance", ex);
        }
    }

    @Override
    public List<Seance> findAll() throws DaoException {
        List<Seance> list = new ArrayList<>();
        String sql = "SELECT * FROM seance ORDER BY id DESC";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException ex) {
            throw DaoException.wrap("findAll séance", ex);
        }
    }

    private Seance map(ResultSet rs) throws SQLException {
        Seance s = new Seance();
        s.setId(rs.getInt("id"));
        s.setGroupeId((Integer) rs.getObject("id_groupe"));
        s.setMatiereId((Integer) rs.getObject("id_matiere"));
        s.setEnseignantId((Integer) rs.getObject("id_enseignant"));
        s.setJour(rs.getString("jour"));

        Time d = rs.getTime("heure_debut");
        Time f = rs.getTime("heure_fin");
        s.setHeureDebut(d == null ? null : d.toLocalTime());
        s.setHeureFin(f == null ? null : f.toLocalTime());

        s.setSalle(rs.getString("salle"));
        return s;
    }

    // ================== VALIDATION ==================

    private void validateSeanceForCreateOrUpdate(Seance s, boolean isUpdate) throws DaoException {
        // FK obligatoires (dans un emploi du temps, normalement oui)
        if (s.getGroupeId() == null || s.getGroupeId() <= 0) {
            throw new DaoException("Le groupe (id_groupe) est obligatoire et doit être > 0.");
        }
        if (s.getMatiereId() == null || s.getMatiereId() <= 0) {
            throw new DaoException("La matière (id_matiere) est obligatoire et doit être > 0.");
        }
        if (s.getEnseignantId() == null || s.getEnseignantId() <= 0) {
            throw new DaoException("L'enseignant (id_enseignant) est obligatoire et doit être > 0.");
        }

        String jour = safeTrim(s.getJour());
        if (jour.isEmpty()) {
            throw new DaoException("Le jour est obligatoire.");
        }
        if (!isJourAllowed(jour)) {
            throw new DaoException("Jour invalide. Valeurs attendues : LUN, MAR, MER, JEU, VEN, SAM.");
        }

        if (s.getHeureDebut() == null) {
            throw new DaoException("L'heure de début est obligatoire.");
        }
        if (s.getHeureFin() == null) {
            throw new DaoException("L'heure de fin est obligatoire.");
        }
        if (!s.getHeureFin().isAfter(s.getHeureDebut())) {
            throw new DaoException("Heure fin doit être strictement après heure début.");
        }

        String salle = safeTrim(s.getSalle());
        if (salle.isEmpty()) {
            throw new DaoException("La salle est obligatoire.");
        }
        // Optionnel : petite limite
        if (salle.length() > 50) {
            throw new DaoException("La salle est trop longue (max 50 caractères).");
        }
    }

    private boolean isJourAllowed(String jour) {
        String j = safeTrim(jour).toUpperCase();
        return j.equals("LUN") || j.equals("MAR") || j.equals("MER")
                || j.equals("JEU") || j.equals("VEN") || j.equals("SAM");
    }

    private String normalizeJour(String jour) {
        return safeTrim(jour).toUpperCase();
    }

    private String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}
