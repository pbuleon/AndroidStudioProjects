package com.patbul.ffe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SmsListMgr 
{
	public static String contactsLookup_keyString="";
	
	LinearLayout smsListView;
	Activity activity;
	String contacts;
	
	SmsListMgr(LinearLayout smsListView, Activity activity, String contacts)
	{
		this.smsListView=smsListView;
		this.activity=activity;
   	 	this.contacts=contacts;
	     
	     updateContactListeView();
	}

	SmsListMgr(LinearLayout smsListView, Activity activity)
	{
		this.smsListView=smsListView;
		this.activity=activity;
		
   	 	SharedPreferences sharedPref = activity.getSharedPreferences("com.patbul.ffecompet.preferences", 0);
	    SmsListMgr.contactsLookup_keyString=sharedPref.getString("CONTACTS",SmsListMgr.contactsLookup_keyString);
	    this.contacts=SmsListMgr.contactsLookup_keyString;
	     
	     updateContactListeView();
	}

	public void updateContactListeView()
    {
		
		Log.d("SmsListMgr", "updateContactListeView contacts" + contacts);
    	smsListView.removeViews(0, smsListView.getChildCount());

	     
	     if (!contacts.isEmpty())
	     {
	    	 String[] contactsLookup_keys = contacts.split(";");
	     
        
	    	 for (int i=0; i<contactsLookup_keys.length; i++)
	    	 {
	    	    Log.d("SmsListMgr", "contactsLookup_keys[i]" + contactsLookup_keys[i]);
	    	    
	    	    Cursor contact = activity.getContentResolver().query(
	    	            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactsLookup_keys[i]),
	    	            null,
	    	            null,
	    	            null,
	    	            null
	    	    );
	    	    
	    	    if (contact == null)
	    	    {
	    	    	delSms(contactsLookup_keys[i]); 
	    	    }

	    	    else
	    	    {
	    	    	contact.moveToFirst();
	    	    	String id =contact.getString(contact.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
	    	    	String name = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    		 
	    	    	View child = activity.getLayoutInflater().inflate(R.layout.sms_layout, null);
	    	    	TextView numSmsView = (TextView)child.findViewById(R.id.smsNumView);
	    	    	ImageView smsDelView = (ImageView)child.findViewById(R.id.imageDeleteSms);
	    	    	smsDelView.setTag(contactsLookup_keys[i]);
	    	    	numSmsView.setText(name);
					numSmsView.setTextColor(this.activity.getResources().getColor(R.color.noir));
	    	    	smsListView.addView(child);
	    	    }
	    	 }
	     }
	     
    }
		
	
	public void updateContactListeView(String newcontacts)
    {
    	this.contacts=newcontacts;
    	
    	updateContactListeView();
    }

    public void delSms(String lookup) 
    {
    	Log.d("SmsListMgr", "delSms lookup : " + lookup);
    	Log.d("SmsListMgr", "addSms old contacts : " + contacts);
    	contacts = contacts.replace((String)(lookup), "");
    	contacts = contacts.replace(";;", ";");
   	    if (contacts.startsWith(";")) contacts=contacts.substring(1);
   	    if (contacts.endsWith(";")) contacts=contacts.substring(0, contacts.length()-1);
    	Log.d("SmsListMgr", "contacts res : " + contacts);
    	
    	Log.d("SmsListMgr", "addSms new contacts : " + contacts);
    	updateContactListeView();
    }
	
    public void addSms(Intent intent)
    {
    	Log.d("SmsListMgr", "addSms old contacts : " + contacts);
        Uri contactData = intent.getData();
		ContentResolver contentResolver = activity.getContentResolver();
		Cursor c = contentResolver.query(contactData, null, null, null, null);

        if (c.moveToFirst()) 
        {
            String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String key = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
            String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d(this.getClass().getName(), "key:"+key);
            if (hasPhone.equalsIgnoreCase("1")) 
            {
           	 	Cursor phones = contentResolver.query(
                          ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
                          null, null);
           	 	phones.moveToFirst();
           	 	String cNumber = phones.getString(phones.getColumnIndex("data1"));
           	 	Log.d(this.getClass().getName(), name + " : " + cNumber);
        	    
           	 	if (!contacts.isEmpty())
           	 	{
           	 		contacts = contacts+";";
           	 	}
           	 	contacts = contacts+key;
           	
           	 	updateContactListeView();
           	 	
            }
            else
            {
            	AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            	alertDialog.setTitle("Setup ...");
            	alertDialog.setMessage("Ce contact n'a pas de num�ro de t�l�phone");
            	alertDialog.setCancelable(false);
            	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok",new DialogInterface.OnClickListener() 
            	{
            		public void onClick(DialogInterface dialog,int id) 
            		{
            			dialog.cancel();
            		}
   			  	});
            	alertDialog.show();

            }
        }

   	
    	Log.d("SmsListMgr", "addSms new contacts : " + contacts);
    	updateContactListeView();
    }

    

    public String getContactList()
    {
    	return this.contacts;
    }

    public void saveSmsAsDefault() 
    {
    	Log.d("SmsListMgr", "saveSmsAsDefault contacts : " + contacts);
    	SmsListMgr.contactsLookup_keyString=contacts;
 
    	SharedPreferences sharedPref = activity.getSharedPreferences("com.patbul.ffecompet.preferences", 0);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString("CONTACTS", SmsListMgr.contactsLookup_keyString);
        editor.commit();
    }





}
