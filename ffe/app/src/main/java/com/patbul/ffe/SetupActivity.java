package com.patbul.ffe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetupActivity extends Activity
{
    private LinearLayout smsListView;
    
    static final int PICK_CONTACT = 0;
    
    SmsListMgr smsmgr;

    FfeInscription ffeInscription;
    

	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        ffeInscription = new FfeInscription(this);

        setContentView(R.layout.activity_setup);
        TextView nbMin = (TextView) findViewById(R.id.nbMinutes);
        String t = ""+FfeWidget.PERIODE;
        nbMin.setText(t);

        TextView ffeLogin = findViewById(R.id.compteFFE);
        ffeLogin.setText(ffeInscription.getLogin());
        
        smsListView=(LinearLayout)findViewById(R.id.smsListeView);
        smsmgr = new SmsListMgr(smsListView, this);

        EditText commpteFFEText = findViewById(R.id.compteFFE);
        commpteFFEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCompteFFEMofification();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText passwordFFEText = findViewById(R.id.passwordFFE);
        passwordFFEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onCompteFFEMofification();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onCompteFFEMofification(){
        findViewById(R.id.testFFE).setEnabled(true);
    }
    public void testCompteFFE(View view)
    {
        TextView compte = findViewById(R.id.compteFFE);
        TextView pw = findViewById(R.id.passwordFFE);
        boolean ok = ffeInscription.testCompte(compte.getText().toString(),pw.getText().toString());
        if (ok) {
            findViewById(R.id.testFFE).setEnabled(false);
            ffeInscription.saveCompte(compte.getText().toString(),pw.getText().toString());

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Test compte FFE ...");
            alertDialog.setMessage("La connexion au compte FFE a échoué");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok",new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                    dialog.cancel();
                }
            });
            alertDialog.show();
            return;
        }
    }
    
    public void myClickHandler(View view) 
    {
    	TextView nbMin = (TextView) findViewById(R.id.nbMinutes);
        String stringnbMin = nbMin.getText().toString();
        if (stringnbMin.length()==0)
        {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Setup ...");
        	alertDialog.setMessage("Le nombre de minutes doit �tre valoris�");
        	alertDialog.setCancelable(false);
        	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok",new DialogInterface.OnClickListener() 
        	{
				public void onClick(DialogInterface dialog,int id) 
				{
					dialog.cancel();
				}
			  });
        	alertDialog.show();
        	return;
        }
        int minutes = Integer.parseInt(stringnbMin);
        if (minutes==0)
        {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Setup ...");
        	alertDialog.setMessage("Le nombre de minutes doit �tre non nul");
        	alertDialog.setCancelable(false);
        	alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok",new DialogInterface.OnClickListener() 
        	{
				public void onClick(DialogInterface dialog,int id) 
				{
					dialog.cancel();
				}
			  });
        	alertDialog.show();
        	return;
        }
        FfeWidget.ChangePeriode(minutes,this.getApplicationContext());
        
	    SharedPreferences sharedPref = this.getSharedPreferences("com.patbul.ffecompet.preferences", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("PERIODE", minutes);
        editor.commit();

        finish();
        
       
    }
    
    public void delSmsHandler(View view) 
    {
    	Log.d(this.getClass().getName(), "delSmsHandler id : " + view.getTag());
    	smsmgr.delSms((String)(view.getTag()));
    	smsmgr.saveSmsAsDefault();
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
            	 smsmgr.saveSmsAsDefault();
             }
           	break;
        }

    }
    
    

}
