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
import com.jsneideris.warcry.lib.Convertors;
import com.jsneideris.warcry.lib.Encryption;
import com.jsneideris.warcry.models.MessageModel;
import com.jsneideris.warcry.stores.DeleteResult;
import com.jsneideris.warcry.stores.DeleteStore;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.RegisterError;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/delete", "/delete/*" })
public class Delete extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Delete() 
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
		
		RequestDispatcher rd = request.getRequestDispatcher("/Delete.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		MessageModel model = new MessageModel();
		model.setCluster(cluster);
		
		String[] strings = Convertors.SplitRequestPath(request);
		
		DeleteStore store = new DeleteStore();
		store.setResult(DeleteResult.unknown);
		
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
			
			request.setAttribute("user", user);
		}
		else
		{
			request.setAttribute("delete", store);
			request.setAttribute("user", user);
			RequestDispatcher rd = request.getRequestDispatcher("/Delete.jsp"); 
			rd.forward(request, response);
			
			return;
		}
		
		if (strings.length > 3)
		{
			String what = strings[2];
			
			if (what.equals("message"))
			{
				String uuid = strings[3];
				MessageStore message = model.getMessage(uuid);
				if (message == null)
					store.setResult(DeleteResult.noMessage);
				else if (!message.getUser().equals(user.getName()))
					store.setResult(DeleteResult.noAuthorization);
				else
				{
					if (model.deleteMesssage(user.getName(), uuid))
						store.setResult(DeleteResult.messageDeleted);
					else
						store.setResult(DeleteResult.unknown);
				}
			}
			else if (what.equals("profile"))
			{
				String name = strings[3];
				UserStore userStore = model.getUser(name);
				if (userStore == null)
					store.setResult(DeleteResult.noUser);
				else if (!userStore.getName().equals(user.getName()))
					store.setResult(DeleteResult.noAuthorization);
				else
				{
					if (model.deleteUser(name))
						store.setResult(DeleteResult.userDeleted);
					else
						store.setResult(DeleteResult.unknown);
				}
			}
			else if (what.equals("friend"))
			{
				String friend = strings[3];
				UserStore friendStore = model.getUser(friend);
				if (friendStore == null)
					store.setResult(DeleteResult.noUser);
				else if (!model.isFriend(user.getName(), friendStore.getName()))
					store.setResult(DeleteResult.noFriend);
				else
				{
					if (model.deleteFriend(user.getName(), friendStore.getName()))
						store.setResult(DeleteResult.friendDeleted);
					else
						store.setResult(DeleteResult.unknown);
				}
			}
		}
		
		request.setAttribute("delete", store);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Delete.jsp"); 
		rd.forward(request, response);
	}
}