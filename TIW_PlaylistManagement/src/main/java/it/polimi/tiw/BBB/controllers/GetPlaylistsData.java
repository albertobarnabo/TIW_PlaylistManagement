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
import it.polimi.tiw.BBB.beans.Playlist;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


@WebServlet("/GetPlaylistsData")
public class GetPlaylistsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;   
    
    public GetPlaylistsData() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		List<Playlist> playlists = new ArrayList<Playlist>();
		
		try {
			playlists = playlistDAO.findPlaylistsByUser(user.getUserId());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover playlists");
			return;
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy/MM/dd").create();
		String json = gson.toJson(playlists);
		
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
