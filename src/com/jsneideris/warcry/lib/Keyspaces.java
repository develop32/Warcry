package com.jsneideris.warcry.lib;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.*;

public final class Keyspaces 
{
	public Keyspaces()
	{
	}
	
	public static void SetUpKeySpaces(Cluster c)
	{
		try
		{
			Session session = c.connect();
			
			/*
			 * KEYSPACE 
			 */
			
			try
			{
				String createkeyspace = "create keyspace if not exists keyspace2  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
				PreparedStatement statement = session.prepare(createkeyspace);
				BoundStatement boundStatement = new BoundStatement(statement);
				session.execute(boundStatement);
				
			}
			catch(Exception et)
			{
				System.out.println("Can't create keyspace2 "+ et);
			}
		
			session.close();
			session = c.connect("keyspace2");
			
			/*
			 * USER TABLE 
			 */
			
			String createUserTable = "CREATE TABLE if not exists Users (" +
					"user varchar," +
					" fullName varchar," +
					" email varchar," +
					" password varchar," +
					" PRIMARY KEY (user)"+
					");";
			System.out.println("" + createUserTable);
	
			try
			{
				SimpleStatement cqlQuery = new SimpleStatement(createUserTable);
				session.execute(cqlQuery);
			}
			catch(Exception et)
			{
				System.out.println("Can't create user table " + et);
			}
			
			/*
			 * FRIEND TABLE 
			 */
			
			String createFriendTable = "CREATE TABLE if not exists Friends (" +
					"user varchar," +
					" friend varchar," +
					" PRIMARY KEY (user, friend)"+
					");";
			System.out.println("" + createFriendTable);
	
			try
			{
				SimpleStatement cqlQuery = new SimpleStatement(createFriendTable);
				session.execute(cqlQuery);
			}
			catch(Exception et)
			{
				System.out.println("Can't create friend table " + et);
			}
			
			/*
			 * MESSAGE TABLE 
			 */
			
			String createMessageTable = "CREATE TABLE if not exists Messages (" +
					"user varchar," +
					" postTime timeuuid," +
					" text varchar," +
					" PRIMARY KEY (user,postTime)"+
					") WITH CLUSTERING ORDER BY (postTime DESC);";
			System.out.println("" + createMessageTable);
	
			try
			{
				SimpleStatement cqlQuery = new SimpleStatement(createMessageTable);
				session.execute(cqlQuery);
			}
			catch(Exception et)
			{
				System.out.println("Can't create message table " + et);
			}
			
			/*
			 * AUTHORIZATION TABLE 
			 */
			
			String createAuthorizationTable = "CREATE TABLE if not exists Authorization (" +
					"user varchar," +
					" tokenCode varchar," +
					" PRIMARY KEY (user, tokenCode)"+
					");";
			System.out.println("" + createAuthorizationTable);
	
			try
			{
				SimpleStatement cqlQuery = new SimpleStatement(createAuthorizationTable);
				session.execute(cqlQuery);
			}
			catch(Exception et)
			{
				System.out.println("Can't create authorization table " + et);
			}
			
			session.close();
		}
		catch(Exception et)
		{
			System.out.println("Other keyspace or coulm definition error" + et);
		}
		
	}
}