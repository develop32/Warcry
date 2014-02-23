<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.jsneideris.warcry.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Warcry - Profile</title>
</head>
<body>

<h1 class="title"><a href="/Warcry/home?auth=${user.token}">Warcry<a/></h1>
by Justinas Sneideris

<%
UserStore user = (UserStore)request.getAttribute("user");
String userName = "";

if (user != null && user.getLoggedIn())
{
	userName = user.getName();
	%>
	<h4><a href="/Warcry/profile?auth=${user.token}">${user.fullName}</a></h4>
	<h4><a href="/Warcry/logout?auth=${user.token}">Log out</a></h4>
	<h4><a href="/Warcry/message?auth=${user.token}">Compose new message</a></h4>
	<hr />
	<%
}
else
{
	%>
	<h4><a href="/Warcry/register">Register</a></h4>
	<h4><a href="/Warcry/login">Login</a></h4>
	<hr />
	<%
}

UserStore profileOwner = (UserStore)request.getAttribute("profileOwner");

if (profileOwner == null)
{
	%>No profile found<br /><%
}
else if (user != null && user.getLoggedIn() && user.getName().equals(profileOwner.getName()))
{
%>

<form action="/Warcry/profile?auth=${user.token}" method="POST">
Full name: <input type="text" name="fullName" value="${user.fullName}" />
<br />
Email: <input type="text" name="email" value="${user.email}" />
<br />
<br />
Password: <input type="password" name="password1" />
<br />
Repeat password: <input type="password" name="password2" />
<br />
<br />
<input type="submit" value="Update" />
</form>
<br />
<form action="/Warcry/delete/profile/${user.name}?auth=${user.token}" method="POST">
<input type="submit" value="Delete your profile" />
</form>
<hr />

<%
}
else
{
%>
User name: ${profileOwner.name}<br />
Full name: ${profileOwner.fullName}<br />
Email: ${profileOwner.email}<br />
<br />
<%

	FriendStore friend = (FriendStore)request.getAttribute("friend");
	if (friend == null)
	{
		%>
		<form action="/Warcry/friend?auth=${user.token}" method="POST">
		<input type="hidden" name="name" value="${profileOwner.name}">
		<input type="submit" value="Add as a friend" />
		</form>
		<br />
		<%
	}	
}

List<String> friends = (List<String>)request.getAttribute("friends");

if (friends == null || friends.isEmpty())
{
	%>No friends<br /><%	
}
else
{
	%>
	Friends:<br />
	<%
	
	for (String friend : friends)
	{
		if (userName.equals(friend))
		{
			%><a href="/Warcry/profile?auth=${user.token}">You</a><%
		}
		else
		{
			%><a href="/Warcry/profile/<%=friend %>?auth=${user.token}"><%=friend %></a><%
		}
		%>
		<form action="/Warcry/delete/friend/<%=friend %>?auth=${user.token}" method="POST">
		<input type="submit" value="Remove" />
		</form>
		<%
	}
}

%>
<hr />
<%


List<MessageStore> messages = (List<MessageStore>)request.getAttribute("messages");

if (messages == null)
{
	%>No messages<br /><%	
}
else for (MessageStore message : messages)
{
	String creator = message.getUser();
	
	if (userName.equals(creator))
	{
		%><a href="/Warcry/profile?auth=${user.token}">You</a><%
	}
	else
	{
		%><a href="/Warcry/profile/<%=message.getUser() %>?auth=${user.token}"><%=message.getUser() %></a><%
	}
	
	%>
	<%=message.getTime() %>
	<br />
	<%=message.getText() %>
	<%
	if (userName.equals(creator))
	{
		%>
		<form action="/Warcry/delete/message/<%=message.getTimeUUID() %>?auth=${user.token}" method="POST">
		<input type="submit" value="Delete" />
		</form>
		<%
	}
	else
	{
		%><br /><%
	}
	%>
	
	<br />
	<%	
}

%>

</body>
</html>