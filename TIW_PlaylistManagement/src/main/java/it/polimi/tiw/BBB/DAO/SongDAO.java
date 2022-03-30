package it.polimi.tiw.BBB.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.BBB.beans.Song;


public class SongDAO {
	
	private Connection connection;
	
	public SongDAO (Connection connection) {
		this.connection = connection;
	}
	
	public void addSong (int userId, String genre, String title, String artist, String imageFile, String songFile, String albumTitle, int publicationYear) throws SQLException {
		
		String query = "INSERT into song (UserID, Genre, Title, Artist, ImageFile, SongFile, AlbumTitle, PublicationYear) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, userId);
			pstatement.setString(2, genre);
			pstatement.setString(3, title);
			pstatement.setString(4, artist);
			pstatement.setString(5, imageFile);
			pstatement.setString(6, songFile);
			pstatement.setString(7, albumTitle);
			pstatement.setInt(8, publicationYear);
			
			pstatement.executeUpdate();
			
		}
		
	}

	public List<Song> findSongsByUserIdNotInThisPlaylist (int userId, int playlistId) throws SQLException {
		
		List<Song> orphanSongs = new ArrayList<Song>();
		
		String query = "SELECT * FROM song WHERE UserID = ? AND song.SongID NOT IN (SELECT songID FROM belonging WHERE PlaylistID = ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, userId);
			pstatement.setInt(2, playlistId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Song song = new Song();
					song.setSongId(result.getInt("SongID"));
					song.setUserId(userId);                        //TODO: Necessario???
					song.setGenre(result.getString("Genre"));
					song.setTitle(result.getString("Title"));
					song.setArtist(result.getString("Artist"));
					song.setImageFile(result.getString("ImageFile"));
					song.setSongFile(result.getString("SongFile"));
					song.setAlbumTitle(result.getString("AlbumTitle"));
					song.setPublicationYear(result.getInt("PublicationYear"));
					
					orphanSongs.add(song);
				}
			}
		}
		return orphanSongs;
		
	}
	
	public Song findSongById (int songId) throws SQLException {
		
		String query = "SELECT * FROM song where SongID = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			Song song = null;
			
			pstatement.setInt(1, songId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					song = new Song();
					song.setSongId(result.getInt("SongID"));
					song.setUserId(result.getInt("UserID"));        //TODO: Necessary???
					song.setGenre(result.getString("Genre"));
					song.setTitle(result.getString("Title"));
					song.setArtist(result.getString("Artist"));
					song.setImageFile(result.getString("ImageFile"));
					song.setSongFile(result.getString("SongFile"));
					song.setAlbumTitle(result.getString("AlbumTitle"));
					song.setPublicationYear(result.getInt("PublicationYear"));
				}
			}
			return song;
		}
	}
	
	public List<Song> findSongsByPlaylistOrderPublYear (int playlistId) throws SQLException{
		
		String query = "SELECT * FROM song JOIN belonging ON song.SongID = belonging.SongID WHERE PlaylistID = ? ORDER BY PublicationYear DESC";
		
		List<Song> groupSong = new ArrayList<Song>();
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, playlistId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Song song = new Song();
					song.setSongId(result.getInt("SongID"));
					song.setUserId(result.getInt("UserID"));                        
					song.setGenre(result.getString("Genre"));
					song.setTitle(result.getString("Title"));
					song.setArtist(result.getString("Artist"));
					song.setImageFile(result.getString("ImageFile"));
					song.setSongFile(result.getString("SongFile"));
					song.setAlbumTitle(result.getString("AlbumTitle"));
					song.setPublicationYear(result.getInt("PublicationYear"));
			
					groupSong.add(song);
				}
			}
		}
		return groupSong;
	}
	
	public List<Song> findSongsByPlaylistOrderPosition (int playlistId) throws SQLException{
		
		String query = "SELECT * FROM song JOIN belonging ON song.SongID = belonging.SongID WHERE PlaylistID = ? ORDER BY Position";
		
		List<Song> groupSong = new ArrayList<Song>();
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			
			pstatement.setInt(1, playlistId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Song song = new Song();
					song.setSongId(result.getInt("SongID"));
					song.setUserId(result.getInt("UserID"));                        
					song.setGenre(result.getString("Genre"));
					song.setTitle(result.getString("Title"));
					song.setArtist(result.getString("Artist"));
					song.setImageFile(result.getString("ImageFile"));
					song.setSongFile(result.getString("SongFile"));
					song.setAlbumTitle(result.getString("AlbumTitle"));
					song.setPublicationYear(result.getInt("PublicationYear"));
			
					groupSong.add(song);
				}
			}
		}
		return groupSong;
	}

	
	
}
	
