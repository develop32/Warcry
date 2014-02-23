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
import com.jsneideris.warcry.lib.CassandraHosts;
import com.jsneideris.warcry.lib.Convertors;
import com.jsneideris.warcry.lib.Encryption;
import com.jsneideris.warcry.models.MessageModel;
import com.jsneideris.warcry.stores.FriendStore;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.ProfileError;
import com.jsneideris.warcry.stores.RegisterError;
import com.jsneideris.warcry.stores.UserStore;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/profile", "/profile/*" })
public class Profile extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Profile() 
    {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
		cluster = CassandraHosts.getCluster();
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String[] strings = Convertors.SplitRequestPath(request);
		
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
		
		UserStore profileOwner = null;
		if (strings.length > 2) 
			profileOwner = model.getUser(strings[2]);
		else
			profileOwner = user;
		
		request.setAttribute("user", user);
		request.setAttribute("profileOwner", profileOwner);
		
		if (user != profileOwner && profileOwner != null)
			if (model.isFriend(user.getName(), profileOwner.getName()))
			{
				FriendStore friend = new FriendStore();
				friend.setName(profileOwner.getName());
				request.setAttribute("friend", friend);
			}
		
		LinkedList<MessageStore> messages = model.getMessages(profileOwner == null ? null : profileOwner.getName());
		request.setAttribute("messages", messages);
		
		LinkedList<String> friends = model.getFriends(profileOwner.getName());
		request.setAttribute("friends", friends);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Profile.jsp"); 
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
			request.setAttribute("profileOwner", user);
			RequestDispatcher rd = request.getRequestDispatcher("/Profile.jsp"); 
			rd.forward(request, response);
			
			return;
		}
		
		String fullName = (String)request.getParameter("fullName");
		String email = (String)request.getParameter("email");
		String password1 = (String)request.getParameter("password1");
		String password2 = (String)request.getParameter("password2");
		
		if (fullName != null && !fullName.isEmpty())
			user.setFullName(fullName);
		user.setEmail(email);
		
		model.updateProfile(user.getName(), user.getFullName(), user.getEmail());
		
		if ((password1 != null && !password1.isEmpty()) || (password2 != null && !password2.isEmpty()))
		{
			if (!password1.equals(password2))
				user.setProfileError(ProfileError.passwordsDontMatch);
			else
			{
				user.setPassword(Encryption.encrypt(password1));
				model.updatePassword(user.getName(), user.getPassword());
			}
		}
		
		request.setAttribute("user", user);
		request.setAttribute("profileOwner", user);
		
		LinkedList<String> friends = model.getFriends(user.getName());
		request.setAttribute("friends", friends);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Profile.jsp"); 
		rd.forward(request, response);
	}
}