package com.patbul.ffe;


import android.util.Log;

public class FfeImmediatUpdate extends Thread
{
	private static String URL_PREF="https://ffecompet.ffe.com/concours/";
	public static String FERME_STATE="Ferme";
	
	public static final String UPDATE = "update";
	public static final String NO_NETWORK = "noNetwork";
	public static final String NETWORK_OK = "networkOk";
	public static final String NOTIFICATION = "com.patbul.ffecompet.update";
	
	public static final String POST_BODY = "ctl0$contentFFE$CPT_Criteres$TXT_NumConcours=201581001&ctl0$contentFFE$CPT_Criteres$ADL_TypePeriode=saisie&ctl0$contentFFE$CPT_Criteres$DPR_DateDebut=&ctl0$contentFFE$CPT_Criteres$DPR_DateFin=&ctl0$contentFFE$CPT_Criteres$CBL_Licences$c0=club&ctl0$contentFFE$CPT_Criteres$CBL_Licences$c1=amateur&ctl0$contentFFE$CPT_Criteres$CBL_Licences$c2=pro&ctl0$contentFFE$CPT_Criteres$TXT_Organisateur=&ctl0$contentFFE$CPT_Criteres$TXT_Lieu=&ctl0$contentFFE$CPT_Criteres$RBL_TypeRechercheLieu=commence_par&ctl0$contentFFE$CPT_Criteres$CPT_Map$typeSelection=ctl0$contentFFE$CPT_Criteres$CPT_Map$RBT_Departement&PRADO_PAGESTATE=eJwFwYENwCAIBMBdHKARfEHsNIC4QPdPeudDPVlwLspvwjVLC0uihzAb8zohvb5NczfCmAIz0EOi1t4fOQ4QXg==&PRADO_CALLBACK_TARGET=ctl0$contentFFE$CPT_Criteres$ALB_RechercheSimple&PRADO_POSTBACK_TARGET=ctl0$contentFFE$CPT_Criteres$ALB_RechercheSimple";

	private MainActivity activite=null;
	
	FfeImmediatUpdate(MainActivity mainActivity)
	{
		activite=mainActivity;
	}
	
	public void run()
	{
		
		try 
		{
			int res = ConcoursReader.RESEAU_KO;
			while ((!this.isInterrupted())&&(res == ConcoursReader.RESEAU_KO))
			{
				Log.d(this.getClass().getName(), "run");
				res = ConcoursReader.UpdateConcours(activite);
				switch (res)
				{
				    case ConcoursReader.RIEN:
						activite.runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								activite.updateViewNoNetwork(true);
							}
						});
						break;
				    case ConcoursReader.RESEAU_KO:
						activite.runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								activite.updateViewNoNetwork(false);
							}
						});
						break;
				    case ConcoursReader.EVOLUTION_CONCOURS:
						activite.runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								activite.updateViewConcours();
								activite.updateViewNoNetwork(true);
							}
						});
						break;

					
				}
				Thread.sleep(60000);
			}
		} 
		catch (InterruptedException e) 
		{
		}
			
		Log.d(this.getClass().getName(), "stop");
	}
	
	public void arrete()
	{
		Log.d(this.getClass().getName(), "arrete");
		this.interrupt();
		Log.d(this.getClass().getName(), "arrete fait");
	}
	
}
