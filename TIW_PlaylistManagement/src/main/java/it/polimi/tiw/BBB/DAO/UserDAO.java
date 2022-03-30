package it.polimi.tiw.BBB.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import it.polimi.tiw.BBB.beans.User;


public class UserDAO {
	
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		
		String query = "SELECT  UserID, Username FROM user WHERE Username = ? AND Password = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					
					User user = new User();
					
					user.setUserId(result.getInt("UserID"));
					user.setUsername(result.getString("Username"));
					
					return user;
				}
			}
		}
	}

}
