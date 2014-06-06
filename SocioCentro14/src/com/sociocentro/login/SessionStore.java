package com.sociocentro.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionStore {
	
	private String FILE_NAME = "SocioCentro_Session_Store";
	private String FB_ACCESS_TOKEN_KEY = "FB_Access_Token";
	private String IG_ACCESS_TOKEN_KEY = "IG_Access_Token";
	private String TW_ACCESS_TOKEN_KEY = "TW_Access_Token";
	private Context context;
	
	public SessionStore(Context context) {
		this.context = context;
	}
	
	public SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveFacebookAccessToken(String accessToken) {
		Editor editor = getSharedPreferences().edit();
		editor.putString(FB_ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}
	
	public void saveInstagramAccessToken(String accessToken) {
		Editor editor = getSharedPreferences().edit();
		editor.putString(IG_ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}
	
	public void saveTwitterAccessToken(String accessToken) {
		Editor editor = getSharedPreferences().edit();
		editor.putString(TW_ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}
	
	public String getFacebookAccessToken() {
		return getSharedPreferences().getString(FB_ACCESS_TOKEN_KEY, null);
	}
	
	public String getInstagramAccessToken() {
		return getSharedPreferences().getString(IG_ACCESS_TOKEN_KEY, null);
	}
	
	public String getTwitterAccessToken() {
		return getSharedPreferences().getString(TW_ACCESS_TOKEN_KEY, null);
	}
	
	public void saveInstagramSession(String id,  String name, String username, String token) {
		Editor editor = getSharedPreferences().edit();
		editor.putString("IG_API_ID", id);
		editor.putString("IG_API_NAME", name);
		editor.putString("IG_API_USERNAME", username);
		editor.putString("IG_API_ACCESS_TOKEN", token);
		editor.commit();
	}
	
	public void resetInstagram() {
		Editor editor = getSharedPreferences().edit();
		editor.remove("IG_API_ID");
		editor.remove("IG_API_NAME");
		editor.remove("IG_API_USERNAME");
		editor.remove("IG_API_ACCESS_TOKEN");
		editor.commit();
	}
	
	public void resetSharedPreferences() {
		Editor editor = getSharedPreferences().edit();
		editor.clear();
		editor.commit();
	}
}

