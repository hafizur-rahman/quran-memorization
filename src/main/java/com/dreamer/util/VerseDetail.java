package com.dreamer.util;

import java.sql.*;

public class VerseDetail {
    public static String getVerseDetails(int chapterId, int verseId, String jdbcUrl) {
        var sql = "SELECT text FROM verses WHERE sura=? and ayah=?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            stmt.setInt(2, verseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("text");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
