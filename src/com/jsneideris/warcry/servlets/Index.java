package com.jsneideris.warcry.servlets;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastax.driver.core.Cluster;
import com.jsneideris.warcry.lib.*;
import com.jsneideris.warcry.models.*;
import com.jsneideris.warcry.stores.*;

@WebServlet(urlPatterns = { "/", "/home", "/home/*" })
public class Index extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Index() 
    {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
		cluster = CassandraHosts.getCluster();
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		MessageModel model = new MessageModel();
		model.setCluster(cluster);
		
		UserStore user = new UserStore();
		
		String auth = request.getParameter("auth");
		
		if (auth != null)
		{
			String userName = model.getUserOfToken(auth);
			
			if (userName != null)
			{
				user = model.getUser(userName);
				user.setToken(auth);
			}
		}
		
		request.setAttribute("user", user);
		
		LinkedList<MessageStore> messages = model.getMessages(null);
		request.setAttribute("messages", messages);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Home.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
}