package com.example.lectureshot;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;


/**
 * @author Mohamed Amine BEN MOUSSA
 */

public class PlagesOccupees {

    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    public Map<String, PlageHoraire> getEvents(Context context, String IdCalendrierSelectionne, String date){
        //Uri est un type de structure de données
        //Les événements locals sont enregistrés sous cette structure
        Uri eventUri;
        long upper = (long) 0, lower = (long) 0;
        try{
            lower = SDF.parse(date + "0000").getTime();
            upper = SDF.parse(date + "2359").getTime();
        }catch(Exception e){
            Log.d("Exception", "Wrong date");
        }

        Map<String, PlageHoraire> listePlageHoraire = new HashMap<String, PlageHoraire>();

        if (Build.VERSION.SDK_INT >= 8) {
            eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            eventUri = Uri.parse("content://calendar/events");
        }

        //Chaque String représente une colonne recherchée
        String[] projection = new String[]{"title", "dtstart", "dtend"};
        //On interroge la BD pour avoir les événements d'un calendrier particulier et ensuite les itérairés en utilisant le Cursor
        Cursor cursor = context.getContentResolver().query(eventUri, projection, "calendar_id=" + IdCalendrierSelectionne, null, "dtstart DESC, dtend DESC");
        if (cursor.moveToFirst()) {
            int num = 0;
            long start;
            long end;
            //C'est la variable qui retient l'index de la colonne qui contient les dates de début
            int columnBegin = cursor.getColumnIndex(projection[1]);
            //C'est la variable qui retient l'index de la colonne qui contient les dates de fin
            int columnEnd = cursor.getColumnIndex(projection[2]);
            do {
                Log.d("I started", cursor.getString(cursor.getColumnIndex(projection[0])));
                start = cursor.getLong(columnBegin);
                end = cursor.getLong(columnEnd);
                ++num;
                if ((start <= upper && start >= lower) || (end <= upper && end >= lower)){
                    //La magie commence ici
                    Log.d("I added", cursor.getString(cursor.getColumnIndex(projection[0])));
                    listePlageHoraire.put(cursor.getString(cursor.getColumnIndex(projection[0])), new PlageHoraire(start,end));
                }
            } while (cursor.moveToNext());
        }
        return listePlageHoraire;
    }
}
