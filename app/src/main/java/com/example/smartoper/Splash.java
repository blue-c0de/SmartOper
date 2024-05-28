package com.example.smartoper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.imageView);

        // FIREBASE OPERARIO ACTIVO
        Map<String, Object> operario = new HashMap<>();
        operario.put("existe", false);
        operario.put("latitude", 0);
        operario.put("longitude", 0);
        FirebaseFirestore.getInstance().collection("ubicaciones").document("dispositivo1").set(operario);

        // Crear animaciones para el escalado en X y en Y
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.1f, 1.0f);

        // Configurar la duración de las animaciones
        scaleX.setDuration(2000); // 2 segundos
        scaleY.setDuration(2000); // 2 segundos

        // Iniciar las animaciones
        scaleX.start();
        scaleY.start();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, Login.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}