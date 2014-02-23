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
import com.jsneideris.warcry.stores.RegisterError;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.UserStore;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/register", "/register/*" })
public class Register extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Register() 
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
		
		RequestDispatcher rd = request.getRequestDispatcher("/Register.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		MessageModel model = new MessageModel();
		model.setCluster(cluster);
		
		String name = (String)request.getParameter("name");
		String password1 = (String)request.getParameter("password1");
		String password2 = (String)request.getParameter("password2");
		
		UserStore user = new UserStore();
		user.setName(name);
		
		if (name == null || name.isEmpty())
			user.setRegisterError(RegisterError.noUsername);
		else if (password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty())
			user.setRegisterError(RegisterError.noPassword);
		else if (!password1.equals(password2))
			user.setRegisterError(RegisterError.passwordsDontMatch);
		else
		{
			UserStore found = model.getUser(name);
			
			if (found == null)
			{
				String encrypted = Encryption.encrypt(password1);
				
				if (model.registerUser(name, encrypted))
				{
					String token = model.buildAuth(name);
					user.setToken(token);
				}
				else
					user.setRegisterError(RegisterError.unknown);
			}
			else
				user.setRegisterError(RegisterError.userExists);
		}
		
		request.setAttribute("user", user);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Register.jsp"); 
		rd.forward(request, response);
	}
}
