package BL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import DAL.Data_Layer;

public class B_LOGIC {

private Data_Layer DAO;

	public B_LOGIC(Data_Layer DAO) {
		this.DAO = DAO;
	}

	public String importTextFile(File file) throws IOException {
		if (!file.getName().endsWith(".txt")) {
			return "Invalid file type. Only text files (.txt) are allowed.";
		}

		String content = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
		return content;
	}

	public String saveImportedFileToDatabase(String content, String fileName) throws NoSuchAlgorithmException {
		boolean isSaved = DAO.saveFile(fileName, content);
		return isSaved ? "File content saved to database successfully." : "Failed to save file content to database.";
	}
	
	public boolean createfile(String filename, String content) throws SQLException {
        Date date = Date.valueOf(LocalDate.now());
        return DAO.createFileWithCheck(filename, content, date);
    }
	
	 public boolean deleteFile(String fileName) {
	    	boolean res = DAO.deleteFileFromDatabase(fileName);
	    	if(res == true){
	    		return true;
	    	}else {
	    		return false;
	    	}
	    }
}