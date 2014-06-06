package com.sociocentro14.login;

import com.sociocentro14.login.handler.LoginHandler.AuthenticationDialogListener;
import com.sociocentro14.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

@SuppressLint({ "ValidFragment", "SetJavaScriptEnabled" })
public class LoginDialog extends DialogFragment{

	private String url;
	private AuthenticationDialogListener authenticationDialogListener;
	private ProgressDialog progressDialog;
	private TextView textView;
	private WebView webView;
	private boolean isShowing = false;

	public LoginDialog(String url, AuthenticationDialogListener AuthenticationDialogListener) {
		this.url = url;
		this.authenticationDialogListener = authenticationDialogListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog; 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_login, null);
		textView = (TextView) view.findViewById(R.id.dialogTextView);
		webView = (WebView) view.findViewById(R.id.dialogWebView);
		initWebView();
		webView.loadUrl(url);
		CookieSyncManager.createInstance(getActivity());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		progressDialog = new ProgressDialog(getActivity());
		return view;
	}

	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearCache(true);  
		webView.setScrollBarStyle(0);
		webView.setWebViewClient(new AuthWebViewClient());
		webView.setWebChromeClient(new AuthWebChromeClient());
	}

	public class AuthWebViewClient extends WebViewClient {

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (!isShowing) {
				showDialog("Loading...");
			}
		}

		public void onPageFinished(WebView view, String url) {
			String title = webView.getTitle();
			if (title != null && title.length() > 0) {
				textView.setText(title);
			}
			Log.d("Login Dialog", "On Page Finished URL: " + url);
			LoginDialog.this.dismissDialog();
		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(getResources().getString(R.string.callback_url))) {
				System.out.println(url);
				String urls[] = url.split("=");
				//try because cancel
				authenticationDialogListener.onComplete(urls[1]);
				LoginDialog.this.dismiss();
				return true;
			}
			return false;
		}

		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			LoginDialog.this.dismiss();
			authenticationDialogListener.onError(description);
		}
	}

	public class AuthWebChromeClient extends WebChromeClient {

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
