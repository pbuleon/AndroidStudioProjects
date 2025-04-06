package com.patbul.ffe;

import static androidx.core.content.ContextCompat.getSystemService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.VibratorManager;
import android.os.VibrationEffect;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


class EpreuveData {
    public String intule;
    public int nbPlaceMax;
    public int nbPlacePrise;

    public EpreuveData() {
        intule = "";
        nbPlaceMax = 0;
        nbPlacePrise = 0;
    }
};

public class ConcoursReader {
    private static String URL_PREF = "https://ffecompet.ffe.com/concours/";
    public static String UNKNOWN_DATE = "--/--/--";
    public final static String UNKNOWN_STATE = "inconnu";
    public final static String OUVERT_STATE = "ouvert";
    public final static String EN_COURS_STATE = "en cours";
    public final static String TERMINE_STATE = "terminé";
    public final static String AVANT_PROGRAMME_STATE = "avant programme";
    public final static String CALENDRIER_STATE = "calendrier";
    public final static String ANNULE_STATE = "annulé";
    public final static String CLOS_STATE = "clos";

    public static String DISPO = "Dispo";
    public static String COMPLET = "Complet";

    public static final int RIEN = 0;
    public static final int RESEAU_KO = 1;
    public static final int EVOLUTION_CONCOURS = 3;


    static public int UpdateConcours(Context context) {
        boolean updateView = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("ConcoursReader", "network available.");
            try {

                // -------------------------------- concours -------------------------------
                ListConcoursDB listeConc = new ListConcoursDB(context);
                Cursor c = listeConc.readAllConcours();
                Log.d("ConcoursReader", "nb concours : " + c.getCount());
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String num = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ID_COLUM_RANK);
                    String oldEtat = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_ETAT_COLUM_RANK);
                    String commentaire = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_COMMENTAIRE_COLUM_RANK);
                    String smsList = c.getString(ListConcoursDB.COLUMN_NAME_CONCOURS_SMS_LIST_RANK);
                    StringBuilder newEtat = new StringBuilder();
                    StringBuilder organisateur = new StringBuilder();
                    StringBuilder date = new StringBuilder();
                    DownloadUrl(URL_PREF + num, newEtat, organisateur, date);
                    Log.d("ConcoursReader", "concours : " + num + " newetat : " + newEtat);

                    if ((newEtat.toString().compareTo(UNKNOWN_STATE) != 0) && (newEtat.toString().compareTo(oldEtat) != 0)) {
                        if (newEtat.toString().compareTo(ConcoursReader.OUVERT_STATE) == 0) {
                            listeConc.updateConcours(num, newEtat.toString(), organisateur.toString(), date.toString(), ListConcoursDB.EVENT_OUVERT);

                            // notif
                            Log.d("ConcoursReader", "Notif : ");

                            vibre(context);


                            //SMS
                            sendSMS("Concours Ouvert " + num + ": " + commentaire, context, smsList);
                        } else {
                            listeConc.updateConcours(num, newEtat.toString(), organisateur.toString(), date.toString(), ListConcoursDB.EVENT_FERME);
                        }
                        updateView = true;
                    }
                    c.moveToNext();
                }


                // -------------------------------- epreuve -------------------------------
                c = listeConc.readAllepreuves();
                Log.d("ConcoursReader", "nb epreuve : " + c.getCount());
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String numConc = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ID_COLUM_RANK);
                    String numEpr = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_NUM_COLUM_RANK);
                    String etat = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK);
                    String intitule = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_INTITULE_COLUM_RANK);
                    int nbPlaceMax = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_MAX_COLUM_RANK);
                    int nbPlacePrise = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT_COLUM_RANK);
                    String comment = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK);
                    int evt = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK);
                    String smsList = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_SMS_LIST_RANK);

                    Log.d("ConcoursReader", "epreuve : " + numConc + " " + numEpr + " etat : " + etat);

                    if (etat.compareTo(DISPO) != 0) {
                        StringBuilder newEtat = new StringBuilder();
                        StringBuilder organisateur = new StringBuilder();
                        StringBuilder date = new StringBuilder();

                        Document doc = DownloadUrl(URL_PREF + numConc, newEtat, organisateur, date);


                        Log.d("ConcoursReader", "epreuve : " + numConc + " " + numEpr + " newetat : " + newEtat);

                        if (newEtat.toString().compareTo(OUVERT_STATE) == 0) {
                            StringBuilder newIntule = new StringBuilder();
                            AtomicInteger nbPlaceMaxNew = new AtomicInteger(0);
                            AtomicInteger nbPlacePriseNew = new AtomicInteger(0);
                            StringBuilder newEtatEpreuve = new StringBuilder();
                            parseEpreuve(doc , Integer.parseInt(numEpr), newIntule, nbPlaceMaxNew, nbPlacePriseNew, newEtatEpreuve);
                            listeConc.updateEpreuve(numConc, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 0, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                            if (newEtatEpreuve.toString().compareTo(etat) != 0) {
                                updateView = true;
                            }
                            if (newEtatEpreuve.toString().compareTo(DISPO) == 0) {
                                listeConc.updateEpreuve(numConc, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 1, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                                // notif
                                Log.d("ConcoursReader", "Notif : ");

                                vibre(context);


                                 //SMS
                                sendSMS("Place dispo " + numConc + " / " + numEpr + " : " + comment, context, smsList);

                            } else {
                                listeConc.updateEpreuve(numConc, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 0, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                            }

                        } else {
                            listeConc.updateEpreuve(numConc, numEpr, newEtat.toString(), organisateur.toString(), date.toString(), 0, UNKNOWN_STATE, 0, 0);
                        }

                    }
                    c.moveToNext();
                }

            } catch (IOException e) {
            }
            if (updateView) {
                return EVOLUTION_CONCOURS;
            }
            return RIEN;

        } else {
            return RESEAU_KO;
        }

    }

    private static void vibre(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Android 8.0+ (API 26) nécessite l'utilisation de VibrationEffect
            long[] pattern = {
                    0, 1000,  // Démarrage immédiat, vibre 1 sec
                    1000, 1000,
                    1000, 1000,
                    1000, 1000,
                    1000, 1000  // Total : 10 sec
            };
            VibrationEffect effect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(effect);

        }
    }

    private static void parseEpreuve(Document doc, int numEpr, StringBuilder newIntule, AtomicInteger nbPlaceMaxNew, AtomicInteger nbPlacePriseNew, StringBuilder newEtatEpreuve) {
        Element table = doc.getElementById("table-contest-results");
        if (table == null){
            Log.d("ConcoursReader", "parseEpreuve : table not found");
            return;
        }
        Elements tableBody = table.getElementsByTag("tbody");
        if ((tableBody == null) || (tableBody.size()==0)){
            Log.d("ConcoursReader", "parseEpreuve : tbody not found");
            return;
        }
        Elements trs = tableBody.get(0).getElementsByTag("tr");
        if ((trs == null) || (trs.size()==0)){
            Log.d("ConcoursReader", "parseEpreuve : trs not found");
            return;
        }
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if ((tds != null) || (trs.size()>=6)){
                try {
                    int tdNum = Integer.parseInt(tds.get(0).text());
                    if (tdNum == numEpr){
                        newIntule.append(tds.get(2).text());
                        String[] tdEngage = tds.get(5).text().trim().split("/");
                        if (tdEngage.length < 2){
                            nbPlaceMaxNew.set(999);
                            nbPlacePriseNew.set(Integer.parseInt(tdEngage[0].trim()));
                            newEtatEpreuve.append(DISPO);
                        }
                        else{
                            nbPlaceMaxNew.set(Integer.parseInt(tdEngage[1].trim()));
                            nbPlacePriseNew.set(Integer.parseInt(tdEngage[0].trim()));
                            if (nbPlacePriseNew.get() < nbPlaceMaxNew.get()){
                                newEtatEpreuve.append(DISPO);
                            }
                            else{
                                newEtatEpreuve.append(COMPLET);
                            }
                        }
                        return;
                    }
                }
                catch(NumberFormatException e){

                }
            }

        }
        return;


    }

    static private void getOrganisateurDate(Document doc, StringBuilder organisateur, StringBuilder date, String myurl) {
        Elements organisateurElem = doc.getElementsByClass("bloc-ffec-body");
        if (organisateurElem.size() == 0) {
            organisateur.append(UNKNOWN_STATE);
            date.append(UNKNOWN_DATE);
            Log.w("ConcoursReader", myurl + " : orgnisateur non trouve");
        } else {

            Element orgaDate = organisateurElem.get(0).child(0);
            if (orgaDate == null) {
                organisateur.append(UNKNOWN_STATE);
                date.append(UNKNOWN_DATE);
                Log.w("ConcoursReader", myurl + " : orgnisateur non trouve");
            }
            String orgaDateString = orgaDate.ownText();
            Log.d("ConcoursReader", myurl + " : " + orgaDateString);
            // recherche organisateur
            organisateur.append(orgaDateString.substring(13).split(" du")[0]);
            int i = orgaDateString.indexOf(") du ") + 5;
            date.append(orgaDateString.substring(i, i + 24));
        }
    }

    static private Document DownloadUrl(String myurl, StringBuilder etat, StringBuilder organisateur, StringBuilder date) throws IOException {
        try {

            Document doc = Jsoup.connect(myurl).get();
            Elements etatElem = doc.getElementsByClass("d-block d-sm-none");
            if (etatElem.size() == 0) {
                etat.append(UNKNOWN_STATE);
                organisateur.append(UNKNOWN_STATE);
                date.append(UNKNOWN_DATE);
                return doc;
            }
            Log.d("ConcoursReader", "etatElem : " + etatElem.html());

            switch (etatElem.html()) {
                case "Ouvert aux engagements":
                    Log.d("ConcoursReader", myurl + " : concours ouverts");
                    etat.append(OUVERT_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "En cours":
                    Log.d("ConcoursReader", myurl + " : concours Encours");
                    etat.append(EN_COURS_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "Traité":
                    Log.d("ConcoursReader", myurl + " : concours traité");
                    etat.append(TERMINE_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "Avant programme":
                    Log.d("ConcoursReader", myurl + " : concours avant programme");
                    etat.append(AVANT_PROGRAMME_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "Calendrier":
                    Log.d("ConcoursReader", myurl + " : concours au calendrier");
                    etat.append(CALENDRIER_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "Annulé":
                    Log.d("ConcoursReader", myurl + " : concours annule");
                    etat.append(ANNULE_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                case "Clos aux engagements":
                    Log.d("ConcoursReader", myurl + " : Clos aux engagements");
                    etat.append(CLOS_STATE);
                    getOrganisateurDate(doc, organisateur, date, myurl);
                    return doc;
                default:
                    Log.d("ConcoursReader", myurl + " : concours ouverts");
                    etat.append(UNKNOWN_STATE);
                    organisateur.append(UNKNOWN_STATE);
                    return doc;
            }
        } catch (IOException ex) {
            Log.d("ConcoursReader", "exception :" + ex.toString());
            etat.append(UNKNOWN_STATE);
            return null;
        }
        catch (Exception ex) {
            Log.d("ConcoursReader", "exception :" + ex.toString());
            etat.append(UNKNOWN_STATE);
            return null;
        }
    }

    static public String ReadIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    static private void sendSMS(String message, Context context, String smsList) {

        if (!smsList.isEmpty()) {
            String[] contactsLookup_keys = smsList.split(";");


            for (int i = 0; i < contactsLookup_keys.length; i++) {
                Log.d("ConcoursReader", "contactsLookup_keys[i]" + contactsLookup_keys[i]);

                Cursor contact = context.getContentResolver().query(
                        Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactsLookup_keys[i]),
                        null,
                        null,
                        null,
                        null
                );

                if (contact != null) {
                    contact.moveToFirst();
                    String id = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String name = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null,
                                null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones.getColumnIndex("data1"));
                        //cNumber="0670976370";
                        Log.d("ConcoursReader", "envoie SMS a " + name + " [" + cNumber + "] : " + message);

                        try {


                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(cNumber, null, message, null, null);
                        } catch (Exception e) {
                            Log.d("ConcoursReader", "echec SMS");
                            e.printStackTrace();
                        }


                    }
                }
            }
        }

    }


}
