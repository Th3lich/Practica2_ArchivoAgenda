package com.example.javil.practica2aad_archivoconagenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.javil.practica2aad_archivoconagenda.MainActivity.contacts;
import static com.example.javil.practica2aad_archivoconagenda.MainActivity.adapter;

public class EditarContacto extends AppCompatActivity {

    private EditText edNombre, edTelefono;
    private Button btedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        edNombre = findViewById(R.id.edNombre);
        edTelefono = findViewById(R.id.edTelefono);
        btedit = findViewById(R.id.btsave);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String telefono = intent.getStringExtra("telefono");
        final int posicion = intent.getIntExtra("posicion", 0);

        edNombre.setText(nombre);
        edTelefono.setText(telefono);

        btedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts.get(posicion).setNombre(String.valueOf(edNombre.getText()));
                contacts.get(posicion).setTelefono(String.valueOf(edTelefono.getText()));
                adapter.notifyDataSetChanged();

                finish();
            }
        });
    }
}
