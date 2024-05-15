package com.example.smartoper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView nom = findViewById(R.id.nombre);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        // Establecer nombre
        nom.setText(getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getString("nombre", ""));

        // Configura adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Pendiente");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Resuelto");
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {}
}