package com.example.smartoper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Resuelto extends Fragment {

    public Resuelto() {
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


        FirebaseFirestore.getInstance().collection("resuelto").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                textView.setVisibility(View.INVISIBLE);
                cargarTarjeta(linearLayout, task.getResult());
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    private void cargarTarjeta(LinearLayout linearLayout, QuerySnapshot querySnapshot){
        for (QueryDocumentSnapshot document : querySnapshot) {
            View tarjeta = LayoutInflater.from(this.getContext()).inflate(R.layout.tarjeta, null);
            TextView estado = tarjeta.findViewById(R.id.estado);
            TextView titulo = tarjeta.findViewById(R.id.titulo);

            String estadoString = document.getString("estado");
            if (estadoString != null) {
                estado.setText(estadoString);
                titulo.setText(document.getString("titulo"));

                if(estadoString.equalsIgnoreCase("GRAVE")){
                    tarjeta.setBackgroundResource(R.drawable.tarjeta_roja);
                    estado.setTextColor(Color.parseColor("#783736"));
                } else {
                    tarjeta.setBackgroundResource(R.drawable.tarjeta_amarillo);
                    estado.setTextColor(Color.parseColor("#c09f51"));
                }

                linearLayout.addView(tarjeta);
                tarjeta.setOnClickListener(v -> startActivity(new Intent(this.getContext(), Problema.class).putExtra("origen", "resuelto").putExtra("coleccion", "resuelto").putExtra("documento", document.getId())));
            }
        }
    }

}