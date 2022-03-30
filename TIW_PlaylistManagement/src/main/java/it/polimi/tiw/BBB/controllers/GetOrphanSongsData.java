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

import it.polimi.tiw.BBB.DAO.SongDAO;
import it.polimi.tiw.BBB.beans.Song;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


@WebServlet("/GetOrphanSongsData")
public class GetOrphanSongsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;   
    
    public GetOrphanSongsData() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		Integer playlistId = null;
		
		try {
			playlistId = Integer.parseInt(request.getParameter("playlistId"));
			
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlistId value");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		int userId = user.getUserId();
		List<Song> orphanSongs = new ArrayList<Song>();
		SongDAO songDAO = new SongDAO(connection);
		
		try {
			orphanSongs = songDAO.findSongsByUserIdNotInThisPlaylist(userId, playlistId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(orphanSongs);
		
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
