package com.example.smartoper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Resuelto extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    Boolean mostrar = true;

    public Resuelto() {
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

        loadData(linearLayout, textView);

        return root;
    }

    private void loadData(LinearLayout linearLayout, TextView textView) {
        linearLayout.removeAllViews();

        FirebaseFirestore.getInstance().collection("resuelto").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cargarTarjeta(linearLayout, task.getResult(), textView);
            } else {
                textView.setVisibility(View.VISIBLE);
            }

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void cargarTarjeta(LinearLayout linearLayout, QuerySnapshot querySnapshot, TextView textView) {
        for (QueryDocumentSnapshot document : querySnapshot) {
            View tarjeta = LayoutInflater.from(requireContext()).inflate(R.layout.tarjeta, null);
            TextView estado = tarjeta.findViewById(R.id.estado);
            TextView titulo = tarjeta.findViewById(R.id.titulo);

            String operario = document.getString("operario");
            String sharedOperario = requireContext().getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getString("operario", "");

            if (operario != null && operario.equals(sharedOperario)){
                String estadoString = document.getString("estado");
                if (estadoString != null) {
                    mostrar = false;
                    estado.setText(estadoString);
                    titulo.setText(document.getString("titulo"));

                    if (estadoString.equalsIgnoreCase("GRAVE")) {
                        tarjeta.setBackgroundResource(R.drawable.tarjeta_roja);
                        estado.setTextColor(Color.parseColor("#783736"));
                    } else {
                        tarjeta.setBackgroundResource(R.drawable.tarjeta_amarillo);
                        estado.setTextColor(Color.parseColor("#c09f51"));
                    }

                    textView.setVisibility(View.INVISIBLE);
                    linearLayout.addView(tarjeta);
                    tarjeta.setOnClickListener(v -> startActivity(new Intent(requireContext(), Problema.class).putExtra("origen", "resuelto").putExtra("coleccion", "resuelto").putExtra("documento", document.getId())));
                }
            } else {
                if (mostrar)
                    textView.setVisibility(View.VISIBLE);
            }
        }
    }

}