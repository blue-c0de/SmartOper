package com.example.smartoper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private EditText usuario, contra;
    private TextInputLayout textInputUsuario, textInputContra;
    private FirebaseAuth mAuth;
    private Drawable redBorderDrawable, defaultBorderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usuario = findViewById(R.id.usuario);
        contra = findViewById(R.id.contra);
        textInputUsuario = findViewById(R.id.textInput_usuario);
        textInputContra = findViewById(R.id.textInput_contra);
        Button boton = findViewById(R.id.boton);
        redBorderDrawable = ContextCompat.getDrawable(this, R.drawable.red_border);
        defaultBorderDrawable = ContextCompat.getDrawable(this, R.drawable.default_border);

        // Ocultar o mostrar hint
        ocultarMostrarHint(usuario, textInputUsuario, "Introduce tu usuario");
        ocultarMostrarHint(contra, textInputContra, "Introduce tu contraseña");

        boton.setOnClickListener(v -> {
            String usu = usuario.getText().toString();
            String con = contra.getText().toString();

            if (!usu.isEmpty() && !con.isEmpty()) {
                loginUser(usu, con);
            } else {
                marcarError(usu, textInputUsuario, "Introduce tu usuario", usuario);
                marcarError(con, textInputContra, "Introduce tu contraseña", contra);
            }
        });
    }

    private void loginUser(String usu, String password) {
        FirebaseFirestore.getInstance().collection("operario").document(usu)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String correo = documentSnapshot.getString("correo");
                            String nombre = documentSnapshot.getString("nombre");

                            SharedPreferences preferences = getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("nombre", nombre);
                            editor.putString("operario", usu);
                            editor.apply();

                            mAuth.signInWithEmailAndPassword(correo, password)
                                    .addOnCompleteListener(this, task2 -> {
                                        if (task2.isSuccessful()) {
                                            mAuth.getCurrentUser();
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            finish();
                                        } else {
                                            showErrorDialog("Usuario/Contraseña Incorrecto.");
                                        }
                                    });
                        } else {
                            showErrorDialog("No existen datos.");
                        }
                    } else {
                       showErrorDialog("No existe el operario.");
                    }
                });
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void ocultarMostrarHint(EditText editText, TextInputLayout textInputLayout, String mensaje) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hacer nada
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No hacer nada
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    textInputLayout.setHint(mensaje);
                } else {
                    textInputLayout.setHint("");
                }
            }
        });
    }
}