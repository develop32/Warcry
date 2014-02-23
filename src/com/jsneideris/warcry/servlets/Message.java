package com.jsneideris.warcry.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.datastax.driver.core.Cluster;
import com.jsneideris.warcry.lib.CassandraHosts;
import com.jsneideris.warcry.lib.Encryption;
import com.jsneideris.warcry.models.MessageModel;
import com.jsneideris.warcry.stores.MessageResult;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.ProfileError;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/message", "/message/*" })
public class Message extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Message() 
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
		
		RequestDispatcher rd = request.getRequestDispatcher("/Message.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
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
		else
		{
			request.setAttribute("user", user);
			RequestDispatcher rd = request.getRequestDispatcher("/Message.jsp"); 
			rd.forward(request, response);
			
			return;
		}
		
		request.setAttribute("user", user);
		
		MessageStore message = new MessageStore();
		
		String text = (String)request.getParameter("text");
		message.setText(text);

		if (text == null || text.isEmpty())
			message.setResult(MessageResult.noMessage);
		else if (text.length() > 140)
			message.setResult(MessageResult.tooLong);
		else
		{
			if (model.createMessage(user.getName(), message.getText()))
				message.setResult(MessageResult.success);
			else
				message.setResult(MessageResult.unknown);
		}
		
		request.setAttribute("message", message);
	
		RequestDispatcher rd = request.getRequestDispatcher("/Message.jsp"); 
		rd.forward(request, response);
	}
}