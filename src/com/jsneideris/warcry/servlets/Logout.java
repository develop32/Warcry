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
import com.jsneideris.warcry.stores.RegisterError;
import com.jsneideris.warcry.stores.UserStore;
import com.jsneideris.warcry.stores.UserStore;

@WebServlet(urlPatterns = { "/logout", "/logout/*" })
public class Logout extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;

    public Logout() 
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
		request.setAttribute("user", user);
		
		String auth = request.getParameter("auth");
		
		if (auth != null)
		{
			String userName = model.getUserOfToken(auth);
			if (userName != null)
				model.removeUserTokens(userName);
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("/Logout.jsp"); 
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
}
