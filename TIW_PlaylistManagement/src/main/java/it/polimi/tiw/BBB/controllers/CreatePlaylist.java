package it.polimi.tiw.BBB.controllers;

import it.polimi.tiw.BBB.DAO.PlaylistDAO;
import it.polimi.tiw.BBB.beans.Playlist;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;




@SuppressWarnings("deprecation")
@WebServlet("/CreatePlaylist")
@MultipartConfig

public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private Connection connection = null;
	
    public CreatePlaylist() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    private Timestamp getMeToday() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
	}

	private boolean checkDuplicatedPlaylistName (String playlisName, int userId) throws SQLException {
		
		boolean hasDouble = false;
		
		List<Playlist> playlists = new ArrayList<Playlist>();
		
		PlaylistDAO playlistDAO = new PlaylistDAO(this.connection);
		
		playlists = playlistDAO.findPlaylistsByUser(userId);
		
		for(Playlist p : playlists) {
			if (p.getPlaylistName().equals(playlisName)) {
				hasDouble = true;
			}
		}
		
		return hasDouble;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		// Get and parse all parameters from request
		
		boolean isBadRequest = false;
		boolean alreadyExists = false;
		
		Timestamp creationDate = null;
		String playlistName = null;
		
		User user = (User) session.getAttribute("user");
		int userId = user.getUserId();
		
		try {
			
			playlistName = StringEscapeUtils.escapeJava(request.getParameter("playlistName"));
			
			creationDate = this.getMeToday();
			
			alreadyExists = checkDuplicatedPlaylistName(playlistName, userId);
			
			isBadRequest = playlistName == null || playlistName.isEmpty() || alreadyExists;
				
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or already existing name");
			return;
		}
		
		// Create a playlist in DB
		
		
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		
		try {
			playlistDAO.addPlaylist(playlistName, creationDate, userId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to create playlist");
			return;
		}
		
		//Warn the client that the playlist was added correctly
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
