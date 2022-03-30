package it.polimi.tiw.BBB.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import it.polimi.tiw.BBB.DAO.PlaylistDAO;
import it.polimi.tiw.BBB.DAO.SongDAO;
import it.polimi.tiw.BBB.beans.Playlist;
import it.polimi.tiw.BBB.beans.Song;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public AddSongToPlaylist() {
        super();
    }
    
    public void init() throws ServletException {
    	
		connection = ConnectionHandler.getConnection(getServletContext());
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		HttpSession session = request.getSession();
		
		//get parameters from request
		
		boolean badRequest = false;
		boolean badPlaylist = false;
		boolean badSong = false;
		
		String plystId = request.getParameter("playlistId");
		String sngId = request.getParameter("songId");
		
		//check if both the song and the playlist belong to the correct user. 
		
		User user = (User) session.getAttribute("user");
		
		Integer playlistId = null;
		Integer songId = null;
		Integer userId = user.getUserId();
		SongDAO songDAO = new SongDAO(connection);
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		int position=0;
		
		
		
		
		try {
			playlistId = Integer.parseInt(plystId);
		}
		catch (NumberFormatException e) {
			badPlaylist = true;
		} 
		
		
		try {
			songId = Integer.parseInt(sngId);
		}
		catch (NumberFormatException e) {
			badSong = true;
		} 
		try {			
			Song song = songDAO.findSongById(songId);
			Playlist playlist = playlistDAO.findPlaylistById(playlistId);
			
			if(userId != song.getUserId() || userId != playlist.getUserId())
				badRequest = true;			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect user");
			return;
		}
		else if(badPlaylist) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlistId");
			return;
		}
		else if(badSong) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect songId");
			return;
		}
		
		//Once the checks are over, we can now add the song to the playlist
		
		try {
			if(playlistDAO.hasDefaultOrder(playlistId)==true) {
				playlistDAO.addSongToPlaylist(songId, playlistId);
			}
			else {
				position = playlistDAO.getNumberOfSongs(playlistId) + 1;				
				playlistDAO.addSongToPlaylist(songId, playlistId, position);
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
