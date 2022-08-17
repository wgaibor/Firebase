package ec.com.firebaselemas.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ec.com.firebaselemas.R;

public class CrearMascotaActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView fotoMascota;
    Button btnSubirFoto, btnEliminarFoto;
    LinearLayout lyt_images;
    EditText etNombreMascota, etEdadMascota, etColorMascota, etPrecioVacunaMascota;
    Button btnGuardarMascota;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_mascota);
        fotoMascota = findViewById(R.id.foto_mascota);
        btnSubirFoto = findViewById(R.id.btn_subir_foto);
        btnEliminarFoto = findViewById(R.id.btn_eliminar_foto);
        lyt_images = findViewById(R.id.lyt_images);
        etNombreMascota = findViewById(R.id.et_nombre_mascota);
        etEdadMascota = findViewById(R.id.et_edad_mascota);
        etColorMascota = findViewById(R.id.et_color_mascota);
        etPrecioVacunaMascota = findViewById(R.id.et_precio_vacuna_mascota);
        btnGuardarMascota = findViewById(R.id.btn_guardar_mascota);
        btnSubirFoto.setOnClickListener(this);
        btnEliminarFoto.setOnClickListener(this);
        btnGuardarMascota.setOnClickListener(this);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_subir_foto:
                break;
            case R.id.btn_eliminar_foto:
                break;
            case R.id.btn_guardar_mascota:
                guardarMascota();
                break;
        }
    }

    private void guardarMascota() {
        String nombreMascota = etNombreMascota.getText().toString().trim();
        String edadMascota = etEdadMascota.getText().toString().trim();
        String colorMascota = etColorMascota.getText().toString().trim();
        String costoVacuna = etPrecioVacunaMascota.getText().toString().trim();

        if(nombreMascota.isEmpty()){
            Toast.makeText(this, "Se requiere el nombre de la mascota", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edadMascota.isEmpty()){
            Toast.makeText(this, "Se requiere la edad de la mascota", Toast.LENGTH_SHORT).show();
            return;
        }
        if(colorMascota.isEmpty()){
            Toast.makeText(this, "Se requiere el color de la mascota", Toast.LENGTH_SHORT).show();
            return;
        }
        if(costoVacuna.isEmpty()){
            Toast.makeText(this, "Se requiere el costo de la vacuna de la mascota", Toast.LENGTH_SHORT).show();
            return;
        }

        String idUser = mAuth.getCurrentUser().getUid();
        DocumentReference _id = mFirestore.collection("mascotas").document();
        Map<String, Object> map = new HashMap<>();
        map.put("idUser", idUser);
        map.put("id", _id);
        map.put("nombre", nombreMascota);
        map.put("edad", edadMascota);
        map.put("color", colorMascota);
        map.put("precioVacuna", costoVacuna);

        mFirestore.collection("mascotas")
                .document(_id.getId())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al crear la mascota", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}