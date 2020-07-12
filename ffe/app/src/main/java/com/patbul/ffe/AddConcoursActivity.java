package com.patbul.ffe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddConcoursActivity extends Activity
{
    private LinearLayout smsListView;
    
    SmsListMgr smsmgr;
    static final int PICK_CONTACT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concours);

        smsListView=(LinearLayout)findViewById(R.id.smsListeView);
        smsmgr = new SmsListMgr(smsListView, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_concours_menu, menu);
        return true;
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void myClickHandler(View view) 
    {
    	TextView concNum = (TextView) findViewById(R.id.numConcours);
        String stringNumConc = concNum.getText().toString();
    	TextView concComment = (TextView) findViewById(R.id.commentaire);
        String stringconcComment = concComment.getText().toString();
        Log.d(this.getClass().getName(), "nouv concours : " + stringNumConc);
        if (stringNumConc.length()!=9)
        {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Ajout ...");
        	alertDialog.setMessage("Le numero doit contenir 9 chiffres");
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
        else
        {
            ListConcoursDB listeConc = new ListConcoursDB(this);
            listeConc.newConcours(stringNumConc, ConcoursReader.UNKNOWN_STATE, ConcoursReader.UNKNOWN_STATE, ConcoursReader.UNKNOWN_DATE,stringconcComment,smsmgr.getContactList());
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Ajout ...");
        	alertDialog.setMessage("Concours ajouté");
        	alertDialog.setCancelable(false);
        	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok",new DialogInterface.OnClickListener() 
        	{
				public void onClick(DialogInterface dialog,int id) 
				{
					finish();
				}
			  });
        	alertDialog.show();
        }
       
    }
    
    public void delSmsHandler(View view) 
    {
    	Log.d(this.getClass().getName(), "delSmsHandler id : " + view.getTag());
    	smsmgr.delSms((String)(view.getTag()));
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
             }
           	break;
        }

    }


}
