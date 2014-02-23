package com.jsneideris.warcry.stores;

public class DeleteStore 
{
	private DeleteResult result = DeleteResult.none;

	public DeleteResult getResult()
	{
		return result;
	}
	
	public void setResult(DeleteResult result)
	{
		this.result = result;
	}
}
