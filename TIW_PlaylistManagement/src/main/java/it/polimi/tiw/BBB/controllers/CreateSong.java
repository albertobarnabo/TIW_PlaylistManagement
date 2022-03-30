package it.polimi.tiw.BBB.controllers;

import it.polimi.tiw.BBB.DAO.SongDAO;
import it.polimi.tiw.BBB.beans.User;
import it.polimi.tiw.BBB.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;


@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private Connection connection = null;
	private String genericFolderPath = "";
	
    public CreateSong() {
        super();
    }

    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		genericFolderPath = getServletContext().getInitParameter("outputpath");
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		//get image and song file
		
		Part imageFilePart = request.getPart("imageFile");
		Part songFilePart = request.getPart("songFile");
		
		if (imageFilePart == null || imageFilePart.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
			return;
		}
		
		if (songFilePart == null || songFilePart.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
			return;
		}
		
		//We have to check if both file formats are correct
		
		String imageContentType = imageFilePart.getContentType();
		String songContentType = songFilePart.getContentType();
		
		
		if (!imageContentType.startsWith("image")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted");
			return;
		}
		
		if (!songContentType.startsWith("audio")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted");
			return;
		}

		// Get and parse all the other parameters from request
		
		boolean badRequest = false;
		
		String genre = null;
		String songTitle = null;
		String artistName = null;
		String imagePath = null;
		String songFilePath = null;
		String albumTitle = null;
		int publicationYear = 0;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		User user = (User) session.getAttribute("user");
		int userId = user.getUserId();
		
		
		try {
			
			genre = request.getParameter("genre");
			
			songTitle = request.getParameter("songTitle");
			
			artistName = request.getParameter("artistName");
			
			imagePath = genericFolderPath + Paths.get(user.getUsername()).toString() + Paths.get(imageFilePart.getSubmittedFileName()).getFileName().toString();
			
			songFilePath = genericFolderPath + Paths.get(user.getUsername()).toString() + Paths.get(songFilePart.getSubmittedFileName()).getFileName().toString();
			
			albumTitle = request.getParameter("albumTitle");
			
			publicationYear = Integer.parseInt(request.getParameter("publicationYear"));
			
			badRequest  = genre.isEmpty() || genre == null || songTitle.isEmpty() || songTitle == null || artistName.isEmpty() || artistName == null || albumTitle.isEmpty() || albumTitle == null || publicationYear > currentYear || publicationYear<0;  
			
		}catch (NumberFormatException | NullPointerException e) {
			badRequest = true;
			e.printStackTrace();
		}
		
		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters");
			return;
		}
		
		//Now we insert all the song info into the database
		
		SongDAO songDAO = new SongDAO(connection);
		
		try {
			songDAO.addSong(userId, genre, songTitle, artistName, imagePath, songFilePath, albumTitle, publicationYear);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create song");
			return;
		}
		
		// Now we save the song and image file in the right directory
		
		File imageFile = new File(imagePath);
		File songFile = new File(songFilePath);
		
		
		try (InputStream imageFileContent = imageFilePart.getInputStream()) {
			
			Files.copy(imageFileContent, imageFile.toPath());
			System.out.println("File saved correctly!");
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
		}
		
		try (InputStream songFileContent = songFilePart.getInputStream()) {
			
			Files.copy(songFileContent, songFile.toPath());
			System.out.println("File saved correctly!");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
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
