package com.example.smartoper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Solucion extends AppCompatActivity {

    private String tituloValue, categoriaValue, contactoValue, direccionValue, descripcionValue;
    private Drawable redBorderDrawable, defaultBorderDrawable;
    private EditText descripcion, imagen;
    private WebView webView;
    private TextInputLayout inputDescripcion, inputImagen;
    private String hIni = null, hFin = null, estado = null;
    private static final int GALLERY_REQUEST_CODE = 123;
    private FloatingActionButton fab;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solucion);

        String coleccion = getIntent().getStringExtra("coleccion");
        String documento = getIntent().getStringExtra("documento");

        descripcion = findViewById(R.id.descripcion);
        imagen = findViewById(R.id.imagen);
        inputDescripcion = findViewById(R.id.input_descripcion);
        inputImagen = findViewById(R.id.input_imagen);
        webView = findViewById(R.id.webView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        String html = "<html><body style='margin:0; padding:0;'><img style='object-fit: contain; width:100%; height:100%;' src='" + imagen.getText().toString() + "' /></body></html>";
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        imagen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String html = "<html><body style='margin:0; padding:0;'><img style='object-fit: contain; width:100%; height:100%;' src='" + imagen.getText().toString() + "' /></body></html>";
                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            }
        });

        Button botonIni = findViewById(R.id.botonIni);
        TextView horaIni = findViewById(R.id.horaIni);
        configurarBoton(botonIni, horaIni);

        Button botonFin = findViewById(R.id.botonFin);
        TextView horaFin = findViewById(R.id.horaFin);
        configurarBoton(botonFin, horaFin);

        redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

        FirebaseFirestore.getInstance().collection(coleccion).document(documento).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                tituloValue = document.getString("titulo");
                categoriaValue = document.getString("categoria");
                contactoValue = document.getString("contacto");
                direccionValue = document.getString("direccion");
                descripcionValue = document.getString("descripcion");
                estado = document.getString("estado");
            }
        }).addOnFailureListener(exception -> {
        });

        Button boton = findViewById(R.id.boton);
        boton.setOnClickListener(v -> {
            String img = imagen.getText().toString();
            String desc = descripcion.getText().toString();

            if (!img.isEmpty() && !desc.isEmpty() && !hIni.isEmpty() && !hFin.isEmpty()) {
                Map<String, Object> datos = new HashMap<>();
                datos.put("titulo", tituloValue);
                datos.put("estado", estado);
                datos.put("categoria", categoriaValue);
                datos.put("contacto", contactoValue);
                datos.put("direccion", direccionValue);
                datos.put("descripcion", descripcionValue);
                datos.put("imagen", img);
                datos.put("solucion", desc);
                datos.put("horaIni", hIni);
                datos.put("horaFin", hFin);
                FirebaseFirestore.getInstance().collection("resuelto").document().set(datos);

                FirebaseFirestore.getInstance().collection(coleccion).document(documento).delete();

                finish();
                startActivity(new Intent(Solucion.this, MainActivity.class));
            } else {
                marcarError(img,inputImagen, "Introduce url", imagen);
                marcarError(desc, inputImagen, "Máx. 2000 carácteres", descripcion);
            }
        });
    }

    private void configurarBoton(final Button boton, final TextView hora) {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());

                // Asigna la hora actual al TextView correspondiente
                hora.setText(currentDateAndTime);

                // Deshabilita y oculta el botón
                boton.setEnabled(false);
                boton.setVisibility(View.INVISIBLE);

                // Determina si es el botón de inicio o de fin y asigna la hora correspondiente
                if (boton.getId() == R.id.botonIni) {
                    hIni = currentDateAndTime;
                } else if (boton.getId() == R.id.botonFin) {
                    hFin = currentDateAndTime;
                }
            }
        });
    }

    private void marcarError(String text, TextInputLayout input, String valor, EditText edit) {
        if (text.isEmpty()) {
            input.setHint(valor);
            edit.setBackground(redBorderDrawable);
            edit.setOnFocusChangeListener((v19, hasFocus) -> {
                if (!hasFocus) {
                    edit.setBackground(defaultBorderDrawable);
                }
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (fileUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/" + fileUri.getLastPathSegment());

            UploadTask uploadTask = imagesRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(imageUrl);
                    imagen.setText(imageUrl);
                }).addOnFailureListener(exception -> {
                    // Error al obtener la URL de descarga
                });
            }).addOnFailureListener(exception -> {
                // Error al subir la imagen
            });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {}
}