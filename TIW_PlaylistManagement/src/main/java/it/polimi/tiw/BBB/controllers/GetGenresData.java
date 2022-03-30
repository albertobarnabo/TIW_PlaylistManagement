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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.BBB.DAO.GenreDAO;
import it.polimi.tiw.BBB.beans.Genre;
import it.polimi.tiw.BBB.utils.ConnectionHandler;


@WebServlet("/GetGenresData")
public class GetGenresData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private Connection connection = null;
	
    public GetGenresData() {
        super();
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		GenreDAO genreDAO = new GenreDAO(connection);
		List<Genre> genres = new ArrayList<Genre>();
		
		try {
			
			genres = genreDAO.getAllGenres();
			
		} catch (SQLException e) {
			
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover genres");
			
			return;
		}
		
		//Gives the genres to the client in  json format
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(genres);
		
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
