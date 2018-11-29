package com.example.javil.practica2aad_archivoconagenda;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Contacto> contacts = new ArrayList<>();
    public static AdaptadorContactoListView adapter;
    public static final String TAG = "131415VJ";

    private static final String FILE_NAME = "contactos.csv";
    private Button btexterna, btinterna;
    private TextView text;
    private ListView lvcontacts;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Agenda de Contactos");

        btexterna = findViewById(R.id.btexterna);
        btinterna = findViewById(R.id.btinterna);
        lvcontacts = findViewById(R.id.lvContacts);
        text = findViewById(R.id.textView);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        boolean checked = prefs.getBoolean("checked", false);

        if(checked == true){
            btinterna.setVisibility(View.INVISIBLE);
            btexterna.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
            lvcontacts.setVisibility(View.VISIBLE);

            if(!contacts.isEmpty()){
                guardarcsv();
            }
            load();
        }else {
            EnableRuntimePermission();
            btexterna.setVisibility(View.VISIBLE);
            btinterna.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            lvcontacts.setVisibility(View.INVISIBLE);
        }

        btexterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btinterna.setVisibility(View.INVISIBLE);
                btexterna.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                lvcontacts.setVisibility(View.VISIBLE);
                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("checked",true);
                editor.commit();
                guardarcsv();
                load();
            }
        });

        btinterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btinterna.setVisibility(View.INVISIBLE);
                btexterna.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                lvcontacts.setVisibility(View.VISIBLE);
                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("checked",true);
                editor.commit();
                guardarcsv();
                load();
            }
        });
    }

    public void initListview(){
        contacts = getListaContactos();
        for(int i=0; i<contacts.size(); i++){
            String telefono = contacts.get(i).getTelefono().substring(1,contacts.get(i).getTelefono().length()-1);
            contacts.get(i).setTelefono(telefono);
            Log.v(TAG, "INIT LIST VIEW");
            Log.v(TAG, contacts.get(i).getNombre() +"   " +contacts.get(i).getTelefono());
        }
    }

    public void guardarcsv(){

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            for(int i=0; i<contacts.size(); i++) {
                String text = convertirEnTexto(i)+"\n";
                fos.write(text.getBytes());
                Log.v(TAG, "Texto"+text);
            }

            Log.v(TAG, "Saved to " + getFilesDir() + "/" + FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(){
        FileInputStream fis = null;

        Log.v(TAG, "- Load -");
        try{
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            String[] linea;
            String name;
            String telefono;

            Log.v(TAG, "leyendo");

            contacts.clear();

            while((text = br.readLine()) != null){
                sb.append(text).append("\n");
                linea = text.split(";",2);
                name = linea[0].substring(1, linea[0].length()-1);
                telefono = linea[1].substring(1, linea[1].length()-2);
                contacts.add(new Contacto(name,telefono));
                Log.v(TAG, "Texto"+text);
                Log.v(TAG, "Nombre: "+name +" Telefono: "+telefono);
            }
            Log.v(TAG, "leido");

            adapter = new AdaptadorContactoListView(MainActivity.this, contacts);
            lvcontacts.setAdapter(adapter);

            //Log.v(TAG, "Texto"+sb.toString());
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(fis != null){
                try{
                    fis.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void externaPrivada(View v){

        btinterna.setVisibility(View.INVISIBLE);
        btexterna.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);

        lvcontacts.setVisibility(View.VISIBLE);
    }

    private String convertirEnTexto(int i){
        String text, nombre, telefono;
        nombre = contacts.get(i).getNombre();
        telefono = contacts.get(i).getTelefono();
        text = "'"+nombre+"';'"+telefono+"';";
        return text;
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        }else{

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "- onRequestPermissionsResult -");
                    initListview();
                    //Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public List<Contacto> getListaContactos(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setNombre(cursor.getString(indiceNombre));
            contacto.setTelefono(getListaTelefonos(cursor.getLong(indiceId)).toString());
            lista.add(contacto);
        }
        return lista;
    }



    public List<String> getListaTelefonos(long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        cursor.moveToNext();
        numero = cursor.getString(indiceNumero);
        lista.add(numero);
        return lista;
    }



    protected void onSaveInstanceState(Bundle guardaEstado){
        super.onSaveInstanceState(guardaEstado);

        btexterna = findViewById(R.id.btexterna);
        btinterna = findViewById(R.id.btinterna);

        if(btexterna.getVisibility() == View.VISIBLE){
            guardaEstado.putInt("v_btexterna", View.VISIBLE);
        }else{
            guardaEstado.putInt("v_btexterna", View.INVISIBLE);
        }

        if(btinterna.getVisibility() == View.VISIBLE){
            guardaEstado.putInt("v_btinterna", View.VISIBLE);
        }else{
            guardaEstado.putInt("v_btinterna", View.INVISIBLE);
        }

        if(text.getVisibility() == View.VISIBLE){
            guardaEstado.putInt("v_text", View.VISIBLE);
        }else{
            guardaEstado.putInt("v_text", View.INVISIBLE);
        }

        if(lvcontacts.getVisibility() == View.VISIBLE){
            guardaEstado.putInt("v_lvcontacts", View.VISIBLE);
        }else{
            guardaEstado.putInt("v_lvcontacts", View.INVISIBLE);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstance){
        super.onRestoreInstanceState(savedInstance);
        int v_btexterna = savedInstance.getInt("v_btexterna");
        int v_btinterna = savedInstance.getInt("v_btinterna");
        int v_text = savedInstance.getInt("v_text");
        int v_lvcontacts = savedInstance.getInt("v_lvcontacts");

        btexterna.setVisibility(v_btexterna);
        btinterna.setVisibility(v_btinterna);
        text.setVisibility(v_text);
        lvcontacts.setVisibility(v_lvcontacts);

        if(v_lvcontacts == View.VISIBLE){
            guardarcsv();
            load();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.importarcontactos:
                EnableRuntimePermission();
                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("checked",false);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.importarcsv:
                guardarcsv();
                load();
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
