package infini.backupmymessages;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SMSReceiver extends BroadcastReceiver {
	DbManager db;
	TableLayout dataTable;
	String name = "";
	String from = "";
	String body = "";
	String time = "";
	String phoneNumber = "";
	String phoneNumber2 = "";
	String finalname = "";
	int flag = 0;
	public static final String SMS_EXTRA_NAME = "pdus";

	@TargetApi(Build.VERSION_CODES.ECLAIR) @SuppressLint("NewApi") @Override
	public void onReceive(Context context, Intent intent) {
		db = new DbManager(context);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		long now;

		// Log.i("time", "time"+formatter.format(calendar.getTime()));

		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;

		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get(SMS_EXTRA_NAME);

			ContentResolver content = context.getContentResolver();
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				from += msgs[i].getOriginatingAddress();
				body += msgs[i].getMessageBody().toString();
				// body += "\n";
				// str += ((msgs[i].getTimestampMillis() / 1000)/ 3600);

				now = msgs[i].getTimestampMillis();
				calendar.setTimeInMillis(now);

				time += formatter.format(calendar.getTime());
			}

			// check phonebook for name of sender
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			while (cursor.moveToNext()) {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				name = cursor
						.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				phoneNumber = "";
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

				if (hasPhone.equalsIgnoreCase("1"))
					hasPhone = "true";
				else
					hasPhone = "false";

				if (Boolean.parseBoolean(hasPhone)) {
					Cursor phones = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						phoneNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneNumber2 = from;
						int len1 = phoneNumber.length();
						int len2 = phoneNumber2.length();

						if (phoneNumber.length() > 10) {
							phoneNumber = phoneNumber
									.substring(len1 - 10, len1);

						}
						if (phoneNumber2.length() > 10) {
							phoneNumber2 = phoneNumber2.substring(len2 - 10,
									len2);
						}
						if (phoneNumber.equals(phoneNumber2)) {
							flag = 1;
							break;

							// finalname = name;
						} else {
							name = "";
						}
						// Toast.makeText(context, name + " / " +phoneNumber,
						// Toast.LENGTH_LONG).show();
					}
					phones.close();

					if (flag == 1) {
						break;
					}
				} else {

					cursor.close();

				}

			}
			if (flag == 0) {
				name = from.toString();
			}

			addRow(from, time, body, name);
			// updateRow();
		}
	    db.db.close();
	}

	private void addRow(String title, String two, String three, String four) {
		try {
			db.addRow(title, two, three, four);

			// updateTable();

		} catch (Exception e) {
			Log.e("Add Error", e.toString());
			e.printStackTrace();
		}
	}

}
