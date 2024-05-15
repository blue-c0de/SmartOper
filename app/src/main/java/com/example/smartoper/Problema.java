package com.example.smartoper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

        String coleccion = getIntent().getStringExtra("coleccion");
        String documento = getIntent().getStringExtra("documento");

        FirebaseFirestore.getInstance().collection(coleccion).document(documento).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                titulo.setText(document.getString("titulo"));
                categoria.setText(document.getString("categoria"));
                contacto.setText(document.getString("contacto"));
                direccion.setText(document.getString("direccion"));
                descripcion.setText(document.getString("descripcion"));
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