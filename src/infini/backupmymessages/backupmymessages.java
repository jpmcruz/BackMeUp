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

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class backupmymessages extends Activity {
	
	String password = "";
    int code = 0;     
	DbManager db;
    int size = 0;
	String vbody = "";
	String vnumber = "";
	String vtime = "";
	String vname = "";
	String rowid = "";
	
	 public static final String PREFS_NAME = "MyPrefsFile";
		private static final String PREF_USERNAME = "username";
		private static final String PREF_PASSWORD = "password";
		private static final String PREF_CHECKED = "checked";
		 static final int PROGRESS_DIALOG = 0;
		 ProgressDialog progressDialog;
		 Context _this = this;
    
		 private Handler successHandler = new Handler() 
		 {
				@Override
				public void handleMessage(Message msg) 
				{
					super.handleMessage(msg);
					Toast.makeText(backupmymessages.this, "Sync successful!",
							Toast.LENGTH_LONG).show();
				}			
			};
			
		    private Handler unknownHandler = new Handler() 
		    {
				@Override
				public void handleMessage(Message msg) 
				{
					super.handleMessage(msg);
					Toast.makeText(backupmymessages.this, "Darn it! We don't know what happened...\nPlease try again.",
							Toast.LENGTH_LONG).show();
				}			
			};
		 
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("SMS BACKMEUP v1.0");
        db = new DbManager(this);
	    size = db.getAllRowsAsArrays().size();
        final Button Settings = (Button) findViewById(R.id.settings_bt);
        Button View = (Button) findViewById(R.id.view_bt);
        Button Sync = (Button) findViewById(R.id.sync_bt);
        Button ViewAll = (Button) findViewById(R.id.viewall);
        final TextView Test = (TextView) findViewById(R.id.test);
      
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		final String usernameshare = pref.getString(PREF_USERNAME, null);
		final String passwordshare = pref.getString(PREF_PASSWORD, null);
			
        Settings.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick (View v)
			{
	    		Intent i = new Intent();
				i.setClass(backupmymessages.this, settings.class);	
				startActivity(i);
			}
	    });
	    
	    View.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick (View v)
			{
	    		Intent i = new Intent();
				i.setClass(backupmymessages.this, viewHome.class);	
				startActivity(i);
			}
	    });
	    
	    Sync.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick (View v)
	    	{		
	    		final DbManager db = new DbManager(_this);
	    		final int sizes = db.getAllRowsAsArrays().size();
	    		 if (sizes == 0)
	    		    {
	    		    	Toast.makeText(backupmymessages.this, "No message to sync...\n" , Toast.LENGTH_LONG).show();
	    		    }
	    		 else
	    		 {	    			 
	 	    		showDialog(PROGRESS_DIALOG);
	 	    		Runnable mRunnable = new Runnable() 
	 	    		{
						public void run() {
			    			 TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			    				final String number = telephonyManager.getSimSerialNumber();
			    				InputStream in = null;
			    				int resCode = -1;
			    				for (int i = 0; i < sizes; i++) 
			    				{
			    					vnumber = db.getAllRowsAsArrays().get(i).get(1).toString();
			    					vtime = db.getAllRowsAsArrays().get(i).get(2).toString();
			    					vbody = db.getAllRowsAsArrays().get(i).get(3).toString();
			    					vname = db.getAllRowsAsArrays().get(i).get(4).toString();
			    					rowid = db.getAllRowsAsArrays().get(i).get(0).toString();
			    					Log.v("sync", "" + i);
			    					try 
			    					{
			    						String rod = "http://pasagoods.org/backmeup/s_uploadmessage.php?username="
			    								+ URLEncoder.encode(usernameshare)
			    								+ "&cellnum="
			    								+ URLEncoder.encode(number)
			    								+ "&password="
			    								+ URLEncoder.encode(passwordshare)
			    								+ "&type="
			    								+ "1"
			    								+ "&other_cellnum="
			    								+ URLEncoder.encode(vnumber)
			    								+ "&name="
			    								+ URLEncoder.encode(vname)
			    								+ "&message="
			    								+ URLEncoder.encode(vbody)
			    								+ "&timestamp="
			    								+ URLEncoder.encode(vtime);
			    						Log.v("debug", rod);
			    						URL url = new URL(rod);
			    						URLConnection urlConn = url.openConnection();

			    						if (!(urlConn instanceof HttpURLConnection)) 
			    						{
			    							throw new IOException("URL is not an Http URL");
			    						}

			    						HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			    						httpConn.setAllowUserInteraction(false);
			    						httpConn.setInstanceFollowRedirects(true);
			    						httpConn.setRequestMethod("GET");
			    						httpConn.connect();
			    						resCode = httpConn.getResponseCode();

			    						if (resCode == HttpURLConnection.HTTP_OK) 
			    						{
			    							in = httpConn.getInputStream();
			    							BufferedReader r = new BufferedReader(
			    									new InputStreamReader(in));
			    							final StringBuilder total = new StringBuilder();
			    							String line;
			    							while ((line = r.readLine()) != null) 
			    							{
			    								total.append(line);
			    							}
			    							if (total.toString().equals("0")) 
			    							{
			    								deleteRow(rowid);
			    							    successHandler.sendEmptyMessage(0);	
			    							}
			    							else 
			    							{
			    							    unknownHandler.sendEmptyMessage(0);	
			    							}
			    							code = 0;
			    						} 
			    						else 
			    						{
			    							Log.v("debug", "OKOKK " + resCode);
			    						}
			    					} 
			    					catch (MalformedURLException e) 
			    					{
			    						code = 1;
			    						e.printStackTrace();
			    					} 
			    					catch (IOException e) 
			    					{
			    						code = 1;
			    						e.printStackTrace();
			    					}
			    				}
			    	    		dismissDialog(PROGRESS_DIALOG);
						}	 	    			
	 	    		};
	    		 new Thread(mRunnable).start();
	    		 }
			}
	    });
	}
    
    private void deleteRow(String id) 
	{
		try 
		{
			db.deleteRow(Long.parseLong(id));
		} 
		catch (Exception e) 
		{
			Log.e("Delete Error", e.toString());
			e.printStackTrace();
		}
	}
    @Override
    protected void onResume()
    {
    	super.onResume();
    	db = new DbManager(this);
 	    size = db.getAllRowsAsArrays().size();
    }

	@Override
	protected void onDestroy() 
	{
		finish();
		super.onDestroy();
	}

	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(_this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Syncing messages...");
			return progressDialog;
		default:
			return null;
		}    	
    }
}