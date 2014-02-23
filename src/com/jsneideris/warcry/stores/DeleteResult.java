package com.jsneideris.warcry.stores;

public enum DeleteResult 
{
	none,
	messageDeleted,
	friendDeleted,
	userDeleted,
	noMessage,
	noFriend,
	noUser,
	noAuthorization,
	unknown
}
