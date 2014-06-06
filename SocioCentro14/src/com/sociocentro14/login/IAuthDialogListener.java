package com.sociocentro14.login;

public interface IAuthDialogListener {
	
	public abstract void onComplete(String token);
	public abstract void onError(String error);

}
