package com.sociocentro.login;

import com.sociocentro.login.handler.LoginHandler;
import com.sociocentro.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LoginMain extends Fragment implements OnClickListener{

	private static final String ARG_SECTION_NAME = "section_name";

	public LoginMain() {
	}

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static LoginMain newInstance(String sectionName) {
		LoginMain fragment = new LoginMain();
		Bundle args = new Bundle();
		args.putString(ARG_SECTION_NAME, sectionName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,false);
		Button loginButton = (Button) rootView.findViewById(R.id.login_button);
		loginButton.setText("Login to " + getArguments().getString(ARG_SECTION_NAME));
		loginButton.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		LoginHandler loginHandler = new LoginHandler(getArguments().getString(ARG_SECTION_NAME), getActivity(), getFragmentManager());
		loginHandler.setAuthenticationListener(new IAuthListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(getActivity(), getArguments().getString(ARG_SECTION_NAME) + " Authorization Successful", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFail(String error) {
				Toast.makeText(getActivity(), getArguments().getString(ARG_SECTION_NAME) + " Authorization Failed", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
