package infini.backupmymessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class viewHome extends Activity 
{
	/** Called when the activity is first created. */
	private TextView startdate_t;
	private Button startdate_b;

	private TextView test;

	private TextView enddate_t;
	private Button enddate_b;
	private Button view;
	private EditText keyname;
	private EditText keyword;
	private int mYear;
	private int mMonth;
	private int mDay;

	static final int DATE_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID2 = 1;
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_CHECKED = "checked";

	String keynames = "";
	String keywords = "";

	 static final int PROGRESS_DIALOG = 100;
	 ProgressDialog progressDialog;
	 Context _this = this;

	    private Handler unknownHandler = new Handler() 
	    {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Toast.makeText(viewHome.this, "Darn it! We don't know what happened...\nPlease try again.",
						Toast.LENGTH_LONG).show();
			}		
		};

	 
	private Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Bundle bundle = new Bundle();
			bundle.putString("total", jsonTotal.toString());
			Intent newIntent = new Intent(viewHome.this,
					ListApp.class);
			newIntent.putExtras(bundle);
			startActivity(newIntent);
		}
	};

	private Handler toastHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Toast.makeText(viewHome.this, "No results...",
					Toast.LENGTH_LONG).show();
		}		
	};

	private StringBuilder jsonTotal;
	 	 
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewhome);
		setTitle("VIEW MESSAGES");
		keyname = (EditText) findViewById(R.id.EditText01);
		keyword = (EditText) findViewById(R.id.EditText02);
		startdate_t = (TextView) findViewById(R.id.startdate_text);
		startdate_b = (Button) findViewById(R.id.startdate);
		enddate_t = (TextView) findViewById(R.id.enddate_text);
		enddate_b = (Button) findViewById(R.id.enddate);
		view = (Button) findViewById(R.id.view);
		test = (TextView) findViewById(R.id.TextView01);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final String usernameshare = pref.getString(PREF_USERNAME, null);
		final String passwordshare = pref.getString(PREF_PASSWORD, null);
		String checked = pref.getString(PREF_CHECKED, "FALSE");

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(PROGRESS_DIALOG);
	
				new Thread(new Runnable() {
					
					public void run() {
						InputStream in = null;
						int resCode = -1;
						keynames = keyname.getText().toString();
						keywords = keyword.getText().toString();
						try {
							String rod = "http://pasagoods.org/backmeup/s_downloadmessage.php?username="
									+ usernameshare
									+ "&password="
									+ passwordshare
									+ "&name="
									+ keynames
									+ "&keyword="
									+ keywords
									+ "&type=1&startdate=01%2F07%2F2010&enddate=12%2F21%2F2012&page_number=1";
							Log.v("viewHome", rod);
							URL url = new URL(rod);
							URLConnection urlConn = url.openConnection();

							if (!(urlConn instanceof HttpURLConnection)) {
								throw new IOException("URL is not an Http URL");
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

								if (total.toString().equals("-1")
										|| total.toString().equals("-2")
										|| total.toString().equals("-3")) {
									   toastHandler.sendEmptyMessage(0);
									Log.v("debug", "not OKOKK ");
								} else {
									jsonTotal = total;

									JSONObject	obj = new JSONObject(jsonTotal.toString());
									JSONArray messages = obj.getJSONArray("messages");
									if (messages.length() == 0) {
									    toastHandler.sendEmptyMessage(0);
									}
									else {
									handler.sendEmptyMessage(0);
									}
								}
							}
							else 
							{
								Log.v("debug", "OKOKK " + resCode);
							}
						} 
						catch (MalformedURLException e) 
						{
							unknownHandler.sendEmptyMessage(0);
							e.printStackTrace();
						} 
						catch (IOException e) 
						{
							unknownHandler.sendEmptyMessage(0);
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					    dismissDialog(PROGRESS_DIALOG);				
					}
				}).start();		
			}
		});

		startdate_b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
				updateDisplay();
			}
		});

		enddate_b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID2);
				updateDisplay2();
			}
		});
	}

	private void updateDisplay() {
		startdate_b.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("/").append(mDay).append("/")
				.append(mYear).append(" "));
	}

	private void updateDisplay2() {
		enddate_b.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("/").append(mDay).append("/")
				.append(mYear).append(" "));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	
	private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay2();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case DATE_DIALOG_ID2:
				return new DatePickerDialog(this, mDateSetListener2, mYear, mMonth,
						mDay);
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(_this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Downloading...");
			return progressDialog;	
		}
		return null;
	}
}