package com.example.smartoper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Pendiente extends Fragment {

    Boolean textoR = false;
    Boolean textoA = false;

    public Pendiente() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {   
        View root = inflater.inflate(R.layout.fragment_pendiente, container, false);

        LinearLayout linearLayout = root.findViewById(R.id.linear);
        TextView textView = root.findViewById(R.id.textView);


        FirebaseFirestore.getInstance().collection("NORMAL").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cargarTarjeta(linearLayout, textView, R.drawable.tarjeta_amarillo, "NORMAL", task.getResult(), Color.parseColor("#c09f51"));
            } else {
                textoA = true;
            }
        });

        FirebaseFirestore.getInstance().collection("GRAVE").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cargarTarjeta(linearLayout, textView, R.drawable.tarjeta_roja, "GRAVE", task.getResult(), Color.parseColor("#783736"));
            } else {
                textoR = true;
            }
        });

        if (textoR && textoA){
            textView.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private void cargarTarjeta(LinearLayout linearLayout, TextView textView, int diseño, String state, QuerySnapshot querySnapshot, int color){
        for (QueryDocumentSnapshot document : querySnapshot) {
            textView.setVisibility(View.INVISIBLE);

            View tarjeta = LayoutInflater.from(this.getContext()).inflate(R.layout.tarjeta, null);
            tarjeta.setBackgroundResource(diseño);

            TextView estado = tarjeta.findViewById(R.id.estado);
            TextView titulo = tarjeta.findViewById(R.id.titulo);

            estado.setText(state);
            estado.setTextColor(color);
            titulo.setText(document.getString("titulo"));

            linearLayout.addView(tarjeta);
            tarjeta.setOnClickListener(v -> {
                startActivity(new Intent(this.getContext(), Problema.class).putExtra("coleccion", state).putExtra("documento", document.getId()));
            });
        }
    }
}