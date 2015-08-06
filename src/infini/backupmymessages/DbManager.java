package infini.backupmymessages;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbManager{

	Context context;
 
	public SQLiteDatabase db;
 
	private final String DBNAME = "mymessages";
	private final int DBVER = 1;
 
	private final String TBLNAME = "message_details";
	private final String SID = "id";
	private final String SNUMBER = "number";
	private final String SDATE = "date";
	private final String STIME = "time"; 
	private final String SBODY = "body";

	public DbManager(Context context) 
	{
		this.context = context;
 		CustomHelper helper = new CustomHelper(context);
		this.db = helper.getWritableDatabase();
	}
 	
 	public void addRow(String num, String date, String time, String body)
	{
		ContentValues values = new ContentValues();
		values.put(SNUMBER, num);
		values.put(SDATE, date);
		values.put(STIME, time);
		values.put(SBODY, body);
 		try{db.insert(TBLNAME, null, values);}
		catch(Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}
 
 	public void deleteRow(long id)
	{
		try {db.delete(TBLNAME, SID + "=" + id, null);}
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}
 
	public ArrayList<Object> getRowAsArray(long id)
	{

		ArrayList<Object> rowArray = new ArrayList<Object>();
		Cursor cursor;
 
		try
		{
			cursor = db.query
			(
					TBLNAME,
					new String[] { SID, SNUMBER, SDATE, STIME, SBODY },
					SID + "=" + id,
					null, null, null, null, null
			);
 
			cursor.moveToFirst();
 
			if (!cursor.isAfterLast())
			{
				do
				{
					rowArray.add(cursor.getLong(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
				
				}
				while (cursor.moveToNext());
			}
 			cursor.close();
		}
		catch (SQLException e) 
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		return rowArray;
	}
  
  	public ArrayList<ArrayList<Object>> getAllRowsAsArrays()
	{
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
 		Cursor cursor;
 
		try
		{
			cursor = db.query(
					TBLNAME,
					new String[]{SID, SNUMBER, SDATE, STIME, SBODY},
					null, null, null, null, null
			);
 			cursor.moveToFirst();
 			if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
 
					dataList.add(cursor.getLong(0));
					dataList.add(cursor.getString(1));
					dataList.add(cursor.getString(2));
					dataList.add(cursor.getString(3));
					dataList.add(cursor.getString(4));
 					dataArrays.add(dataList);
				}

				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
 		return dataArrays;
	}
 
  	private class CustomHelper extends SQLiteOpenHelper
	{
		public CustomHelper(Context context)
		{
			super(context, DBNAME, null, DBVER);
		}
 
		@Override
		public void onCreate(SQLiteDatabase db)
		{

			String sql = "create table " + TBLNAME +
										" (" +	SID + " integer primary key autoincrement not null,"
										+ SNUMBER + " text," + SDATE + " text," + STIME + " text," + SBODY + " text" + ");";
			db.execSQL(sql);
		}
 
 		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
 			db.execSQL("DROP TABLE IF EXISTS " + TBLNAME);
 			onCreate(db);
		}

		@Override
		public synchronized void close() {
			super.close();
		}
 	}

	public void deleteAllRow() {
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
		Cursor cursor;
		try
		{
			cursor = db.query(
					TBLNAME,
					new String[]{SID, SNUMBER, SDATE, STIME, SBODY},
					null, null, null, null, null
			);
 
			cursor.moveToFirst();
 
			if (!cursor.isAfterLast())
			{
				do
				{
					db.delete(TBLNAME, cursor.getString(cursor.getPosition()), null);
				}

				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
}