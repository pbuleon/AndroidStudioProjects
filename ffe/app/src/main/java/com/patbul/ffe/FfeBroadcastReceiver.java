package com.patbul.ffe;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

public class FfeBroadcastReceiver extends BroadcastReceiver
{
	static PrivThread thread =null;
	
	static int nbMinutes = FfeWidget.PERIODE;
	private class PrivThread extends Thread
	{
		private Context cont;
		
		PrivThread()
		{
			Log.d (this.getClass().getName(),"PrivThread :");
		}
		public void setContext(Context context)
		{
			cont=context;
			Log.d (this.getClass().getName(),"setContext :");
		}
		
		public void run()
		{
			Log.d (this.getClass().getName(),"run :");
			int res = ConcoursReader.UpdateConcours(cont);
			ComponentName thiswidget = new ComponentName(cont, FfeWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(cont);
			int[] ids = manager.getAppWidgetIds(thiswidget);
			int N = ids.length;

			switch (res)
			{
			    case ConcoursReader.RIEN:
                case ConcoursReader.EVOLUTION_CONCOURS:
                    for (int i = 0; i < N; i++)
			        {
			            int appWidgetId = ids[i];
			            FfeWidget.updateAppWidget(cont, manager, appWidgetId);
			            FfeWidget.setNetworkOK(cont, manager, appWidgetId);
			        }
					break;
			    case ConcoursReader.RESEAU_KO:
			        for (int i = 0; i < N; i++) 
			        {
			            int appWidgetId = ids[i];
			            FfeWidget.updateAppWidget(cont, manager, appWidgetId);
			            FfeWidget.setNetworkKO(cont, manager, appWidgetId);
			        }
					break;


            }
			Log.d (this.getClass().getName(),"run fini:");
		}
			

	}
	 @Override
	 public void onReceive(Context context, Intent intent) 
	 {
		 Log.d (this.getClass().getName(),"onReceive :");

		 if (intent.getAction() != null &&
				 intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			 // Pour Android 8.0+, vous devez démarrer le service en premier plan
			 Intent serviceIntent = new Intent(context, FFEServiceForeGround.class);

             context.startForegroundService(serviceIntent);
			 return;
         }



		 nbMinutes++;
		 if (nbMinutes < FfeWidget.PERIODE) return;
		 
		 nbMinutes = 0;
		 
		 if ((thread==null)||(thread.getState()==Thread.State.TERMINATED))
		 {
			 thread= new PrivThread();
			 thread.setContext(context);
			 thread.start();
		 }
	 }

}
