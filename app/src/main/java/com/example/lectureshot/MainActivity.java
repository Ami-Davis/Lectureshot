package com.example.lectureshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    TextView txtDate;
    int mYear, mMonth, mDay;
    String res = "Empty";
    Button exec;
    private Calendrier[] calendriers_trouves;
    List<Map<String, PlageHoraire>> plageslocales = new ArrayList<>();
    Map<String, PlageHoraire> PO;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        //Vérification des permissions
        int Permission_All = 1;
        String[] Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR,};
        if (!hasPermissions(this,Permissions)){
            ActivityCompat.requestPermissions(this,Permissions, Permission_All);
        }
        while (!((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))) {
        }
        start();
    }

    public static boolean hasPermissions (Context context, String... permissions){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    public void start() {
        exec = (Button) findViewById(R.id.execute);
        txtDate = (TextView) findViewById(R.id.in_date);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            //Cette méthode permet d'ouvrir un pop-up screen contenant un calendrier
            public void onClick(View v) {
                if (v == txtDate) {
                    // Get Current Date
                    final java.util.Calendar c = java.util.Calendar.getInstance();
                    mYear = c.get(java.util.Calendar.YEAR);
                    mMonth = c.get(java.util.Calendar.MONTH);
                    mDay = c.get(java.util.Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    String str = "" + year + "/";
                                    int Month = monthOfYear + 1;
                                    if (Month < 10)
                                        str += "0";
                                    str += Month + "/";
                                    str += dayOfMonth;
                                    txtDate.setText(str);
                                    res = str.replace("/", "");
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }
        });
        exec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExec(); }
        });
    }

    public void doExec(){
        if (res.equals("Empty")){
            SimpleDateFormat formatter_1 = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            res = formatter_1.format(date);
        }
        File dir = new File(Environment.getExternalStorageDirectory().toString(), "/lectureshot/");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        String path = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Map<File, Long> selectedFiles = new HashMap<File, Long>();

        Log.d("Files", "Size: "+ files.length);
        for(int i=0;i<files.length;i++){
            Log.d("Files", "FileName:" + files[i].getName() + i);
            String fileName = files[i].getName();
            if (fileName.startsWith(res)) {
                SimpleDateFormat formatter_2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
                try {
                    Date date = formatter_2.parse(fileName);
                    selectedFiles.put(files[i], date.getTime());
                    Log.d("Files", "FileName:" + files[i].getName() + "   Date: " + date.getTime());
                }catch (Exception e){
                    Log.d("Exception","Wrong file name");
                }
            }
        }
        if (selectedFiles.size()==0){
            Log.d("Empty", "No photos found for that date");
            Toast.makeText(MainActivity.this, "No photos found for that date" , Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, PlageHoraire> plageHoraireMap = getPO(res);
        if (plageHoraireMap.isEmpty()){
            Toast.makeText(MainActivity.this, "No events found on that date" , Toast.LENGTH_LONG).show();
        }
        Map<String, List<File>> match = new HashMap<>();
        for (Map.Entry<String, PlageHoraire> entry : plageHoraireMap.entrySet()){
            long lower = entry.getValue().getStartDate();
            long upper = entry.getValue().getEndDate();
            for (Map.Entry<File, Long> fileLongEntry : selectedFiles.entrySet()){
                if (fileLongEntry.getValue()<=upper && fileLongEntry.getValue()>=lower){
                    if(!match.containsKey(entry.getKey())){
                        match.put(entry.getKey(), new ArrayList<File>());
                    }
                    List temp = match.get(entry.getKey());
                    temp.add(fileLongEntry.getKey());
                    match.put(entry.getKey(),temp);
                    Log.d("match !", "keyyyyyy " + entry.getKey() + "value " + temp.get(0));
                }
            }
        }

        for(Map.Entry<String, List<File>> entry : match.entrySet()){
            File temp = new File(Environment.getExternalStorageDirectory().toString() + "/lectureshot/", entry.getKey() + "/");
            if (!temp.isDirectory()) {
                temp.mkdirs();
            }
            List<File> listFiles = entry.getValue();
            for (int i=0; i<listFiles.size();i++) {
                File source = new File (path + listFiles.get(i).getName());
                Log.d("Source", source.getAbsolutePath());
                File target = new File (temp.getAbsolutePath() + "/" + listFiles.get(i).getName());
                Log.d("target", target.getAbsolutePath());
                source.renameTo(target);
                Log.d("source modif", source.getAbsolutePath());
            }
        }
        Toast.makeText(MainActivity.this, "Photos organized successfully!" , Toast.LENGTH_LONG).show();
    }

    private Map<String, PlageHoraire> Union = new HashMap<>();

    //La liste Union retient l'union de toutes les listes de Plages Horaires d'un seul utilisateur sans doublure.
    public Map<String, PlageHoraire> Unifier (List<Map<String, PlageHoraire>> ll){
        Log.d("Unifier", "I unified");
        for (int i=0;i<ll.size();i++) {
            Map<String, PlageHoraire> l = ll.get(i);
            for (Map.Entry<String, PlageHoraire> entry : l.entrySet()) {
                if(!Union.containsKey(entry.getKey())){
                    Union.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return Union;
    }

    //Ceci pour avoir la liste des Plages Horaires Occupées de chaque utilisateur
    public Map<String, PlageHoraire> getPO(String date) {
        //Tableau contenant tous les calendriers trouvés
        calendriers_trouves = ListingCalendriers.getCalendars(context);
        //C'est la phase de collecte de calendriers
        if (calendriers_trouves != null) {
            for (int i = 0; i < calendriers_trouves.length; i++) {
                if (calendriers_trouves[i] != null) {
                    Log.d("calendars", "I am in calendar: " + calendriers_trouves[i].name);
                    String calendrier_selectionne = calendriers_trouves[i].id;
                    Map <String, PlageHoraire> l = new PlagesOccupees().getEvents(context, calendrier_selectionne, date);

                    for(Map.Entry<String, PlageHoraire> entry: l.entrySet()){

                        Log.d("Message", "key " + entry.getKey() + " value ");
                    }

                    plageslocales.add(l);
                }
            }
            //Eliminer la doublures entre les différents calendriers d'un même utilisateur
            PO = Unifier(plageslocales);
        }
        return PO;
    }
}



