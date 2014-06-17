package com.sociocentro.login;

import android.app.Activity;
import android.app.FragmentManager;

import com.sociocentro.login.implentation.FacebookLogin;
import com.sociocentro.login.implentation.InstagramLogin;
import com.sociocentro.login.implentation.TwitterLogin;

public class LoginHandler {
	
	public LoginHandler(String socialMediaName, Activity activity, FragmentManager fragmentManager, IAuthListener authenticationListener) {
		SessionStore sessionStore = new SessionStore(activity);
		if (socialMediaName.equals("Facebook")) {
			FacebookLogin fbLogin = new FacebookLogin(activity, fragmentManager);
		} else if (socialMediaName.equals("Instagram")) {
			InstagramLogin igLogin = new InstagramLogin(activity, fragmentManager, sessionStore, authenticationListener);
		} else if (socialMediaName.equals("Twitter")) {
			TwitterLogin twLogin = new TwitterLogin(activity, fragmentManager);
		} else {
			
		}
	}
}