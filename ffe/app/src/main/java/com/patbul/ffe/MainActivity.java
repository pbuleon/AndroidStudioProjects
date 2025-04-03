package com.patbul.ffe;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity{

    public static final String CONCOURS_TO_SHOW = "com.patbull.ffecompet.concourstoshow";
    public static final String EPREUVE_TO_SHOW = "com.patbull.ffecompet.epreuvetoshow";
    private LinearLayout concousListView;
    private LinearLayout epreuvesListView;

    FfeServiceThread thread=null;;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.concousListView = (LinearLayout) findViewById(R.id.concoursListView);
        this.epreuvesListView = (LinearLayout) findViewById(R.id.epreuvesListView);

        SharedPreferences sharedPref = this.getSharedPreferences("com.patbul.ffecompet.preferences", 0);
        FfeWidget.PERIODE=sharedPref.getInt("PERIODE", FfeWidget.PERIODE);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

//      EventReceiver.SetActivity(this);

        Log.d(this.getClass().getName(), "onStart");
//      Intent mIntent = new Intent(this, FfeCompetService.class);
//      bindService(mIntent, mConnexion, BIND_AUTO_CREATE);
//      mBound = true;
//      Log.d(this.getClass().getName(), "bindService fai t");

        thread = new FfeServiceThread(this);
        thread.start();

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        updateViewConcours();

    }


    public void updateViewConcours()
    {
        ListConcoursDB listeConc = new ListConcoursDB(this);
        Cursor c = listeConc.readAllConcours();
        this.concousListView.removeViews(0, this.concousListView.getChildCount());
        Log.d(this.getClass().getName(), "nb concours : " + c.getCount());
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            String num = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ID_COLUM_RANK);
            String etat = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ETAT_COLUM_RANK);
            String organisateur = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ORGANISATEUR_COLUM_RANK);
            String date = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_DATE_COLUM_RANK);
            String comment = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_COMMENTAIRE_COLUM_RANK);
            int evt = c.getInt(ListConcoursDB.COLUMN_NAME_CONCOURS_EVENT_COLUM_RANK);
            String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_SMS_LIST_RANK);
            Log.d(this.getClass().getName(), "concours : " + num + " etat: " + etat);
            View child = getLayoutInflater().inflate(R.layout.concours_layout, null);
            TextView numConc = (TextView)child.findViewById(R.id.concoursNumView);
            numConc.setText(num);
            TextView etatConc = (TextView)child.findViewById(R.id.etatView );
            etatConc.setText(etat);
            switch(etat){
                case ConcoursReader.UNKNOWN_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursAvantOuverture));
                    break;
                case ConcoursReader.OUVERT_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursOuvert));
                    break;
                case ConcoursReader.EN_COURS_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursClos));
                    break;
                case ConcoursReader.TERMINE_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursClos));
                    break;
                case ConcoursReader.AVANT_PROGRAMME_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursAvantOuverture));
                    break;
                case ConcoursReader.CALENDRIER_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursAvantOuverture));
                    break;
                case ConcoursReader.ANNULE_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursClos));
                    break;
                case ConcoursReader.CLOS_STATE:
                    etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursClos));
                    break;
                default:

            }


            TextView orgaConc = (TextView)child.findViewById(R.id.commentaire );
            orgaConc.setText(comment);
            TextView dateConc = (TextView)child.findViewById(R.id.dateView );
            dateConc.setText(date);

            // en gars pour les event
            Log.d(this.getClass().getName(),"evt :"+evt);
            if (evt!=0)
            {
                Log.d(this.getClass().getName(),"evt gras");
                SpannableString spanString = new SpannableString(etatConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                etatConc.setText(spanString);

                spanString = new SpannableString(numConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                numConc.setText(spanString);

                spanString = new SpannableString(orgaConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                orgaConc.setText(spanString);

                spanString = new SpannableString(dateConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                dateConc
                        .setText(spanString);
            }
            this.concousListView.addView(child);

            c.moveToNext();
        }


        c = listeConc.readAllepreuves();
        this.epreuvesListView.removeViews(0, this.epreuvesListView.getChildCount());
        Log.d(this.getClass().getName(), "nb epreuve : " + c.getCount());
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            String numConc = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ID_COLUM_RANK);
            String numEpr = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_NUM_COLUM_RANK);
            String etat = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK);
            String organisateur = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ORGANISATEUR_COLUM_RANK);
            String intitule = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_INTITULE_COLUM_RANK);
            int nbPlaceMax = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_MAX_COLUM_RANK);
            int nbPlacePrise = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT_COLUM_RANK);
            String date = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_DATE_COLUM_RANK);
            String comment = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK);
            int evt = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK);
            String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_SMS_LIST_RANK);

            Log.d(this.getClass().getName(), "epreuve : " + numConc + " " + numEpr + " etat: " + etat);
            View child = getLayoutInflater().inflate(R.layout.epreuves_layout, null);
            TextView numConcView = (TextView)child.findViewById(R.id.concoursNumView);
            numConcView.setText(numConc);
            TextView numEprView = (TextView)child.findViewById(R.id.epreuveNumView);
            numEprView.setText(numEpr);
            TextView etatConc = (TextView)child.findViewById(R.id.etatView );
            etatConc.setText(etat);
            if (etat.compareTo(ConcoursReader.DISPO)==0)
            {
                etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursOuvert));
            }
            else
            {
                etatConc.setBackgroundColor(this.getResources().getColor(R.color.concoursClos));
            }

            TextView commentView = (TextView)child.findViewById(R.id.commentaire );
            commentView.setText(comment);
            TextView dateConc = (TextView)child.findViewById(R.id.dateView );
            dateConc.setText(date);

            // en gars pour les event
            Log.d(this.getClass().getName(),"evt :"+evt);
            if (evt!=0)
            {
                Log.d(this.getClass().getName(),"evt gras");
                SpannableString spanString = new SpannableString(etatConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                etatConc.setText(spanString);

                spanString = new SpannableString(numConcView.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                numConcView.setText(spanString);

                spanString = new SpannableString(numEprView.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                numEprView.setText(spanString);

                spanString = new SpannableString(commentView.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                commentView.setText(spanString);

                spanString = new SpannableString(dateConc.getText());
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                dateConc
                        .setText(spanString);
            }
            this.epreuvesListView.addView(child);

            c.moveToNext();
        }

    }

    public void myClickHandler(View view) // clic sur un concours
    {
        LinearLayout lay = (LinearLayout) view;
        TextView tView = (TextView)lay.findViewById(R.id.concoursNumView);
        String NumConc =  tView.getText().toString();
        Log.d(this.getClass().getName(), "clique concours : " + NumConc);

        Intent intent = new Intent(this, ShowConcoursActivity.class);
        intent.putExtra(CONCOURS_TO_SHOW, NumConc);
        startActivity(intent);
        return;

    }

    public void myClickHandlerEpreuve(View view) // clic sur une epreuve
    {
        LinearLayout lay = (LinearLayout) view;
        TextView tView = (TextView)lay.findViewById(R.id.concoursNumView);
        String NumConc =  tView.getText().toString();
        TextView ttView = (TextView)lay.findViewById(R.id.epreuveNumView);
        String NumEpr =  ttView.getText().toString();
        Log.d(this.getClass().getName(), "clique epreuve : " + NumConc + " " + NumEpr);

        Intent intent = new Intent(this, ShowEpreuveActivity.class);
        intent.putExtra(CONCOURS_TO_SHOW, NumConc);
        intent.putExtra(EPREUVE_TO_SHOW, NumEpr);
        startActivity(intent);
        return;

    }

    public void addConcoursHandler(View view) // clic sur le PLUS des concours
    {
        addConcours();
    }

    public void setupHandler(View view) // clic sur le PLUS des concours
    {
        setup();
    }

    public void addEpreuveHandler(View view) // clic sur le PLUS des concours
    {
        addEpreuve();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings) return setup();
        if (id == R.id.addConcours) return addConcours();
        if (id == R.id.addEpreuve) return addEpreuve();
        return super.onOptionsItemSelected(item);

    }

    private boolean setup()
    {
        Log.d(this.getClass().getName(), "addConcours");
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        return true;
    }
    private boolean addConcours()
    {
        Log.d(this.getClass().getName(), "addConcours");
        Intent intent = new Intent(this, AddConcoursActivity.class);
        startActivity(intent);
        return true;
    }
    private boolean addEpreuve()
    {
        Log.d(this.getClass().getName(), "addEpreuve");
        Intent intent = new Intent(this, AddEpreuveActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //   	if(mBound)
        //   	{
        //   		unbindService(mConnexion);
        //   		mBound = false;
        //   	}
        if (thread != null) thread.arrete();
        thread=null;
    }

    public void updateViewNoNetwork(boolean ok)
    {
        if (ok)
        {
            ImageView im = (ImageView) findViewById(R.id.network);
            im.setImageDrawable(getResources().getDrawable(R.drawable.logoffeok));
        }
        else
        {
            ImageView im = (ImageView) findViewById(R.id.network);
            im.setImageDrawable(getResources().getDrawable(R.drawable.logoffeko));
        }

    }






}
