package com.example.smartoper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView nom = findViewById(R.id.nombre);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ImageView cerrar = findViewById(R.id.cerrar);

        cerrar.setOnClickListener(view -> cerrarSesion());

        // FIREBASE OPERARIO ACTIVO
        Map<String, Object> operario = new HashMap<>();
        operario.put("existe", true);
        operario.put("latitude", 40.4214031);
        operario.put("longitude", -3.6681971);
        FirebaseFirestore.getInstance().collection("ubicaciones").document("dispositivo1").set(operario);

        // NOMBRE
        nom.setText(getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getString("nombre", ""));

        // ADAPTADOR
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Pendiente");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Resuelto");
    }

    private void cerrarSesion() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        startActivity(new Intent(this, Login.class));

        this.finish();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {}
}