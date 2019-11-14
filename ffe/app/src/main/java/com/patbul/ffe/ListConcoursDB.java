package com.patbul.ffe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class ListConcoursDB extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ffecompet.db";
    
    public static final int COLUMN_NAME_CONCOURS_ID_COLUM_RANK = 1;
    public static final int COLUMN_NAME_CONCOURS_ETAT_COLUM_RANK = 2;
    public static final int COLUMN_NAME_CONCOURS_ORGANISATEUR_COLUM_RANK = 3;
    public static final int COLUMN_NAME_CONCOURS_DATE_COLUM_RANK = 4;
    public static final int COLUMN_NAME_CONCOURS_COMMENTAIRE_COLUM_RANK = 5;
    public static final int COLUMN_NAME_CONCOURS_EVENT_COLUM_RANK = 6;
    public static final int COLUMN_NAME_CONCOURS_SMS_LIST_RANK = 7;


    public static final int COLUMN_NAME_EPREUVES_ID_COLUM_RANK = 1;
    public static final int COLUMN_NAME_EPREUVES_NUM_COLUM_RANK = 2;
    public static final int COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK = 3;
    public static final int COLUMN_NAME_EPREUVES_ORGANISATEUR_COLUM_RANK = 4;
    public static final int COLUMN_NAME_EPREUVES_INTITULE_COLUM_RANK = 5;
    public static final int COLUMN_NAME_EPREUVES_NB_PLACE_MAX_COLUM_RANK = 6;
    public static final int COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT_COLUM_RANK = 7;
    public static final int COLUMN_NAME_EPREUVES_DATE_COLUM_RANK = 8;
    public static final int COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK = 9;
    public static final int COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK = 10;
    public static final int COLUMN_NAME_EPREUVES_SMS_LIST_RANK = 11;
    
    // event value
	public static final int EVENT_OUVERT = 1;
	public static final int EVENT_FERME = 0;



    //--------------------------- SQL	
	public static abstract class ConcoursEntry implements BaseColumns 
	{
        public static final String TABLE_NAME = "concours";
        public static final String COLUMN_NAME_CONCOURS_ID = "concoursid";
        public static final String COLUMN_NAME_CONCOURS_ETAT = "etat";
        public static final String COLUMN_NAME_CONCOURS_ORGANISATEUR = "organisateur";
        public static final String COLUMN_NAME_CONCOURS_DATE = "date";
        public static final String COLUMN_NAME_CONCOURS_COMMENTAIRE = "commentaire";
        public static final String COLUMN_NAME_CONCOURS_EVENT = "event";
        public static final String COLUMN_NAME_CONCOURS_SMS = "sms";
    }
	public static abstract class EpreuveEntry implements BaseColumns 
	{
        public static final String TABLE_NAME = "epreuves";
        public static final String COLUMN_NAME_EPREUVES_ID = "concoursid";
        public static final String COLUMN_NAME_EPREUVES_NUM = "epreuvenum";
        public static final String COLUMN_NAME_EPREUVES_ETAT = "etat";
        public static final String COLUMN_NAME_EPREUVES_ORGANISATEUR = "organisateur";
        public static final String COLUMN_NAME_EPREUVES_INTITULE = "intitule";
        public static final String COLUMN_NAME_EPREUVES_NB_PLACE_MAX = "placemax";
        public static final String COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT = "placecurrent";
        public static final String COLUMN_NAME_EPREUVES_DATE = "date";
        public static final String COLUMN_NAME_EPREUVES_COMMENTAIRE = "commentaire";
        public static final String COLUMN_NAME_EPREUVES_EVENT = "event";
        public static final String COLUMN_NAME_EPREUVES_SMS = "sms";
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_CONCOURS =
	    "CREATE TABLE " + ConcoursEntry.TABLE_NAME + " (" +
	    ConcoursEntry._ID + " INTEGER PRIMARY KEY," +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_ID + TEXT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT + TEXT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR + TEXT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_DATE + TEXT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_COMMENTAIRE + TEXT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT + INT_TYPE + COMMA_SEP +
	    ConcoursEntry.COLUMN_NAME_CONCOURS_SMS + TEXT_TYPE + 
	    " )";
	private static final String SQL_CREATE_EPREUVE =
		    "CREATE TABLE " + EpreuveEntry.TABLE_NAME + " (" +
		    EpreuveEntry._ID + " INTEGER PRIMARY KEY," +
		    EpreuveEntry.COLUMN_NAME_EPREUVES_ID + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX + INT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT + INT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_DATE + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_COMMENTAIRE + TEXT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT + INT_TYPE + COMMA_SEP +
			EpreuveEntry.COLUMN_NAME_EPREUVES_SMS + TEXT_TYPE +
		    " )";

	private static final String SQL_DELETE_CONCOURS =
	    "DROP TABLE IF EXISTS " + ConcoursEntry.TABLE_NAME;
	private static final String SQL_DELETE_EPREUVES =
		    "DROP TABLE IF EXISTS " + EpreuveEntry.TABLE_NAME;

	
    public ListConcoursDB(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL(SQL_CREATE_CONCOURS);
        db.execSQL(SQL_CREATE_EPREUVE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        db.execSQL(SQL_DELETE_CONCOURS);
        db.execSQL(SQL_DELETE_EPREUVES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        onUpgrade(db, oldVersion, newVersion);
    }
    
 
    
//-------------------------------------CONCOURS-------------------------------------    
    public void newConcours(String num, String etat, String organisateur,String date, String commentaire, String contactsKeyString)
    {
    	Log.d(this.getClass().getName(), "newConcours concours : contactsKeyString " + contactsKeyString );
    	if (checkAConcours(num))
    	{
    		Log.d(this.getClass().getName(), "newConcours concours : " + num + " deja present");
    		return;
    	}
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_ID, num);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT, etat);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR, organisateur);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_DATE,date);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_COMMENTAIRE,commentaire);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT,0);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_SMS,contactsKeyString);

    	long newRowId;
    	newRowId = db.insert(ConcoursEntry.TABLE_NAME, null, values);
    	if (newRowId==-1)
    	{
    		Log.d(this.getClass().getName(), "concours non ajoute dans db");
    	}
    	else
    	{
    		Log.d(this.getClass().getName(), "concours ajoute dans db");
    	}
    }
    public Cursor readAllConcours()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			ConcoursEntry._ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_DATE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_COMMENTAIRE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_SMS
    		    };

    	String sortOrder =
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " ASC";

    	
    	Cursor c = db.query(
    			ConcoursEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    null,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	return c;
    	
    }

    public Cursor readAConcours(String num)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			ConcoursEntry._ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_DATE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_COMMENTAIRE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_SMS
    		    };

    	String sortOrder =
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " ASC";

    	String where = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;
    	
    	Cursor c = db.query(
    			ConcoursEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    where,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	return c;
    	
    }

    public void delAConcours(String num)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	

    	String where = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;
    	
    	db.delete(ConcoursEntry.TABLE_NAME, where, null);
    	
    	
    }

    public boolean checkAConcours(String num)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			ConcoursEntry._ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_DATE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_COMMENTAIRE,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT,
    			ConcoursEntry.COLUMN_NAME_CONCOURS_SMS
    		    };

    	String sortOrder =
    			ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " ASC";
    	
    	String where = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;

    	
    	Cursor c = db.query(
    			ConcoursEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    where,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	if (c.getCount()>0) return true;
    	else return false;
    	
    }

	public void updateConcours(String num, String newEtat, String newOrganisateur, String date, int event) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_ETAT, newEtat);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_ORGANISATEUR, newOrganisateur);
    	values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_DATE, date);
    	
    	if (event!=0)
    	{
    		values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT, event);
    	}
     	
    	String selection = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;
 
    	int count = db.update(
    			ConcoursEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "updateConcours nb concours modifie : " + count);
	}

	public void rmEvtConcours(String num) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
   		values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_EVENT, 0);
     	
    	String selection = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;
 
    	int count = db.update(
    			ConcoursEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "rmEvtConcours nb concours modifie : " + count);
	}

	
	public void updateSmsListeConcours(String num, String smsList) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
   		values.put(ConcoursEntry.COLUMN_NAME_CONCOURS_SMS, smsList);
     	
    	String selection = ConcoursEntry.COLUMN_NAME_CONCOURS_ID + " = " + num;
 
    	int count = db.update(
    			ConcoursEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "updateSmsListe sms liste modifie : " + count);
	}

	
//-------------------------------------EPREUVES-------------------------------------    
    public void newEpreuves(String numConcours, String numEpreuves, String etat, String organisateur,String date, String commentaire, String intituleEpreuve, int maxPlace, int placePrises, String contactsKeyString)
    {
    	if (checkAnEpreuve(numConcours,numEpreuves))
    	{
    		Log.d(this.getClass().getName(), "newEpreuves concours : " + numConcours + " epreuve : " + numEpreuves + " deja present");
    		return;
    	}
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ID,numConcours);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_NUM,numEpreuves);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT,etat);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR,organisateur);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE,intituleEpreuve);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX,maxPlace);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT,placePrises);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_DATE,date);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_COMMENTAIRE,commentaire);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT,0);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_SMS,contactsKeyString);

    	long newRowId;
    	newRowId = db.insert(EpreuveEntry.TABLE_NAME, null, values);
    	if (newRowId==-1)
    	{
    		Log.d(this.getClass().getName(), "epreuve non ajoute dans db");
    	}
    	else
    	{
    		Log.d(this.getClass().getName(), "epreuve ajoute dans db");
    	}
    }
 
    public Cursor readAllepreuves()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			EpreuveEntry._ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NUM,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_DATE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_COMMENTAIRE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_SMS
    		    };

    	String sortOrder =
    			EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " ASC , " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM  + " ASC";

    	
    	Cursor c = db.query(
    			EpreuveEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    null,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	return c;
    	
    }

    public Cursor readAnEpreuve(String numConcours, String numEpreuve)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			EpreuveEntry._ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NUM,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_DATE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_COMMENTAIRE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_SMS
    		    };

    	String sortOrder =
    			EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " ASC , " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM  + " ASC";

    	String where = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
    	
    	Cursor c = db.query(
    			EpreuveEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    where,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	return c;
    	
    }

    public void delAnEpreuve(String numConcours, String numEpreuve)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	

    	String where = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
    	
    	db.delete(EpreuveEntry.TABLE_NAME, where, null);

    	
    }

    public boolean checkAnEpreuve(String numConcours, String numEpreuve)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] projection = {
    			EpreuveEntry._ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ID,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NUM,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_DATE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_COMMENTAIRE,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT,
    	    	EpreuveEntry.COLUMN_NAME_EPREUVES_SMS
     		    };

    	String sortOrder =
    			EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " ASC , " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM  + " ASC";

    	String where = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
    	

    	
    	Cursor c = db.query(
    			EpreuveEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    where,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );

    	if (c.getCount()>0) return true;
    	else return false;
    	
    }

	public void updateEpreuve(String numConcours, String numEpreuve, String newEtat, String newOrganisateur, String date, int event, String intituleEpreuve, int maxPlace, int placePrises) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT,newEtat);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ORGANISATEUR,newOrganisateur);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_INTITULE,intituleEpreuve);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_MAX,maxPlace);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT,placePrises);
    	values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_DATE,date);
    	
    	if (event!=0)
    	{
    		values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT, event);
    	}
     	
    	String selection = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
 
    	int count = db.update(
    			EpreuveEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "updateEpreuve nb epreuve modifie : " + count);
	}

	public void rmEvtEpreuve(String numConcours, String numEpreuve) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
   		values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_EVENT, 0);
     	
    	String selection = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
 
    	int count = db.update(
    			EpreuveEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "rmEvtEpreuve nb epreuve modifie : " + count);
	}
	
	public void updateSmsListeEpreuve(String numConcours, String numEpreuve, String smsList) 
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
   		values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_SMS, smsList);
     	
    	String selection = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
    	 
    	int count = db.update(
    			EpreuveEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "rmEvtEpreuve nb epreuve modifie : " + count);
	}


	public void setEpreuveState(String numConcours, String numEpreuve, String etat)
	{
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
   		values.put(EpreuveEntry.COLUMN_NAME_EPREUVES_ETAT, etat);
     	
    	String selection = EpreuveEntry.COLUMN_NAME_EPREUVES_ID + " = " + numConcours + " AND " + EpreuveEntry.COLUMN_NAME_EPREUVES_NUM + " = " + numEpreuve;
 
    	int count = db.update(
    			EpreuveEntry.TABLE_NAME,
    		    values,
    		    selection,
    		    null);
    	Log.d(this.getClass().getName(), "setEpreuveState nb epreuve modifie : " + count);
		
	}

	
}
