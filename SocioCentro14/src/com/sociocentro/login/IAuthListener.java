package com.sociocentro.login;

public interface IAuthListener {

	public abstract void onSuccess();
	public abstract void onFail(String error);
	
}
