package com.sociocentro.login.implentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sociocentro.R;
import com.sociocentro.login.IAuthDialogListener;
import com.sociocentro.login.IAuthListener;
import com.sociocentro.login.LoginDialog;
import com.sociocentro.login.SessionStore;

public class InstagramLogin {

	private String client_id;
	private String client_secret;
	private String authURL;
	private String tokenURL;
	private String authURLString;
	private String tokenURLString;
	private String CALLBACK_URL;
	private String accessToken;
	private ProgressDialog progressDialog;
	private IAuthListener authenticationListener;
	private SessionStore sessionStore;

	public InstagramLogin(Activity activity, FragmentManager fragmentManager, SessionStore sessionStore, IAuthListener authenticationListener) {
		client_id = activity.getResources().getString(R.string.ig_client_id);
		client_secret = activity.getResources().getString(R.string.ig_client_secret);
		authURL = activity.getResources().getString(R.string.ig_auth_url);
		tokenURL = activity.getResources().getString(R.string.ig_token_url);
		CALLBACK_URL = activity.getResources().getString(R.string.callback_url);
		authURLString = authURL + "?client_id=" + client_id + "&redirect_uri=" + CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships";
		tokenURLString = tokenURL + "?client_id=" + client_id + "&client_secret=" + client_secret + "&redirect_uri=" + CALLBACK_URL + "&grant_type=authorization_code";
		this.sessionStore = sessionStore;
		IAuthDialogListener authenticationDialogListener = new IAuthDialogListener() {

			@Override
			public void onComplete(String token) {
				String[] params = new String[2];
				params[0] = tokenURLString;
				params[1] = "client_id="+ client_id + "&client_secret="+ client_secret + "&grant_type=authorization_code" + "&redirect_uri="+CALLBACK_URL+ "&code=" + token;
				new GetInstagramTokenAsyncTask().execute(params);
			}

			@Override
			public void onError(String error) {
			}

		};
		LoginDialog loginDialog = new LoginDialog(authURLString, authenticationDialogListener);
		loginDialog.show(fragmentManager, "loginDialog");
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("Please Wait");
		progressDialog.setCancelable(false);
		this.authenticationListener = authenticationListener;
	}

	public class GetInstagramTokenAsyncTask extends AsyncTask<String, Void, String>{

		protected String doInBackground(String... Params) {
			try {
				String[] parameters = Params;
				URL url = new URL(parameters[0]);
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
				httpsURLConnection.setRequestMethod("POST");
				httpsURLConnection.setDoInput(true);
				httpsURLConnection.setDoOutput(true);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
				outputStreamWriter.write(parameters[1]);
				outputStreamWriter.flush();
				//Response would be a JSON response sent by instagram
				String response = streamToString(httpsURLConnection.getInputStream());
				return response;
			}
			catch (Exception e) {
				authenticationListener.onFail(e.toString());
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			showDialog("Getting Access Token...");
		}

		protected void onPostExecute(String response) {
			dismissDialog();
			String id = "";
			String username = "";
			String name = "";
			try {
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				accessToken = jsonObject.getString("access_token");
				JSONObject userJsonObject = jsonObject.getJSONObject("user");
				id = userJsonObject.getString("id");
				username = userJsonObject.getString("username");
				name = userJsonObject.getString("full_name");
				sessionStore.saveInstagramSession(id, username, name, accessToken);
				authenticationListener.onSuccess();
				Log.d("Access Token", accessToken);
			} catch (JSONException e) {
				authenticationListener.onFail(e.toString());
				e.printStackTrace();
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
}
