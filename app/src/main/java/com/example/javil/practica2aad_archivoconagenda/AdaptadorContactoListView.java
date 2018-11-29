package com.example.javil.practica2aad_archivoconagenda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.example.javil.practica2aad_archivoconagenda.MainActivity.contacts;


public class AdaptadorContactoListView extends ArrayAdapter<Contacto> {

    private Context contexto;
    private int layout;
    LayoutInflater inflater;
    private List<Contacto> contactos;
    private Drawable imagen;

    public AdaptadorContactoListView(@NonNull Context context, List<Contacto> contactos) {
        this(context, R.layout.item, contactos);
    }

    public AdaptadorContactoListView(@NonNull Context context, int layout, List<Contacto> contactos) {
        super(context, layout, contactos);
        this.contactos = contactos;
        this.contexto = context;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        Log.v("MITAG", position + "");
        if(convertView == null){
            Log.v("MITAG", "inflando: " +position + "");
            convertView = inflater.inflate(layout, null);
        }

        final Contacto c = contactos.get(position);

        imagen = contexto.getResources().getDrawable(R.drawable.contacto);
        Bitmap imagenbitmap = ((BitmapDrawable) imagen).getBitmap();
        RoundedBitmapDrawable imagencircular = RoundedBitmapDrawableFactory.create(contexto.getResources(), imagenbitmap);
        imagencircular.setCircular(true);

        TextView tvnombre = convertView.findViewById(R.id.tvNombre);
        TextView tvtelefono = convertView.findViewById(R.id.tvNumero);
        ImageView ivcontacto = convertView.findViewById(R.id.ivcontacto);
        Button btedit = convertView.findViewById(R.id.btedit);
        Button btdelete = convertView.findViewById(R.id.btdelete);

        tvnombre.setText(c.getNombre());
        tvtelefono.setText(c.getTelefono());
        ivcontacto.setImageDrawable(imagencircular);

        btedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, EditarContacto.class);
                intent.putExtra("nombre",c.getNombre());
                intent.putExtra("telefono",c.getTelefono());
                intent.putExtra("posicion", position);
                contexto.startActivity(intent);
            }
        });

        btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
