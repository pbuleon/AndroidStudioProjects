package com.patbul.ffe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ShowConcoursActivity extends Activity
{
    String numConcours;
    
    private LinearLayout smsListView;
    SmsListMgr smsmgr;
    static final int PICK_CONTACT = 0;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_concours);
        
        final Intent intent = getIntent();
        this.numConcours = intent.getStringExtra(MainActivity.CONCOURS_TO_SHOW);
        TextView tv1 = (TextView)findViewById( R.id.concoursNumView );
        tv1.setText(this.numConcours);
        
        ListConcoursDB listeConc = new ListConcoursDB(this);
        Cursor c = listeConc.readAConcours(this.numConcours);
        if (c.getCount()>0)
        {
        	c.moveToFirst();
    		String num = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ID_COLUM_RANK);
    		String etat = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ETAT_COLUM_RANK);
    		String organisateur = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ORGANISATEUR_COLUM_RANK);
    		String date = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_DATE_COLUM_RANK);
    		String comment = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_COMMENTAIRE_COLUM_RANK);
    		int evt = c.getInt(ListConcoursDB.COLUMN_NAME_CONCOURS_EVENT_COLUM_RANK);
    		String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_SMS_LIST_RANK);
            TextView numConc = (TextView)findViewById( R.id.concoursNumView );
            numConc.setText(num);
            TextView etatConc = (TextView)findViewById(R.id.etatView );
            etatConc.setText(etat);
            TextView orgaConc = (TextView)findViewById(R.id.concoursOrganisateur );
            orgaConc.setText(organisateur);
            TextView dateConc = (TextView)findViewById(R.id.dateView );
            dateConc.setText(date);
            TextView commentConc = (TextView)findViewById(R.id.commentaire );
            commentConc.setText(comment);
            
            listeConc.rmEvtConcours(num);
            Log.d(this.getClass().getName(), "envoi intent pour maj widget : ");
            Intent intent2 = new Intent(this,FfeWidget.class);
            intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, FfeWidget.class));
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent2);

            smsListView=(LinearLayout)findViewById(R.id.smsListeView);
            smsmgr = new SmsListMgr(smsListView, this, smsListString);
   	}
        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_concours_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void supClickHandler(View view) 
    {
    	TextView concNum = (TextView) findViewById(R.id.concoursNumView);
        String stringNumConc = concNum.getText().toString();
        Log.d(this.getClass().getName(), "sup concours : " + stringNumConc);
        
        ListConcoursDB listeConc = new ListConcoursDB(this);
        listeConc.delAConcours(stringNumConc);
        
        
        Log.d(this.getClass().getName(), "envoi intent pour maj widget : ");
        Intent intent = new Intent(this,FfeWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, FfeWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
        
        finish();
       
    }
    
    public void ouvrirClickHandler(View view) 
    {
    	final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://ffecompet.ffe.com/concours/"+this.numConcours));
    	this.startActivity(intent);
    }
    
    public void delSmsHandler(View view) 
    {
    	Log.d(this.getClass().getName(), "delSmsHandler id : " + view.getTag());
    	smsmgr.delSms((String)(view.getTag()));
        ListConcoursDB listeConc = new ListConcoursDB(this);
        listeConc.updateSmsListeConcours(numConcours, smsmgr.getContactList());
    }

    public void addSmsHandler(View view) 
    {
    	Log.d(this.getClass().getName(), "addSmsHandler");
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }
 
    @Override 
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    { 
        super.onActivityResult(reqCode, resultCode, data);

        switch(reqCode)
        {
           case (PICK_CONTACT):
             if (resultCode == Activity.RESULT_OK)
             {
            	 smsmgr.addSms(data);
                 ListConcoursDB listeConc = new ListConcoursDB(this);
                 listeConc.updateSmsListeConcours(numConcours, smsmgr.getContactList());
             }
           	break;
        }

    }


}
