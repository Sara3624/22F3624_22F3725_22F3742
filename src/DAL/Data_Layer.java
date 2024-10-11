package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Data_Layer {
	private DB_Connection dbConnection;

	public Data_Layer() {
		dbConnection = new DB_Connection();
	}

	public boolean saveFile(String fileName, String content) {
		String sql = "INSERT INTO imported_files (file_name, content) VALUES (?, ?)";

		try (Connection conn = dbConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, fileName);
			pstmt.setString(2, content);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
