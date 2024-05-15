package com.example.smartoper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Problema extends AppCompatActivity {
    private TextView titulo, categoria, contacto, direccion, descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);

        Button boton = findViewById(R.id.boton);
        TextView titulo = findViewById(R.id.titulo);
        TextView categoria = findViewById(R.id.categoria);
        TextView contacto = findViewById(R.id.contacto);
        TextView direccion = findViewById(R.id.direccion);
        TextView descripcion = findViewById(R.id.descripcion);
        WebView webView = findViewById(R.id.webView);
        TextView hIni = findViewById(R.id.fechaIni);
        TextView hFin = findViewById(R.id.fechaFin);
        TextView descripcionP = findViewById(R.id.desc);
        TextView descripcionLayout = findViewById(R.id.layoutDesc);
        LinearLayout layoutHoraIni = findViewById(R.id.layoutHoraIni);
        LinearLayout layoutHoraFin = findViewById(R.id.layoutHoraFin);
        View linea = findViewById(R.id.linea);

        String origen = getIntent().getStringExtra("origen");
        if(origen != null && origen.equals("resuelto")){
            linea.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            layoutHoraIni.setVisibility(View.VISIBLE);
            layoutHoraFin.setVisibility(View.VISIBLE);
            descripcionLayout.setVisibility(View.VISIBLE);
            descripcionP.setVisibility(View.VISIBLE);
            boton.setVisibility(View.INVISIBLE);
        }

        String coleccion = getIntent().getStringExtra("coleccion");
        String documento = getIntent().getStringExtra("documento");

        FirebaseFirestore.getInstance().collection(coleccion).document(documento).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                titulo.setText(document.getString("titulo"));
                categoria.setText(document.getString("categoria"));
                contacto.setText(document.getString("contacto"));
                direccion.setText(document.getString("direccion"));
                descripcion.setText(document.getString("descripcion"));
                String html = "<html><body style='margin:0; padding:0;'><img style='object-fit: contain; width:100%; height:100%;' src='" + document.getString("imagen") + "' /></body></html>";
                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                hIni.setText(document.getString("horaIni"));
                hFin.setText(document.getString("horaFin"));
                descripcionP.setText(document.getString("solucion"));
            }
        }).addOnFailureListener(exception -> {
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Problema.this, Solucion.class).putExtra("coleccion", coleccion).putExtra("documento", documento));
            }
        });
    }
}