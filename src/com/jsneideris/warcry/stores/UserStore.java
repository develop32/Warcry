package com.jsneideris.warcry.stores;

public class UserStore 
{
	private String name;
	private String fullName;
	private String email;
	private RegisterError registerError = RegisterError.none;
	private LoginError loginError = LoginError.none;
	private ProfileError profileError = ProfileError.none;
	private String token;
	private String password;
	
	public boolean getLoggedIn()
	{
		return token != null;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public RegisterError getRegisterError()
	{
		return registerError;
	}
	
	public LoginError getLoginError()
	{
		return loginError;
	}
	
	public ProfileError getProfileError()
	{
		return profileError;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getToken()
	{
		return token;
	}
	
	public String getFullName()
	{
		if (fullName == null)
			return name;
		else
			return fullName;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setLoggedIn(boolean value)
	{
		if (token != null && !value)
			token = null;
	}
	
	public void setLoginError(LoginError loginError)
	{
		this.loginError = loginError;
	}
	
	public void setRegisterError(RegisterError registerError)
	{
		this.registerError = registerError;
	}
	
	public void setProfileError(ProfileError profileError)
	{
		this.profileError = profileError;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setToken(String token)
	{
		this.token = token;
	}
	
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
}
