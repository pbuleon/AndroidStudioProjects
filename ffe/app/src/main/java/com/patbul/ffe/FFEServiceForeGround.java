package com.patbul.ffe;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class FFEServiceForeGround extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_STOP_SERVICE = "action_stop_service";

    // Pour les tâches périodiques
    private final Handler handler = new Handler();
    private Runnable periodicTask;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialiser vos ressources nécessaires
        initialiserRessources();

        // Configurer la tâche périodique
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // Exécuter votre tâche périodique
                executerTachePeriodique();

                // Programmer la prochaine exécution
                handler.postDelayed(this, 60000 * FfeWidget.PERIODE); // Exécution toutes les minutes
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP_SERVICE.equals(action)) {
                stopSelf();
                return START_NOT_STICKY;
            }
        }

        // Créer et afficher la notification
        startForeground(NOTIFICATION_ID, createNotification());


        // Démarrer les tâches périodiques
        handler.post(periodicTask);

        // Si le service est tué, le redémarrer
        return START_STICKY;
    }

    private Notification createNotification() {
        // Créer une intention pour arrêter le service
        Intent stopIntent = new Intent(this, FFEServiceForeGround.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Créer une intention pour ouvrir l'application
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Construire la notification
        return new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Service en cours d'exécution")
                .setContentText("Traitement en arrière-plan actif")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Arrêter", stopPendingIntent)
                .setOngoing(true) // Notification non rejetable
                .build();
    }

    private void initialiserRessources() {
        // Initialiser les ressources nécessaires pour votre tâche
        Log.d("FFEServiceForeGround", "thread initialiserRessources ....");
    }


    private void executerTachePeriodique() {
        // Code à exécuter périodiquement
        Log.d("FFEServiceForeGround", "executerTachePeriodique ....");
        int res = ConcoursReader.UpdateConcours(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Arrêter les tâches périodiques
        handler.removeCallbacks(periodicTask);

        // Libérer les ressources
        libererRessources();
    }

    private void libererRessources() {
        // Libérer les ressources utilisées par votre service
        Log.d("FFEServiceForeGround", "libererRessources ....");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Ce service n'est pas utilisé pour la liaison (binding)
        Log.d("FFEServiceForeGround", "onBind ....");
        return null;
    }
}
