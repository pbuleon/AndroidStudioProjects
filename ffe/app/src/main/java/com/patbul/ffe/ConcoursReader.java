package com.patbul.ffe;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.Cursor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.VibrationEffect;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.os.Vibrator;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;




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
    private static String URL_BASE = "https://ffecompet.ffe.com";
    private static String  FFE_USERNAME = "ws-mobile";
    private static String FFE_PASSWORD = "ws-mobile";
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


    static private String getToken(){
        Log.d("ConcoursReader", "getToken .....");

        try {
            URL url = new URL(URL_BASE + "/secure-ws/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String params = "username=" + URLEncoder.encode(FFE_USERNAME, "UTF-8") +
                    "&password=" + URLEncoder.encode(FFE_PASSWORD, "UTF-8") +
                    "&service=" + URLEncoder.encode(URL_BASE + "/", "UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String token = response.toString();
                if (token.isEmpty()) {
                    Log.d("ConcoursReader", "Empty token");
                }
                else {
                    Log.d("ConcoursReader", "Token reçu : " + token);
                }
                return token;
            } else {
                Log.d("ConcoursReader", "response code : " + responseCode);
                return "";
            }

        } catch (Exception e) {
            Log.d("ConcoursReader", "Erreur lors de l'authentification");
            System.out.println("Erreur lors de l'authentification");
            return "";
        }
    }

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
                    DownloadUrl(num, newEtat, organisateur, date);
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
                    String concoursId = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ID_COLUM_RANK);
                    String numEpr = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_NUM_COLUM_RANK);
                    String etat = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_ETAT_COLUM_RANK);
                    String intitule = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_INTITULE_COLUM_RANK);
                    int nbPlaceMax = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_MAX_COLUM_RANK);
                    int nbPlacePrise = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_NB_PLACE_CURRENT_COLUM_RANK);
                    String comment = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_COMMENTAIRE_COLUM_RANK);
                    int evt = c.getInt(ListConcoursDB.COLUMN_NAME_EPREUVES_EVENT_COLUM_RANK);
                    String smsList = c.getString(ListConcoursDB.COLUMN_NAME_EPREUVES_SMS_LIST_RANK);

                    Log.d("ConcoursReader", "epreuve : " + concoursId + " " + numEpr + " etat : " + etat);

                    if (etat.compareTo(DISPO) != 0) {
                        StringBuilder newEtat = new StringBuilder();
                        StringBuilder organisateur = new StringBuilder();
                        StringBuilder date = new StringBuilder();

                        JsonNode root = DownloadUrl(concoursId, newEtat, organisateur, date);


                        Log.d("ConcoursReader", "epreuve : " + concoursId + " " + numEpr + " newetat : " + newEtat);
                        if (newEtat.toString().compareTo(OUVERT_STATE) == 0) {
                            StringBuilder newIntule = new StringBuilder();
                            AtomicInteger nbPlaceMaxNew = new AtomicInteger(nbPlaceMax);
                            AtomicInteger nbPlacePriseNew = new AtomicInteger(0);
                            StringBuilder newEtatEpreuve = new StringBuilder();
                            parseEpreuve(root , Integer.parseInt(numEpr), newIntule, nbPlaceMaxNew, nbPlacePriseNew, newEtatEpreuve, "");
                            listeConc.updateEpreuve(concoursId, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 0, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                            if (newEtatEpreuve.toString().compareTo(etat) != 0) {
                                updateView = true;
                            }
                            if (newEtatEpreuve.toString().compareTo(DISPO) == 0) {
                                listeConc.updateEpreuve(concoursId, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 1, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                                // notif
                                Log.d("ConcoursReader", "Notif : ");

                                vibre(context);


                                 //SMS
                                sendSMS("Place dispo " + concoursId + " / " + numEpr + " : " + comment, context, smsList);

                            } else {
                                listeConc.updateEpreuve(concoursId, numEpr, newEtatEpreuve.toString(), organisateur.toString(), date.toString(), 0, newIntule.toString(), nbPlaceMaxNew.get(), nbPlacePriseNew.get());
                            }

                        } else {
                            listeConc.updateEpreuve(concoursId, numEpr, newEtat.toString(), organisateur.toString(), date.toString(), 0, UNKNOWN_STATE, 0, 0);
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

    private static void parseEpreuve(JsonNode root, int numEpr, StringBuilder newIntule, AtomicInteger nbPlaceMaxNew, AtomicInteger nbPlacePriseNew, StringBuilder newEtatEpreuve, String numConcours) {


        JsonNode liste = root.path("listeEprDis");
        String numevt = root.path("numEvt2").textValue();

        if (numConcours.isEmpty())
        {
            Log.d("ConcoursReader", "parseEpreuve : numConcours empty");
            if (liste.isArray())
            {
                for (JsonNode item : liste) {
                    String numconcoursItem = item.path("numconcours").textValue();
                    String token = getToken();
                    String urlStr = URL_BASE + "/index.php?ffeservice=TServiceEpreuvesConc"
                            + "&site_provenance=ffe.com"
                            + "&numEvt2=" + numevt
                            + "&numconc=" + numconcoursItem;
                    try {
                        URL url = new URL(urlStr);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Authorization", "Bearer " + token);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(15000);
                        int responseCode = conn.getResponseCode();

                        BufferedReader reader;
                        if (responseCode >= 200 && responseCode < 300) {
                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();

                            String epreuves = response.toString();

                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode rootepreuves = mapper.readTree(epreuves);
                            JsonNode listeepreuves = rootepreuves.path("ListeEpreuveC");
                            if (listeepreuves.isArray())
                            {
                                for (JsonNode itemEpreuve : listeepreuves) {
                                    int epreuveNume = itemEpreuve.path("numEpreuve").asInt();
                                    if (epreuveNume == numEpr){
                                        newIntule.append(itemEpreuve.path("libelleEpreuve").textValue());
                                        if (nbPlaceMaxNew.get() == 0 ) {
                                            nbPlaceMaxNew.set(itemEpreuve.path("nbEngages").asInt());
                                        }
                                        nbPlacePriseNew.set(itemEpreuve.path("nbEngages").asInt());
                                        if (nbPlacePriseNew.get()<nbPlaceMaxNew.get())
                                            newEtatEpreuve.append(DISPO);
                                        else
                                            newEtatEpreuve.append(COMPLET);
                                        return;
                                    }
                                }
                            }
                        }
                        else {
                            Log.d("ConcoursReader", "parseEpreuve responseCode : " + responseCode);
                        }

                        }
                    catch (Exception e)
                    {
                        Log.d("ConcoursReader", "parseEpreuve exception : " + e.toString());
                    }


                }
            }
            return;
        }

        nbPlaceMaxNew.set(999);
        nbPlacePriseNew.set(999);
        newEtatEpreuve.append(DISPO);
        return;


    }


    static private JsonNode DownloadUrl(String concId, StringBuilder etat, StringBuilder organisateur, StringBuilder date) throws IOException {
        try {

            Log.d("ConcoursReader", "DownloadUrl concours : " + concId);
            String token = getToken();

            String urlStr = URL_BASE + "/index.php?ffeservice=TServiceFicheConcours"
                    + "&site_provenance=ffe.com"
                    + "&numEvt2=" + concId;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            int responseCode = conn.getResponseCode();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String fiche = response.toString();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(fiche);

                Log.d("ConcoursReader", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
                List<String> numConcList = new ArrayList<>();
                JsonNode liste = root.path("listeEprDis");
                int nbEpreuvesTotal = 0;
                if (liste.isArray())
                {
                    for (JsonNode item : liste) {
                        nbEpreuvesTotal += item.path("nbEpreuves").asInt(0);
                    }
                }
                if (nbEpreuvesTotal > 0)
                    etat.append(OUVERT_STATE);
                else
                    etat.append(CALENDRIER_STATE);

                organisateur.append(root.path("lieuLibelle"));
                date.append(root.path("datedebut"));
                return root;
             } else {
                Log.d("ConcoursReader", "DownloadUrl responseCode : " + responseCode);
                etat.append(UNKNOWN_STATE);
                organisateur.append(UNKNOWN_STATE);
                date.append(UNKNOWN_DATE);
                return new ObjectMapper().createObjectNode();
            }

        } catch (Exception ex) {
            Log.d("ConcoursReader", "exception :" + ex.toString());
            etat.append(UNKNOWN_STATE);
            return new ObjectMapper().createObjectNode();
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
