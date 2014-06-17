package com.sociocentro.login.implentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.sociocentro.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

public class TwitterLogin {
	
	private String client_id;
	private String client_secret;
	private String request_tokenURL;
	private String CALLBACK_URL;

	public TwitterLogin(Activity activity, FragmentManager fragmentManager) {
		client_id = activity.getResources().getString(R.string.tw_client_id);
		client_secret = activity.getResources().getString(R.string.tw_client_secret);
		CALLBACK_URL = activity.getResources().getString(R.string.callback_url);
		//requestTwitterToken(activity.getResources(), CALLBACK_URL);
	}
	
	public String requestTwitterToken(Resources resources, String callback) {
		String response = "";
		try {
			URL url = new URL(resources.getString(R.string.tw_request_token_url));
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
			outputStreamWriter.write("oath_callback=" + callback);
			outputStreamWriter.flush();
			//Response would be a JSON response sent by instagram
			response = streamToString(httpsURLConnection.getInputStream());
			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
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
