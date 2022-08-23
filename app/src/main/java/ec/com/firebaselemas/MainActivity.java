package ec.com.firebaselemas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ec.com.firebaselemas.activity.ListadoMascotaActivity;
import ec.com.firebaselemas.activity.RegistroActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegistrar;
    Button btnIngresar;
    Button btnVisitante;
    EditText etCorreoLogin;
    EditText etPasswordLogin;
    String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegistrar = findViewById(R.id.btn_registrar);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnVisitante = findViewById(R.id.btn_visitante);
        btnRegistrar.setOnClickListener(this);
        btnIngresar.setOnClickListener(this);
        btnVisitante.setOnClickListener(this);

        etCorreoLogin = findViewById(R.id.et_correo_login);
        etPasswordLogin = findViewById(R.id.et_password_login);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            llamarSiguienteActividad();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_registrar:
                Intent intent = new Intent(this, RegistroActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ingresar:
                consultarPersona();
                break;
            case R.id.btn_visitante:
                esUnVisitante();
                break;
        }
    }

    private void esUnVisitante() {
        Log.d(TAG, "Falta desarrollar");
    }

    private void consultarPersona() {
        String emailUsuario = etCorreoLogin.getText().toString().trim();
        String passUsuario = etPasswordLogin.getText().toString().trim();

        if(emailUsuario.isEmpty() && passUsuario.isEmpty()){
            Toast.makeText(this, "Favor ingrese los datos", Toast.LENGTH_SHORT).show();
        } else {
            consultarEnFirebase(emailUsuario, passUsuario);
        }
    }

    private void consultarEnFirebase(String emailUsuario, String passUsuario) {
        mAuth.signInWithEmailAndPassword(emailUsuario, passUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Bienvenido   "+user.getEmail(), Toast.LENGTH_SHORT).show();
                    llamarSiguienteActividad();
                } else {
                    Toast.makeText(MainActivity.this, "Error no existe el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error al inciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void llamarSiguienteActividad(){
        Intent intent = new Intent(MainActivity.this, ListadoMascotaActivity.class);
        startActivity(intent);
        //finish();
    }
}