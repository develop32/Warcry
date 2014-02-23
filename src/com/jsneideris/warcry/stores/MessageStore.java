package com.jsneideris.warcry.stores;

import java.util.Date;
import java.util.UUID;

public class MessageStore 
{
    private String text;
    private String user;
    private MessageResult result;
    private UUID time;
    
    private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
    
    public Date getTime()
    {
    	return new Date((this.time.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000);
    }
    
    public UUID getTimeUUID()
    {
    	return time;
    }
    
    public MessageResult getResult()
    {
    	return result;
    }
    
    public String getText()
    {
   	 	return text;
    }
    
    public String getUser()
    {
   	 	return user;
    }
    
    public void setResult(MessageResult result)
    {
    	this.result = result;
    }
    
    public void setText(String text)
    {
   	 	this.text = text;
    }
    
    public void setUser(String user)
    {
    	this.user = user;
    }
    
    public void setTimeUUID(UUID time)
    {
    	this.time = time;
    }
}
