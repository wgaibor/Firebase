package ec.com.firebaselemas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ec.com.firebaselemas.R;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etNombre, etCorreo, etPassword;
    Button btnRegistrarPersona;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        etNombre = findViewById(R.id.et_nombre);
        etCorreo = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);
        btnRegistrarPersona = findViewById(R.id.btn_registrar_persona);
        btnRegistrarPersona.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_registrar_persona:
                String nombre = etNombre.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(nombre.isEmpty() && correo.isEmpty() && password.isEmpty()){
                    Toast.makeText(this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else {
                    registrarConFirebase(nombre, correo, password);
                }
                break;
        }
    }

    private void registrarConFirebase(String nombre, String correo, String password) {
        mAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCustomToken:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast.makeText(RegistroActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", nombre);
                map.put("email", correo);
                map.put("password", password);

                mFirestore.collection("user")
                        .document(id)
                        .set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            onBackPressed();
        }
    }
}