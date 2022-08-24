package ec.com.firebaselemas.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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

    int COD_SEL_IMAGE = 14;
    Uri imageUrl;
    ProgressDialog progressDialog;
    String rutaAlmacenamiento = "imagenes/*";
    String photo = "photo";
    StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

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
                        String picMascota  = documentSnapshot.getString("fotoMascota");

                        etNombreMascota.setText(nombreMascota);
                        etEdadMascota.setText(edadMascota);
                        etColorMascota.setText(colorMascota);
                        etPrecioVacunaMascota.setText(precioVacuna);
                        btnGuardarMascota.setText("Actualizar");
                        if(!picMascota.equals("")){
                            Picasso.with(CrearMascotaActivity.this)
                                    .load(picMascota)
                                    .resize(200 , 200)
                                    .into(fotoMascota);
                        }
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
                subirFoto();
                break;
            case R.id.btn_eliminar_foto:
                eliminarFoto();
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

    private void eliminarFoto() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("fotoMascota", "");
        mFirestore.collection("mascotas").document(idMascota).update(map);
        String rutaAlmacenamientoFoto = rutaAlmacenamiento+""+photo+""+mAuth.getUid()+""+idMascota;
        StorageReference reference = storageReference.child(rutaAlmacenamientoFoto);
        reference.delete();
        fotoMascota.setImageResource(android.R.drawable.btn_star);
        Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
    }

    private void subirFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, COD_SEL_IMAGE);
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
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == COD_SEL_IMAGE){
                imageUrl = data.getData();
                grabarImagen(imageUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void grabarImagen(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto...");
        progressDialog.show();
        String rutaAlmacenamientoFoto = rutaAlmacenamiento+""+photo+""+mAuth.getUid()+""+idMascota;
        StorageReference reference = storageReference.child(rutaAlmacenamientoFoto);
        Picasso.with(CrearMascotaActivity.this)
                        .load(imageUrl)
                        .resize(170, 170)
                        .into(fotoMascota);
        reference.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        if(uriTask.isSuccessful()){
                            uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imagenDescargada = uri.toString();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("fotoMascota", imagenDescargada);
                                    mFirestore.collection("mascotas")
                                            .document(idMascota)
                                            .update(map);
                                    Toast.makeText(CrearMascotaActivity.this, "Foto Actualizada", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CrearMascotaActivity.this, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}