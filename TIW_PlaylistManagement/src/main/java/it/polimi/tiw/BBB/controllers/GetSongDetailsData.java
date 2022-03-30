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

import com.google.gson.Gson;

import it.polimi.tiw.BBB.DAO.SongDAO;
import it.polimi.tiw.BBB.beans.Song;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;




@WebServlet("/GetSongDetailsData")
public class GetSongDetailsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;   

    public GetSongDetailsData() {
        super();
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		boolean badRequest = false;
		boolean wrongUser = false;
		
		HttpSession session = request.getSession();
			
		//get parameters and do controls
		
		Integer songId = null;
		User user = (User) session.getAttribute("user");

		SongDAO songDAO = new SongDAO(connection);
		Song song = new Song();
		
		try {
			songId = Integer.parseInt(request.getParameter("songId"));
			
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect songId value");
			return;
		}
	
		try {
			
			song = songDAO.findSongById(songId);
			if(song.getUserId() != user.getUserId())
				wrongUser = true;
			
			badRequest = songId == null || songId <= 0 || wrongUser;
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid song");
			return;
		}
		
		// Redirect to the Home page and add songs to the parameters
		String json = new Gson().toJson(song);
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
