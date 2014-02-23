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
import com.jsneideris.warcry.models.MessageModel;
import com.jsneideris.warcry.stores.FriendError;
import com.jsneideris.warcry.stores.FriendStore;
import com.jsneideris.warcry.stores.MessageResult;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/friend", "/friend/*" })
public class Friend extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Friend() 
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
		
		RequestDispatcher rd = request.getRequestDispatcher("/Friend.jsp"); 
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
			RequestDispatcher rd = request.getRequestDispatcher("/Friend.jsp"); 
			rd.forward(request, response);
			
			return;
		}
		
		request.setAttribute("user", user);
		
		String friendName = request.getParameter("name");
		UserStore friendUser = model.getUser(friendName);
		
		FriendStore friend = new FriendStore();
		friend.setName(friendName);;
		
		if (friendUser == null)
			friend.setError(FriendError.friendDoesntExist);
		else if (model.addFriend(user.getName(), friend.getName()))
			friend.setError(FriendError.none);
		else
			friend.setError(FriendError.unknown);
		
		request.setAttribute("friend", friend);
	
		RequestDispatcher rd = request.getRequestDispatcher("/Friend.jsp"); 
		rd.forward(request, response);
	}
}
