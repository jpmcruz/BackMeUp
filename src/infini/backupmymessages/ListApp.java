package infini.backupmymessages;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListApp extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setTitle("MESSAGES");
		Bundle bundle = this.getIntent().getExtras();
		String param1 = bundle.getString("total");
		FRIEND_STATUSES.clear();
		FRIEND_NAMES.clear();
		FRIEND_IMAGES.clear();
		FRIEND_DATE.clear();

		try {
			JSONObject obj = new JSONObject(param1);
			JSONArray messages = obj.getJSONArray("messages");

			for (int i = 0; i < messages.length(); i++) 
			{

				JSONObject mess = messages.getJSONObject(i);
				String m_name = mess.getString("name");
				String m_type = mess.getString("type");
				String m_cellnum = mess.getString("other_cellnum");
				String m_message = mess.getString("message");
				String m_timestamp = mess.getString("timestamp");
				String m_timestamp_short = m_timestamp.substring(0, 16);
				if (m_type.equals("1")) 
				{
					m_type = "INBOX";
				}
				FRIEND_STATUSES.add(m_message);
				FRIEND_NAMES.add(m_name);
				FRIEND_DATE.add(m_timestamp_short);
				FRIEND_IMAGES.add(BitmapFactory.decodeResource(getResources(),
						R.drawable.default_face));
			}
			setListAdapter(new EfficientAdapter(this));
			ListView lv = getListView();
			lv.setClickable(true);
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
								AlertDialog.Builder alert = new AlertDialog.Builder(
							ListApp.this);
					alert.setTitle("Message");
					alert.setMessage("From: " + FRIEND_NAMES.get(position)
							+ "\nMessage: " + FRIEND_STATUSES.get(position)
							+ "\nDate and Time: " + FRIEND_DATE.get(position)		
					);

					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();
				}
			});
		}

		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}

	private int itemSelected;
	public static List<String> FRIEND_NAMES = new ArrayList<String>();
	public static List<String> FRIEND_STATUSES = new ArrayList<String>();
	public static List<String> FRIEND_DATE = new ArrayList<String>();
	static List<Bitmap> FRIEND_IMAGES = new ArrayList<Bitmap>();

	private static class EfficientAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;

		public EfficientAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public Object getItem(int position) 
		{
			return position;
		}

		public long getItemId(int position) 
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.customlist, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.listtext);
				holder.date = (TextView) convertView
						.findViewById(R.id.listdate);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.listicon);
				holder.subtext = (TextView) convertView
						.findViewById(R.id.listsubtext);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(FRIEND_NAMES.get(position));
			holder.date.setText(FRIEND_DATE.get(position));
			holder.icon.setImageBitmap(FRIEND_IMAGES.get(position));
			holder.subtext.setText(FRIEND_STATUSES.get(position));
			return convertView;
		}

		static class ViewHolder 
		{
			TextView text;
			ImageView icon; 
			TextView subtext;
			TextView date;
		}

		public int getCount() 
		{
			return FRIEND_NAMES.size();
		}
	}
}
