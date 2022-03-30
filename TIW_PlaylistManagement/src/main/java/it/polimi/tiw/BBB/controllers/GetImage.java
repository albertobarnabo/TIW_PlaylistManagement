package it.polimi.tiw.BBB.controllers;



import java.io.File;

import java.io.IOException;

import java.nio.file.Files;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;



@WebServlet("/GetImage")

public class GetImage extends HttpServlet {

    private static final long serialVersionUID = 1L;

   
    String folderPath = "";

    

    public void init() throws ServletException{

        folderPath = getServletContext().getInitParameter("outputpath");

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pathInfo = request.getParameter("path");

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
            return;
        }

        String filename = pathInfo.substring(0, folderPath.length() - 1);

        File file = new File(pathInfo); 

        if (!file.exists() || file.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        response.setHeader("Content-Type", getServletContext().getMimeType(filename));

        response.setHeader("Content-Length", String.valueOf(file.length()));

        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

        Files.copy(file.toPath(), response.getOutputStream());

    }
    
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	doGet(request, response);
    	
    }

    
}