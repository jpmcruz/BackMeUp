package infini.backupmymessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class setusername extends Activity {
	/** Called when the activity is first created. */
	int code = 0;
	String textpassword = "";
	String textuser = "";
	String usernames = null;
	String passwords = null;

	static final int PROGRESS_DIALOG = 0;
	ProgressDialog progressDialog;
	Context _this = this;

	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username"; 
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_CHECKED = "checked";

	private Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Intent i = new Intent();
			i.setClass(setusername.this, backupmymessages.class);
			startActivity(i);
		}
	};

	private Handler successHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(setusername.this, "Account successfully created!",
					Toast.LENGTH_LONG).show();
		}
	};

	private Handler failHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(setusername.this,
					"An account with this username already exists.\nPlease try again.",
					Toast.LENGTH_LONG).show();
		}
	};

	private Handler unknownHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Toast.makeText(
					setusername.this,
					"Darn it! We don't know what happened...\nPlease try again.",
					Toast.LENGTH_LONG).show();
		}
	};

	private Handler errorHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(setusername.this, "Unk", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setuser);
		setTitle("SETUP ACCOUNT");
		Button ok = (Button) findViewById(R.id.ok);
		Button cancel = (Button) findViewById(R.id.cancel);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);

		password.setTransformationMethod(new PasswordTransformationMethod());

		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String usernameshare = pref.getString(PREF_USERNAME, null);
		String passwordshare = pref.getString(PREF_PASSWORD, null);
		String checked = pref.getString(PREF_CHECKED, "FALSE");

		if (usernameshare == null) 
		{

		} 
		else 
		{
			Intent i = new Intent();
			i.setClass(setusername.this, backupmymessages.class);
			startActivity(i);
		}

		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				usernames = username.getText().toString();
				passwords = password.getText().toString();
				if (usernames.equals("") || passwords.equals("")
						|| usernames.equals(null) || passwords.equals(null)) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							setusername.this);
					alert.setTitle("Oops");
					alert.setMessage("Please input email and password");
					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();
				}

				else if (usernames.contains("\n") || usernames.contains("\r")) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							setusername.this);
					alert.setTitle("Invalid UserName");
					alert.setMessage("Please remove newline/enter characters");

					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) 
								{
								}
							});
					alert.show();

				} 
				
				else if (passwords.contains("\n") || passwords.contains("\r")) 
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(
							setusername.this);
					alert.setTitle("Invalid Password");
					alert.setMessage("Please remove newline/enter characters");

					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();
				}
				
				else if (!usernames.contains("@") && !usernames.contains(".")) 
				{
					AlertDialog.Builder alert = new AlertDialog.Builder(
							setusername.this);
					alert.setTitle("Invalid Email Address");
					alert.setMessage("Please make sure you input your valid email address.");

					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();

				}

				else {
					showDialog(PROGRESS_DIALOG);
					Runnable mRunnable = new Runnable() {

						public void run() {
							TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
							final String number = telephonyManager
									.getSimSerialNumber();
							textpassword = password.getText().toString();
							textuser = username.getText().toString();
							InputStream in = null;
							int resCode = -1;
							try {
								String rod = "http://pasagoods.org/backmeup/s_createaccount.php?cellnum="
										+ number
										+ "&password="
										+ textpassword
										+ "&username=" + textuser;
								URL url = new URL(rod);
								URLConnection urlConn = url.openConnection();

								if (!(urlConn instanceof HttpURLConnection)) {
									throw new IOException(
											"URL is not an Http URL");
								}

								HttpURLConnection httpConn = (HttpURLConnection) urlConn;
								httpConn.setAllowUserInteraction(false);
								httpConn.setInstanceFollowRedirects(true);
								httpConn.setRequestMethod("GET");
								httpConn.connect();
								resCode = httpConn.getResponseCode();

								if (resCode == HttpURLConnection.HTTP_OK) {
									in = httpConn.getInputStream();
									BufferedReader r = new BufferedReader(
											new InputStreamReader(in));
									final StringBuilder total = new StringBuilder();
									String line;
									while ((line = r.readLine()) != null) {
										total.append(line);
									}
									if (total.toString().equals("0")) {
										successHandler.sendEmptyMessage(0);
										getSharedPreferences(PREFS_NAME,
												MODE_PRIVATE)
												.edit()
												.putString(
														PREF_USERNAME,
														username.getText()
																.toString())
												.putString(
														PREF_PASSWORD,
														password.getText()
																.toString())
												.putString(PREF_CHECKED, "TRUE")
												.commit();
										handler.sendEmptyMessage(0);

									} else if (total.toString().equals("-1")) {
										failHandler.sendEmptyMessage(0);
									}
									code = 0;
								}
							} catch (MalformedURLException e) {
								unknownHandler.sendEmptyMessage(0);
								code = 1;
								e.printStackTrace();
							} catch (IOException e) {
								unknownHandler.sendEmptyMessage(0);
								code = 1;
								e.printStackTrace();
							}
							dismissDialog(PROGRESS_DIALOG);
						}
					};
					new Thread(mRunnable).start();
				}
			} 
		});

		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(_this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Creating account...");
			return progressDialog;
		default:
			return null;
		}
	}
}