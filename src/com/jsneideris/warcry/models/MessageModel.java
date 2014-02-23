package com.jsneideris.warcry.models;

import java.util.LinkedList;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.jsneideris.warcry.lib.*;
import com.jsneideris.warcry.stores.MessageStore;
import com.jsneideris.warcry.stores.UserStore;

public class MessageModel 
{
	Cluster cluster;
	
	public MessageModel()
	{
	}

	public void setCluster(Cluster cluster)
	{
		this.cluster = cluster;
	}
	
	public String getUserOfToken(String token)
	{
		Session session = cluster.connect("keyspace2");
		
		PreparedStatement statement = session.prepare("SELECT * from Authorization WHERE tokenCode='" + token + "' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No users returned");
		} 
		else 
		{
			for (Row row : rs) 
				return row.getString("user");
		}
		
		session.close();
		return null;
	}
	
	public boolean deleteUser(String user)
	{
		Session session = cluster.connect("keyspace2");
		
		String deleteMessage = "DELETE FROM Friends WHERE user='" + user + "';";
		System.out.println("" + deleteMessage);
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		LinkedList<String> friendsOf = getFriendsWith(user);
		
		for (String friendOf : friendsOf)
		{
			deleteMessage = "DELETE FROM Friends WHERE user='" + friendOf + "' AND friend='" + user + "';";
			System.out.println("" + deleteMessage);
			
			try
			{
				SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
				session.execute(cqlQuery);
			}
			catch(Exception et)
			{
				System.out.println("Can't remove " + et);
				session.close();
				return false;
			}
			
		}
		
		deleteMessage = "DELETE FROM Authorization WHERE user='" + user + "';";
		System.out.println("" + deleteMessage);
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		deleteMessage = "DELETE FROM Messages WHERE user='" + user + "';";
		System.out.println("" + deleteMessage);
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		deleteMessage = "DELETE FROM Users WHERE user='" + user + "';";
		System.out.println("" + deleteMessage);
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		session.close();
		return true;
	}
	
	public boolean deleteFriend(String user, String friend)
	{
		String deleteMessage = "DELETE FROM Friends WHERE user='" + user + "' AND friend='" + friend + "';";
		System.out.println("" + deleteMessage);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		session.close();
		return true;
	}
	
	public boolean deleteMesssage(String user, String uuid)
	{
		String deleteMessage = "DELETE FROM Messages WHERE user='" + user + "' AND postTime=" + uuid + ";";
		System.out.println("" + deleteMessage);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(deleteMessage);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
			session.close();
			return false;
		}
		
		session.close();
		return true;
	}
	
	public void removeUserTokens(String user)
	{
		String removeTokens = "DELETE FROM Authorization WHERE user='" + user + "';";
		System.out.println("" + removeTokens);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(removeTokens);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't remove " + et);
		}
		
		session.close();
	}
	
	public String getTokenOfUser(String user)
	{
		Session session = cluster.connect("keyspace2");
		
		PreparedStatement statement = session.prepare("SELECT * from Authorization WHERE user='" + user + "' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No users returned");
		} 
		else 
		{
			for (Row row : rs) 
				return row.getString("tokenCode");
		}
		
		session.close();
		return null;
	}
	
	public String buildAuth(String user)
	{
		String token = Encryption.randomToken();
		
		String buildAuth = "INSERT INTO Authorization (user, tokenCode) VALUES ("
				+ "'" + user + "', "
				+ "'" + token + "');";
		System.out.println("" + buildAuth);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(buildAuth);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't authorize " + et);
		}
		
		session.close();
		return token;
	}
	
	public boolean addFriend(String user, String friend)
	{
		String insertFriend = "INSERT INTO Friends (user, friend) VALUES ("
				+ "'" + user + "', "
				+ "'" + friend + "');";
		System.out.println("" + insertFriend);
		
		Session session = cluster.connect("keyspace2");
		
		boolean ok = false;
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(insertFriend);
			session.execute(cqlQuery);
			ok = true;
		}
		catch(Exception et)
		{
			System.out.println("Can't add friend " + et);
		}
		
		session.close();
		
		return ok;
	}
	
	public boolean isFriend(String user, String friend)
	{
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare("SELECT * from Friends WHERE user='" + user + "' AND friend='" + friend + "' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
			System.out.println("No friends returned");
		else 
			return true;
		
		session.close();
		return false;
	}
	
	public String updatePassword(String user, String encryptedPassword)
	{
		String token = Encryption.randomToken();
		
		String updateProfile = "UPDATE Users SET "
				+ "password='" + encryptedPassword + "' " +
				"WHERE user='" + user + "';";
		System.out.println("" + updateProfile);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(updateProfile);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't update " + et);
		}
		
		session.close();
		return token;
	}
	
	public String updateProfile(String user, String fullName, String email)
	{
		String token = Encryption.randomToken();
		
		String updateProfile = "UPDATE Users SET "
				+ "fullName='" + fullName + "'"
				+ ", email='" + email + "' " +
				"WHERE user='" + user + "';";
		System.out.println("" + updateProfile);
		
		Session session = cluster.connect("keyspace2");
		
		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(updateProfile);
			session.execute(cqlQuery);
		}
		catch(Exception et)
		{
			System.out.println("Can't update " + et);
		}
		
		session.close();
		return token;
	}
	
	public boolean registerUser(String user, String password)
	{
		String insertUser = "INSERT INTO users (user, password) VALUES ("
				+ "'" + user + "', "
				+ "'" + password + "');";
		System.out.println("" + insertUser);
		
		Session session = cluster.connect("keyspace2");
		
		boolean ok = false;

		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(insertUser);
			session.execute(cqlQuery);
			ok = true;
		}
		catch(Exception et)
		{
			System.out.println("Can't create user " + et);
		}
		
		session.close();
		return ok;
	}
	
	public LinkedList<String> getFriendsWith(String user)
	{
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare("SELECT * from Friends WHERE friend='" + user + "' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		LinkedList<String> list = new LinkedList<String>();
		
		if (rs.isExhausted()) 
		{
			System.out.println("No friends returned");
		} 
		else 
		{
			for (Row row : rs)
				list.add(row.getString("user"));
		}
		
		session.close();
		return list;
	}
	
	public UserStore getUser(String user)
	{
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare("SELECT * from Users WHERE user='" + user + "' ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No users returned");
		} 
		else 
		{
			for (Row row : rs) 
			{
				UserStore store = new UserStore();
				store.setName(row.getString("user"));
				store.setFullName(row.getString("fullName"));
				store.setEmail(row.getString("email"));
				store.setPassword(row.getString("password"));
				session.close();
				return store;
			}
		}
		
		session.close();
		return null;
	}
	
	public boolean createMessage(String user, String text)
	{
		String createMessage = "INSERT INTO Messages (user, text, postTime) VALUES ("
				+ "'" + user + "', "
				+ "'" + text + "', "
				+ "now());";
		System.out.println("" + createMessage);
		
		Session session = cluster.connect("keyspace2");
		
		boolean ok = false;

		try
		{
			SimpleStatement cqlQuery = new SimpleStatement(createMessage);
			session.execute(cqlQuery);
			ok = true;
		}
		catch(Exception et)
		{
			System.out.println("Can't create user " + et);
		}
		
		session.close();
		return ok;
	}
	
	public LinkedList<String> getFriends(String user) 
	{
		LinkedList<String> list = new LinkedList<String>();
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare(
				"SELECT * FROM Friends WHERE user='" + user + "';");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No friends returned");
		} 
		else 
		{
			for (Row row : rs)
				list.add(row.getString("friend"));
		}
		
		session.close();
		return list;
	}
	
	public MessageStore getMessage(String uuid) 
	{
		LinkedList<MessageStore> list = new LinkedList<MessageStore>();
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare(
				"SELECT * FROM Messages WHERE postTime=" + uuid + " ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No messages returned");
		} 
		else 
		{
			for (Row row : rs) 
			{
				MessageStore ts = new MessageStore();
				ts.setText(row.getString("text"));
				ts.setUser(row.getString("user"));
				ts.setTimeUUID(row.getUUID("postTime"));
				session.close();
				return ts;
			}
		}
		
		session.close();
		return null;
	}

	public LinkedList<MessageStore> getMessages(String user) 
	{
		LinkedList<MessageStore> list = new LinkedList<MessageStore>();
		Session session = cluster.connect("keyspace2");

		PreparedStatement statement = session.prepare(
				user == null ? "SELECT * from Messages;" : "SELECT * FROM Messages WHERE user='" + user + "' ORDER BY postTime;");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) 
		{
			System.out.println("No messages returned");
		} 
		else 
		{
			for (Row row : rs) 
			{
				MessageStore ts = new MessageStore();
				ts.setText(row.getString("text"));
				ts.setUser(row.getString("user"));
				ts.setTimeUUID(row.getUUID("postTime"));
				list.add(ts);
			}
		}
		
		session.close();
		return list;
	}
}