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


import it.polimi.tiw.BBB.DAO.PlaylistDAO;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


@WebServlet("/SaveSort")
public class SaveSort extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
   
    public SaveSort() {
        super();
    }
    
    public void init() throws ServletException {   	
		connection = ConnectionHandler.getConnection(getServletContext());		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String plystId = request.getParameter("playlistId");
		String sngs = request.getParameter("songs");
		
		String songs [] = sngs.split(",");
		
		List<Integer> songIds = new ArrayList<Integer>();
		
		for(int i=0; i<songs.length; i++) {
			songIds.add(Integer.parseInt(songs[i]));
		}
		
		Integer playlistId = Integer.parseInt(plystId);
		
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		
		
		for(int i=0; i<songIds.size(); i++) {
			try {
				playlistDAO.changePosition(songIds.get(i), playlistId, i+1);
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
