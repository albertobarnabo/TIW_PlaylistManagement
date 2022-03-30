package it.polimi.tiw.BBB.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.BBB.beans.Genre;


public class GenreDAO {
	
	private Connection connection;
	
	public GenreDAO (Connection connection) {
		this.connection = connection;
	}
	
	public List<Genre> getAllGenres () throws SQLException {
		
		List<Genre> genres = new ArrayList<Genre>();
		
		String query = "SELECT * FROM genres";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Genre genre = new Genre(result.getString("Genre"));
					
					genres.add(genre);
				}
			}
		}
		
		return genres;
		
		
	}
	

}
