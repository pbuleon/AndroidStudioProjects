package com.patbul.ffe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class FfeWidget extends AppWidgetProvider
{
	
	public static int PERIODE = 5;
	

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
    {
        final int N = appWidgetIds.length;
        
        Log.d(this.getClass().getName(), "onUpdate : ");
        
        
         // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) 
        {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) 
    {
        Log.d("FfeWidget", "updateAppWidget : ");

        ListConcoursDB listeConc = new ListConcoursDB(context);
        Cursor c = listeConc.readAllConcours();
    	Log.d("FfeWidget", "nb concours : " + c.getCount());
    	c.moveToFirst();
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ffe);
    	
		Intent widgetIntent = new Intent(context, MainActivity.class);
		PendingIntent widgetPendingIntent = PendingIntent.getActivity(context, 0, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //  Attach an on-click listener to the event
        views.setOnClickPendingIntent(R.id.widget_ffe, widgetPendingIntent);

    	
    	
    	int i=0;
    	while ((!c.isAfterLast())&&(i<4)) 
    	{
    		
    		String num = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ID_COLUM_RANK);
    		String etat = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ETAT_COLUM_RANK);
    		String organisateur = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ORGANISATEUR_COLUM_RANK);
    		String date = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_DATE_COLUM_RANK);
    		String comment = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_COMMENTAIRE_COLUM_RANK);
    		int evt = c.getInt(ListConcoursDB.COLUMN_NAME_CONCOURS_EVENT_COLUM_RANK);
    		String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_SMS_LIST_RANK);
    		
    		if (evt==ListConcoursDB.EVENT_OUVERT)
    		{
    			i++;
    			int etatId=0;
    			switch(i)
    			{
    			case 1:
    				etatId=R.id.evtText_1;
    				break;
    			case 2:
    				etatId=R.id.evtText_2;
    				break;
    			case 3:
    				etatId=R.id.evtText_3;
    				break;
    			case 4:
    				etatId=R.id.evtText_4;
    				break;
    			}
    		
    			String text = "Concours Ouvert : " + comment;
    			views.setTextViewText(etatId, text);
  
    			Intent resultIntent = new Intent(context, ShowConcoursActivity.class);
    			resultIntent.putExtra(MainActivity.CONCOURS_TO_SHOW, num);
    			PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	        //  Attach an on-click listener to the event
    	        views.setOnClickPendingIntent(etatId, resultPendingIntent);

    		}
    		
    		Log.d("FfeWidget", "concours : " + num + " etat: " + etat);
            
    		c.moveToNext();
    		
    	}
    	
    	
        c = listeConc.readAllepreuves();
    	Log.d("FfeWidget", "nb epreuve : " + c.getCount());
    	c.moveToFirst();
    	
    	while ((!c.isAfterLast())&&(i<4)) 
    	{
    		
    		String numConc = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ID_COLUM_RANK);
    		String numEpr = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_NUM_COLUM_RANK);
    		String etat = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK);
    		String comment = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK);
    		int evt = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK);
    		String smsListString = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_SMS_LIST_RANK);
    		
    		if (evt==ListConcoursDB.EVENT_OUVERT)
    		{
    			i++;
    			int etatId=0;
    			switch(i)
    			{
    			case 1:
    				etatId=R.id.evtText_1;
    				break;
    			case 2:
    				etatId=R.id.evtText_2;
    				break;
    			case 3:
    				etatId=R.id.evtText_3;
    				break;
    			case 4:
    				etatId=R.id.evtText_4;
    				break;
    			}
    		
    			String text = "Places dispos : " + comment;
    			views.setTextViewText(etatId, text);
  
    			Intent resultIntent = new Intent(context, ShowEpreuveActivity.class);
    			resultIntent.putExtra(MainActivity.CONCOURS_TO_SHOW, numConc);
    			resultIntent.putExtra(MainActivity.EPREUVE_TO_SHOW, numEpr);
    			PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	        //  Attach an on-click listener to the event
    	        views.setOnClickPendingIntent(etatId, resultPendingIntent);

    		}
    		
    		c.moveToNext();
    		
    	}

    	while (i<4)
    	{
    		i++;
    		int etatId=0;
    		switch(i)
    		{
    		case 1:
    			etatId=R.id.evtText_1;
    			break;
    		case 2:
    			etatId=R.id.evtText_2;
    			break;
    		case 3:
    			etatId=R.id.evtText_3;
    			break;
    		case 4:
    			etatId=R.id.evtText_4;
    			break;
    		}
    		
    		views.setTextViewText(etatId, "");
    	}
   	

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    static void setNetworkOK(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
    	Log.d("FfeWidget", "setNetworkOK : ");
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ffe);
    	views.setImageViewResource(R.id.network, R.drawable.logoffeok);
        appWidgetManager.updateAppWidget(appWidgetId, views);
   }

    static void setNetworkKO(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
    	Log.d("FfeWidget", "setNetworkKO : ");
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ffe);
    	views.setImageViewResource(R.id.network, R.drawable.logoffeko);
        appWidgetManager.updateAppWidget(appWidgetId, views);
   }
    
	 @Override
	 public void onDeleted(Context context, int[] appWidgetIds) 
	 {
		 Log.d("FfeWidget", "onDeleted : ");
		 super.onDeleted(context, appWidgetIds);
	 }
	 
	 @Override
	 public void onDisabled(Context context) 
	 {
		 Log.d("FfeWidget", "onDisabled : ");
		 Intent intent = new Intent(context, FfeBroadcastReceiver.class);
		 PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		 AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		 alarmManager.cancel(sender);
		 super.onDisabled(context);
	 }

	 @Override
	 public void onEnabled(Context context) 
	 {
		 super.onEnabled(context);
		 Log.d("FfeWidget", "onEnabled : ");
		 
	     SharedPreferences sharedPref = context.getSharedPreferences("com.patbul.ffecompet.preferences", 0);
	     FfeWidget.PERIODE=sharedPref.getInt("PERIODE", FfeWidget.PERIODE);

		 
		 
		 AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		 Intent timerIntent = new Intent(context, FfeBroadcastReceiver.class);
		 PendingIntent pi = PendingIntent.getBroadcast(context, 0, timerIntent, 0);
		 //After after 3 seconds
		 am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 1000 * 3, 60000 , pi); 
	 }
	 
	 public static void ChangePeriode(int minutes,Context context)
	 {
		 PERIODE=minutes;
	 }
	 

	 

	 

    
}
