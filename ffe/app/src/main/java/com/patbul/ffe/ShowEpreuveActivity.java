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

public class ShowEpreuveActivity extends Activity
{
	String numConcours;
	String eprNum;
 
    private LinearLayout smsListView;
    SmsListMgr smsmgr;
    static final int PICK_CONTACT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_epreuve);

        final Intent intent = getIntent();
        this.numConcours = intent.getStringExtra(MainActivity.CONCOURS_TO_SHOW);
        TextView tv1 = (TextView)findViewById( R.id.concoursNumView );
        tv1.setText(this.numConcours);

        eprNum = intent.getStringExtra(MainActivity.EPREUVE_TO_SHOW);
        TextView tv2 = (TextView)findViewById( R.id.epreuveNumView );
        tv2.setText(eprNum);

        ListConcoursDB listeConc = new ListConcoursDB(this);
        Cursor c = listeConc.readAnEpreuve(this.numConcours, eprNum);
        if (c.getCount()>0)
        {
        	c.moveToFirst();
        	
        	String num = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ID_COLUM_RANK);
        	String numEpr = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_NUM_COLUM_RANK);
        	String etat = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK);
        	String organisateur = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ORGANISATEUR_COLUM_RANK);
        	String intitule = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_INTITULE_COLUM_RANK);
        	int nbMaxPlaces = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_MAX_COLUM_RANK);
        	int nbPlacesPrises = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT_COLUM_RANK);
        	String date = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_DATE_COLUM_RANK);
        	String comment = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK);
        	int evt = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK);
        	String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_SMS_LIST_RANK);
 
        	TextView numConc = (TextView)findViewById( R.id.concoursNumView );
            numConc.setText(num);
            TextView numEprView = (TextView)findViewById( R.id.epreuveNumView);
            numEprView.setText(numEpr);
            TextView etatConc = (TextView)findViewById(R.id.etatView );
            etatConc.setText(etat);
            TextView orgaConc = (TextView)findViewById(R.id.concoursOrganisateur );
            orgaConc.setText(organisateur);
            TextView dateConc = (TextView)findViewById(R.id.dateView );
            dateConc.setText(date);
            TextView commentConc = (TextView)findViewById(R.id.commentaire );
            commentConc.setText(comment);
            TextView intituleConc = (TextView)findViewById(R.id.epreuveIntitule);
            intituleConc.setText(intitule);
            TextView nbPlaceView = (TextView)findViewById(R.id.nbPlaces);
            nbPlaceView.setText(nbPlacesPrises+"/"+nbMaxPlaces);
            
            listeConc.rmEvtEpreuve(num, numEpr);
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
    	TextView eprNum = (TextView) findViewById(R.id.epreuveNumView);
        String stringEprConc = eprNum.getText().toString();
        Log.d(this.getClass().getName(), "sup epreuve : " + stringNumConc + " " + stringEprConc);
        
        ListConcoursDB listeConc = new ListConcoursDB(this);
        listeConc.delAnEpreuve(stringNumConc, stringEprConc);
        
        
        Log.d(this.getClass().getName(), "envoi intent pour maj widget : ");
        Intent intent = new Intent(this,FfeWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, FfeWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
        
        finish();
       
    }
    
    public void reacClickHandler(View view) 
    {
    	TextView concNum = (TextView) findViewById(R.id.concoursNumView);
        String stringNumConc = concNum.getText().toString();
    	TextView eprNum = (TextView) findViewById(R.id.epreuveNumView);
        String stringEprConc = eprNum.getText().toString();
        Log.d(this.getClass().getName(), "sup epreuve : " + stringNumConc + " " + stringEprConc);

        ListConcoursDB listeConc = new ListConcoursDB(this);
        listeConc.setEpreuveState(stringNumConc, stringEprConc, "Complet");
 
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
   	 	listeConc.updateSmsListeEpreuve(this.numConcours,this.eprNum,smsmgr.getContactList());
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

        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                smsmgr.addSms(data);
                ListConcoursDB listeConc = new ListConcoursDB(this);
                listeConc.updateSmsListeEpreuve(this.numConcours, this.eprNum, smsmgr.getContactList());
            }
        }

    }



}
