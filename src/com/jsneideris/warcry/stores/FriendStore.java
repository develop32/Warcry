package com.jsneideris.warcry.stores;

public class FriendStore 
{
	private String name;
	private FriendError error;
	
	public FriendError getError()
	{
		return error;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setError(FriendError error)
	{
		this.error = error;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
