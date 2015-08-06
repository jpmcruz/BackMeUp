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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class settings extends Activity 
{
    /** Called when the activity is first created. */
	 public static final String PREFS_NAME = "MyPrefsFile";    //unique identifier for our Preferences
	 private static final String PREF_USERNAME = "username";   //fields to be saved
	 private static final String PREF_PASSWORD = "password";
	 private static final String PREF_CHECKED = "checked";
	 static final int PROGRESS_DIALOG = 0;
	 ProgressDialog progressDialog;
	 Context _this = this;

	String newpassword = "";
	String oldpassword = "";
	String confirmpassword = "";
	 int code = 0; 
	 
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dismissDialog(PROGRESS_DIALOG);
			}
		};
 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setTitle("SETTINGS: CHANGE PASSWORD");
        
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
        final String usernameshare = pref.getString(PREF_USERNAME, null);
        final String passwordshare = pref.getString(PREF_PASSWORD, null);
        
        final EditText oldpass = (EditText) findViewById(R.id.oldpass);                
        final EditText newpass = (EditText) findViewById(R.id.newpass);
        final EditText confirmpass = (EditText) findViewById(R.id.confirmpass);              
        Button update = (Button) findViewById(R.id.change);
        
        oldpass.setTransformationMethod(new PasswordTransformationMethod());
        newpass.setTransformationMethod(new PasswordTransformationMethod());
        confirmpass.setTransformationMethod(new PasswordTransformationMethod());
    
        update.setOnClickListener(new OnClickListener()
	    {
	    	public void onClick (View v)
			{
	    		newpassword = newpass.getText().toString();
	    		oldpassword = oldpass.getText().toString();
	    		confirmpassword = confirmpass.getText().toString();
	    		
	    		if (newpassword.equals("") || newpassword.equals(null)
	    			||	oldpassword.equals("") || oldpassword.equals(null)
	    			||	confirmpassword.equals("") || confirmpassword.equals(null))
	    		{
	    			Toast.makeText(settings.this, "COMPLETE REQUIRED FIELDS", Toast.LENGTH_LONG).show();
	    		}
	    		
	    		if (!newpassword.equals(confirmpassword))
	    		{
	    			Toast.makeText(settings.this, "CONFIRM PASSWORD IS NOT EQUAL TO NEW PASSWORD", Toast.LENGTH_LONG).show();
	    		}
	    		
	    		if (!passwordshare.equals(oldpassword))
	    		{
	    			Toast.makeText(settings.this, "OLD PASSWORD IS INCORRECT", Toast.LENGTH_LONG).show();
	    		}
	    		
	    		if (passwordshare.equals(oldpassword) && newpassword.equals(confirmpassword))
	    		{
	    			showDialog(PROGRESS_DIALOG);
	    			new Thread(new Runnable(){

						public void run() {
				    		InputStream in = null;
			        		int resCode = -1;
			    			try 
				    		{
			    				String rod = "http://pasagoods.org/backmeup/s_changpassword.php?username=" + usernameshare + "&oldpassword=" + oldpassword + "&newpassword=" + newpassword;
				        	
				        		URL url = new URL(rod);
				    		    URLConnection urlConn = url.openConnection();

				    		    if (!(urlConn instanceof HttpURLConnection)) 
				    		    {
				    		    	throw new IOException ("URL is not an Http URL");
				    	        }

				    		    HttpURLConnection httpConn = (HttpURLConnection)urlConn;
				    		    httpConn.setAllowUserInteraction(false);
				    		    httpConn.setInstanceFollowRedirects(true);
				    		    httpConn.setRequestMethod("GET");
				    		    httpConn.connect();
				    		    resCode = httpConn.getResponseCode();

				    		    if (resCode == HttpURLConnection.HTTP_OK) 
				    		    {
				    		        in = httpConn.getInputStream(); 
				    		        BufferedReader r = new BufferedReader(new InputStreamReader(in));
				    				final StringBuilder total = new StringBuilder();
				    				String line;
				    				while ((line = r.readLine()) != null) 
				    				{
				    					total.append(line);
				    				}

				    				if (total.toString().equals("0"))
				    				{
				    					Toast.makeText(settings.this, "CREATING NEW PASSWORD", Toast.LENGTH_LONG).show();
				    	    			
				    	    			getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
				    	                .edit()
				    	                .putString(PREF_PASSWORD, newpass.getText().toString())
				    	                .putString(PREF_CHECKED,"TRUE")
				    	                .commit();

				    		    		Intent i = new Intent();
				    					i.setClass(settings.this, backupmymessages.class);	
				    					startActivity(i);
				    					
				    					Toast.makeText(settings.this, "PASSWORD UPDATE SUCCESSFUL!", Toast.LENGTH_LONG).show();

				    				
				    				}
				    				else if (total.toString() == "-1")
				    				{
				    					Toast.makeText(settings.this, "WEBSITE ERROR", Toast.LENGTH_LONG).show();  					
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
				    			Toast.makeText(settings.this, "1", Toast.LENGTH_LONG).show();
				    			code = 1;
				    			e.printStackTrace();
				    		} 
				    		catch (IOException e) 
				    		{
				    			Toast.makeText(settings.this, "2", Toast.LENGTH_LONG).show();
				    			code = 1;
				    		    e.printStackTrace();
				    		}
				    		handler.sendEmptyMessage(0);							
						}	    				
	    			}).start();		
	    		}
			}
	    });
	}
	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(_this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Updating password...");
			return progressDialog;
		default:
			return null;
		}    	
    }		
}