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
import com.jsneideris.warcry.stores.LoginError;
import com.jsneideris.warcry.stores.RegisterError;
import com.jsneideris.warcry.stores.UserStore;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/login", "/login/*" })
public class Login extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Login() 
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
		
		RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		MessageModel model = new MessageModel();
		model.setCluster(cluster);
		
		String name = (String)request.getParameter("name");
		String password = (String)request.getParameter("password");
		
		UserStore user = new UserStore();
		user.setName(name);
		
		if (name == null || name.isEmpty())
			user.setLoginError(LoginError.noUsername);
		else if (password == null || password.isEmpty())
			user.setLoginError(LoginError.noPassword);
		else
		{
			UserStore found = model.getUser(name);
			String encrypted = Encryption.encrypt(password);
			
			if (found != null)
			{
				user = found;
				
				if (user.getPassword().equals(encrypted))
				{
					String token = model.getTokenOfUser(name);
					if (token == null) token = model.buildAuth(name);
					user.setToken(token);
					user.setLoggedIn(true);
				}
				else
					user.setLoginError(LoginError.passwordDoesntMatch);
			}
			else
				user.setLoginError(LoginError.userDoesntExist);
		}
		
		request.setAttribute("user", user);
		
		RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
		rd.forward(request, response);
	}
}