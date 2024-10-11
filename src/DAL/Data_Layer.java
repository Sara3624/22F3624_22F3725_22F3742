package DAL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	public boolean createFileWithCheck(String fileName, String newContent, Date createdDate) throws SQLException {
        if (isContentExisting(fileName, newContent)) {
		    return false;
		} else {
		    creatingFile(fileName, newContent, createdDate);
		    return true;
		}
    }

	public boolean isContentExisting(String fileName, String newContent) {
	    String query = "SELECT fileContent FROM document WHERE fileName = ?";

	    try (Connection conn = dbConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, fileName);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            String oldContent = rs.getString("fileContent");
	            return oldContent.equals(newContent);
	        }
	        return false;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	private boolean creatingFile(String fileName, String newContent, Date createdDate) {
	    String queryInsert = "INSERT INTO document (fileName, fileContent, createdDate) VALUES (?, ?, ?)";

	    try (Connection conn = dbConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(queryInsert)) {
	        pstmt.setString(1, fileName);
	        pstmt.setString(2, newContent);
	        pstmt.setDate(3, createdDate);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean deleteFileFromDatabase(String selectedFile) {
	    String deleteQuery = "DELETE FROM document WHERE fileName = ?";
	    
	    try (Connection conn = dbConnection.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
	        stmt.setString(1, selectedFile);
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
}
