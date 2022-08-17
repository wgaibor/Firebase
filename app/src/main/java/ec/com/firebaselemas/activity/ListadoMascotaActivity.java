package ec.com.firebaselemas.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import ec.com.firebaselemas.R;

public class ListadoMascotaActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCerrarSession;
    Button btnAgregarMascotaV1;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_mascota);
        btnCerrarSession = findViewById(R.id.btn_cerrar_session);
        btnAgregarMascotaV1 = findViewById(R.id.btn_agregar_mascota_v1);
        btnCerrarSession.setOnClickListener(this);
        btnAgregarMascotaV1.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cerrar_session:
                cerrarSession();
                break;
            case R.id.btn_agregar_mascota_v1:
                agregarMascotas();
                break;
        }
    }

    private void agregarMascotas() {
        Intent intent = new Intent(this, CrearMascotaActivity.class);
        startActivity(intent);
    }

    private void cerrarSession() {
        mAuth.signOut();
        finish();
    }
}