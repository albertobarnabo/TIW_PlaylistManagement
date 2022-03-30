package it.polimi.tiw.BBB.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.BBB.DAO.PlaylistDAO;
import it.polimi.tiw.BBB.DAO.SongDAO;
import it.polimi.tiw.BBB.beans.Playlist;
import it.polimi.tiw.BBB.beans.Song;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;

@WebServlet("/GetSongsData")
public class GetSongsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;   
    
    public GetSongsData() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Integer playlistId = null;
		Playlist playlist = null;
		
		try {
			playlistId = Integer.parseInt(request.getParameter("playlistId"));	
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlistId value");
			return;
		}
		
		SongDAO songDAO = new SongDAO(connection);
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		List<Song> songs = new ArrayList<Song>();
		
		//Check if the selected playlist belongs to the correct user
		try {
			
			playlist = playlistDAO.findPlaylistById(playlistId);
			if (playlist == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "playlist not found");
				return;
			}
			if (playlist.getUserId() != user.getUserId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		//Get the songs of the playlist according to the sorting method
		
		try {
			if(playlistDAO.hasDefaultOrder(playlistId)==true) {
				songs = songDAO.findSongsByPlaylistOrderPublYear(playlistId);
			}
			else {
				songs = songDAO.findSongsByPlaylistOrderPosition(playlistId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Send the client the result as a Json object
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(songs);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
