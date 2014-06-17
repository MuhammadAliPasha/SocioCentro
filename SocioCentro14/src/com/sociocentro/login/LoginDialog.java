package com.sociocentro.login;

import com.sociocentro.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint({ "ValidFragment", "SetJavaScriptEnabled" })
public class LoginDialog extends DialogFragment{

	private String url;
	private IAuthDialogListener authenticationDialogListener;
	private ProgressDialog progressDialog;
	private TextView textView;
	private WebView webView;
	private boolean isShowing = false;

	public LoginDialog(String url, IAuthDialogListener authenticationDialogListener) {
		this.url = url;
		this.authenticationDialogListener = authenticationDialogListener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		webView = new WebView(getActivity());
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		webView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearCache(true);  
		webView.setScrollBarStyle(0);
		webView.setWebViewClient(new AuthWebViewClient());
		webView.setWebChromeClient(new AuthWebChromeClient());
		webView.getSettings().setAllowFileAccess(true);
		
		builder.setView(webView);
		
		progressDialog = new ProgressDialog(getActivity());
		webView.loadUrl(url);
		CookieSyncManager.createInstance(getActivity());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		return builder.create(); 
	}
	
//	private void initWidgets(Dialog dialog) {
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		View view = inflater.inflate(R.layout.fragment_dialog_login, null);
//		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.dialogLinearLayout);
//		textView = new TextView(getActivity());
//		textView.setText("Login");
//		textView.setTextColor(Color.WHITE);
//		textView.setBackgroundColor(Color.BLACK);
//		textView.setPadding(20, 10, 10, 10);
//		linearLayout.addView(textView);
//		
//		
//		
//		DisplayMetrics displaymetrics = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		int height = displaymetrics.heightPixels;
//		int width = displaymetrics.widthPixels;
//		float[] portrait = {width, height};
//		float[] landscape = {height, width};
//		
//		float[] dimensions = (width < height) ? portrait : landscape;
//		dialog.addContentView(linearLayout, new FrameLayout.LayoutParams((int) (dimensions[0] * 0.95), (int) (dimensions[1] * 0.5)));
//	}
	
	public class AuthWebViewClient extends WebViewClient {

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (!isShowing) {
				Log.d("loading page", url);
				showDialog("Loading...");
			}
		}

		public void onPageFinished(WebView view, String url) {
			LoginDialog.this.dismissDialog();
		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(getResources().getString(R.string.callback_url))) {
				String urls[] = url.split("=");
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
