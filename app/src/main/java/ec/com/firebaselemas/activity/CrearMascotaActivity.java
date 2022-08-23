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
import com.google.firebase.firestore.DocumentSnapshot;
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
    boolean isNecesarioActualizar = false;
    String idMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_mascota);
        this.setTitle("Crear Mascota");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        idMascota = getIntent().getStringExtra("idMascota");
        if(idMascota != null){
            buscarMascota(idMascota);
        }
    }

    private void buscarMascota(String idMascota) {
        mFirestore.collection("mascotas")
                    .document(idMascota)
                    .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombreMascota = documentSnapshot.getString("nombre");
                        String edadMascota =  documentSnapshot.getString("edad");
                        String colorMascota = documentSnapshot.getString("color");
                        String precioVacuna = documentSnapshot.getString("precioVacuna");

                        etNombreMascota.setText(nombreMascota);
                        etEdadMascota.setText(edadMascota);
                        etColorMascota.setText(colorMascota);
                        etPrecioVacunaMascota.setText(precioVacuna);
                        btnGuardarMascota.setText("Actualizar");
                        isNecesarioActualizar = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_subir_foto:
                break;
            case R.id.btn_eliminar_foto:
                break;
            case R.id.btn_guardar_mascota:
                if (isNecesarioActualizar) {
                    actualizarMascota(idMascota);
                } else {
                    guardarMascota();
                }
                break;
        }
    }

    private void actualizarMascota(String idMascota) {
        String nombreMascota = etNombreMascota.getText().toString().trim();
        String edadMascota = etEdadMascota.getText().toString().trim();
        String colorMascota = etColorMascota.getText().toString().trim();
        String costoVacuna = etPrecioVacunaMascota.getText().toString().trim();

        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombreMascota);
        map.put("edad", edadMascota);
        map.put("color", colorMascota);
        map.put("precioVacuna", costoVacuna);

        mFirestore.collection("mascotas")
                .document(idMascota)
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}