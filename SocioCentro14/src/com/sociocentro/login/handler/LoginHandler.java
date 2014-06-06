package com.sociocentro.login.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.sociocentro.login.IAuthListener;
import com.sociocentro.login.LoginDialog;
import com.sociocentro.login.SessionStore;
import com.sociocentro.R;

public class LoginHandler {

	private static final int FACEBOOK = 0;
	private static final int INSTAGRAM = 1;
	private static final int TWITTER = 2;
	private int socialMedia;
	private String client_id;
	private String client_secret;
	private String authURLString;
	private String tokenURLString;
	private String CALLBACK_URL;
	private IAuthListener authenticationListener;
	private SessionStore sessionStore;
	private String token;
	private String accessToken;
	private ProgressDialog progressDialog;

	public LoginHandler(String socialMediaName, Activity activity, FragmentManager fragmentManager) {
		sessionStore = new SessionStore(activity);
		CALLBACK_URL = activity.getResources().getString(R.string.callback_url);
		switch (convertMediaName(socialMediaName)) {
		case FACEBOOK:
			client_id = activity.getResources().getString(R.string.fb_client_id);
			client_secret = activity.getResources().getString(R.string.fb_client_secret);
			authURLString = activity.getResources().getString(R.string.fb_auth_url);
			tokenURLString = activity.getResources().getString(R.string.fb_token_url);
			accessToken = sessionStore.getFacebookAccessToken();
			socialMedia = FACEBOOK;
			break;
		case INSTAGRAM:
			client_id = activity.getResources().getString(R.string.ig_client_id);
			client_secret = activity.getResources().getString(R.string.ig_client_secret);
			authURLString = activity.getResources().getString(R.string.ig_auth_url) + "?client_id=" + client_id + "&redirect_uri=" + CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships";
			tokenURLString = activity.getResources().getString(R.string.ig_token_url) + "?client_id=" + client_id + "&client_secret=" + client_secret + "&redirect_uri=" + CALLBACK_URL + "&grant_type=authorization_code";
			accessToken = sessionStore.getInstagramAccessToken();
			socialMedia = INSTAGRAM;
			break;
		case TWITTER:
			client_id = activity.getResources().getString(R.string.tw_client_id);
			client_secret = activity.getResources().getString(R.string.tw_client_secret);
			authURLString = activity.getResources().getString(R.string.tw_auth_url);
			tokenURLString = activity.getResources().getString(R.string.tw_token_url);
			accessToken = sessionStore.getTwitterAccessToken();
			socialMedia = TWITTER;
			break;
		default:
			break;
		}
		AuthenticationDialogListener authenticationDialogListener = new AuthenticationDialogListener();
		LoginDialog loginDialog = new LoginDialog(authURLString, authenticationDialogListener);
		loginDialog.show(fragmentManager, "loginDialog");
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("Please Wait");
		progressDialog.setCancelable(false);
	}

	public class AuthenticationDialogListener implements com.sociocentro.login.IAuthDialogListener {

		@Override
		public void onComplete(String token) {
			getAccessToken(token);
		}

		@Override
		public void onError(String error) {
		}
	}
	
	private void getAccessToken(String token) {
		this.token = token;
		switch (socialMedia) {
		case FACEBOOK:
			break;
		case INSTAGRAM:
			new GetInstagramTokenAsyncTask().execute();
			break;
		case TWITTER:
			break;
		default:
			break;
		}
	}
	
	public class GetInstagramTokenAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL(tokenURLString);
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
				httpsURLConnection.setRequestMethod("POST");
				httpsURLConnection.setDoInput(true);
				httpsURLConnection.setDoOutput(true);
				
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
				outputStreamWriter.write("client_id="+client_id+
						"&client_secret="+ client_secret +
						"&grant_type=authorization_code" +
						"&redirect_uri="+CALLBACK_URL+
						"&code=" + token);
				outputStreamWriter.flush();
				//Response would be a JSON response sent by instagram
				String response = streamToString(httpsURLConnection.getInputStream());
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				accessToken = jsonObject.getString("access_token");
				JSONObject userJsonObject = jsonObject.getJSONObject("user");
				String id = userJsonObject.getString("id");
				String username = userJsonObject.getString("username");
				String name = userJsonObject.getString("full_name");
				sessionStore.saveInstagramSession(id, username, name, accessToken);
				//LoadHomePage
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			showDialog("Getting Access Token...");
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissDialog();
			authenticationListener.onSuccess();
			super.onPostExecute(result);
		}
	}
	
	public String streamToString(InputStream is) throws IOException {
		String string = "";
		if (is != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				reader.close();
			} finally {
				is.close();
			}
			string = stringBuilder.toString();
		}
		return string;
	}
	
	public void showDialog(String message) {
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	public void dismissDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void setAuthenticationListener(IAuthListener authenticationListener) {
		this.authenticationListener = authenticationListener;
	}
	
	private int convertMediaName(String socialMediaName) {
		if (socialMediaName.equals("Facebook")) {
			return 0;
		} else if (socialMediaName.equals("Instagram")) {
			return 1;
		} else if (socialMediaName.equals("Twitter")) {
			return 2;
		} else {
			return 3;
		}
	}
}
