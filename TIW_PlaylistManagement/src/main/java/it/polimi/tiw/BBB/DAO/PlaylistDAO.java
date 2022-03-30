package it.polimi.tiw.BBB.DAO;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.BBB.beans.Playlist;



public class PlaylistDAO {

	private Connection connection;
	
	public PlaylistDAO (Connection connection) {
		this.connection = connection;
	}
	
	public void addPlaylist (String playlistName, Timestamp date, int userId) throws SQLException{
		
		String query = "INSERT into playlist (PlaylistName, CreationDate, UserID)   VALUES(?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setString(1, playlistName);
			pstatement.setTimestamp(2, date);
			pstatement.setInt(3, userId);
			
			pstatement.executeUpdate();
		}
	}
	
	public List<Playlist> findPlaylistsByUser(int userId) throws SQLException {
		
		List<Playlist> userPlaylists = new ArrayList<Playlist>();
		
		String query = "SELECT * FROM playlist WHERE UserID = ? ORDER BY CreationDate DESC";
				
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, userId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Playlist playlist = new Playlist();
					playlist.setPlaylistId(result.getInt("PlaylistID"));
					playlist.setPlaylistName(result.getString("PlaylistName"));
					playlist.setCreationDate(result.getDate("CreationDate"));
					playlist.setUserId(userId); //TODO:Ricontrollare se veramente necessario avere questo metodo
					
					userPlaylists.add(playlist);
				}
			}
		}
		return userPlaylists;
		
	}
	
	
	public void addSongToPlaylist(int songId, int playlistId) throws SQLException {
		
		String query ="INSERT into belonging (SongID, PlaylistID) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, songId);
			pstatement.setInt(2, playlistId);
			
			pstatement.executeUpdate();
		}
	}
	
	public void addSongToPlaylist(int songId, int playlistId, int position) throws SQLException {
		
		String query ="INSERT into belonging (SongID, PlaylistID, Position) VALUES(?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, songId);
			pstatement.setInt(2, playlistId);
			pstatement.setInt(3, position);
			
			pstatement.executeUpdate();
		}
	}
	
	public Playlist findPlaylistById (int playlistId) throws SQLException {
		
		Playlist playlist = null;

		String query = "SELECT * FROM playlist WHERE PlaylistID = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, playlistId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					playlist = new Playlist();
					playlist.setPlaylistId(playlistId);
					playlist.setUserId(result.getInt("UserID"));
					playlist.setPlaylistName(result.getString("PlaylistName"));
					playlist.setCreationDate(result.getDate("CreationDate"));
					}
			}
		}
		return playlist;
	}
	
	public int getNumberOfSongs(int playlistId) throws SQLException {
		
		int n = 0;
		
		String query = "SELECT * FROM belonging WHERE PlaylistID = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, playlistId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next())
					n++;
				
				return n;			
			}
		}
	}
	
	public void changePosition(int songId, int playlistId, int position) throws SQLException {
		
		String query = "UPDATE belonging SET Position = ? WHERE SongID = ? AND PlaylistID = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, position);
			pstatement.setInt(2, songId);
			pstatement.setInt(3, playlistId);
			
			pstatement.executeUpdate();
			return;
		}
	}
	
	public boolean hasDefaultOrder (int playlistId) throws SQLException {
		
		String query = "SELECT Position FROM belonging WHERE PlaylistID = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, playlistId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				
				while (result.next()) {
					Integer position;
					position = result.getInt("Position");
					if(position != 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
}

