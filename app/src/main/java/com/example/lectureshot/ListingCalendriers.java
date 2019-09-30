package com.example.lectureshot;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

/**
 *
 *@author Mohamed Amine BEN MOUSSA
 */

public class ListingCalendriers {

    private static Calendrier calendriers_trouves[];

    //Fonction pour avoir les noms et les identifiants des calendriers
    static public Calendrier[] getCalendars(Context context) {
        //Chaque String représente une colonne recherchée
        String[] projection = new String[]{"_id", "calendar_displayName"};
        //Uri est un type de structure de données
        //Les calendriers locals sont enregistrés sous cette structure
        Uri calendars;
        if (Build.VERSION.SDK_INT >= 8) {
            calendars = Uri.parse("content://com.android.calendar/calendars");
        } else {
            calendars = Uri.parse("content://calendar/calendars");
        }
        //On interroge la BD pour avoir les calendriers et ensuite les itérairés en utilisant le Cursor
        Cursor cursor = context.getContentResolver().query(calendars, projection, null, null, null);
        //num est itérateur
        int num = 0;
        if (cursor.moveToFirst()) {
            calendriers_trouves = new Calendrier[cursor.getCount()];
            String calName;
            String calId;
            //C'est la variable qui retient l'index de la colonne qui contient les noms des calendriers
            int nameColonne = cursor.getColumnIndex(projection[1]);
            //C'est la variable qui retient l'index de la colonne qui contient les identifiants des calendriers
            int idColonne = cursor.getColumnIndex(projection[0]);
            do {
                //Création d'un tableau de calendriers
                calName = cursor.getString(nameColonne);
                calId = cursor.getString(idColonne);
                Log.d("Cal", calName);
                if (calName.contains("@") || calName.contains("synapses")) {
                    calendriers_trouves[num] = new Calendrier(calName, calId);
                    ++num;
                }
            } while (cursor.moveToNext());
        }

        Calendrier[] res = new Calendrier[num];
        for (int i=0;i<num;i++){
            res[i] = calendriers_trouves[i];
            Log.d("Calendrierr", res[i].toString());
        }

        return res;
    }
}

