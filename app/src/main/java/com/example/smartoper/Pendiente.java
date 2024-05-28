package com.example.smartoper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Pendiente extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    Boolean textoR = false;
    Boolean textoA = false;

    public Pendiente() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pendiente, container, false);

        LinearLayout linearLayout = root.findViewById(R.id.linear);
        TextView textView = root.findViewById(R.id.textView);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData(linearLayout, textView);
        });

        // Load data initially
        loadData(linearLayout, textView);

        return root;
    }

    private void loadData(LinearLayout linearLayout, TextView textView) {
        linearLayout.removeAllViews();

        FirebaseFirestore.getInstance().collection("NORMAL").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cargarTarjeta(linearLayout, textView, R.drawable.tarjeta_amarillo, task.getResult(), Color.parseColor("#c09f51"));
            } else {
                textoA = true;
            }
        });

        FirebaseFirestore.getInstance().collection("GRAVE").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cargarTarjeta(linearLayout, textView, R.drawable.tarjeta_roja, task.getResult(), Color.parseColor("#783736"));
            } else {
                textoR = true;
            }
        });

        if (textoR && textoA) {
            textView.setVisibility(View.VISIBLE);
        }

        // After data loading is complete, hide the refreshing indicator
        swipeRefreshLayout.setRefreshing(false);
    }

    private void cargarTarjeta(LinearLayout linearLayout, TextView textView, int diseño, QuerySnapshot querySnapshot, int color) {
        for (QueryDocumentSnapshot document : querySnapshot) {
            textView.setVisibility(View.INVISIBLE);

            View tarjeta = LayoutInflater.from(requireContext()).inflate(R.layout.tarjeta, null);
            tarjeta.setBackgroundResource(diseño);

            TextView estado = tarjeta.findViewById(R.id.estado);
            TextView titulo = tarjeta.findViewById(R.id.titulo);

            estado.setText(document.getString("estado"));
            estado.setTextColor(color);
            titulo.setText(document.getString("titulo"));

            linearLayout.addView(tarjeta);
            tarjeta.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), Problema.class).putExtra("coleccion", document.getString("estado")).putExtra("documento", document.getId()));
            });
        }
    }
}
