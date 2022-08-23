package ec.com.firebaselemas.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import ec.com.firebaselemas.R;
import ec.com.firebaselemas.adapter.MascotaAdapter;
import ec.com.firebaselemas.entity.Mascota;
import ec.com.firebaselemas.entity.Usuario;

public class ListadoMascotaActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCerrarSession;
    Button btnAgregarMascotaV1;
    RecyclerView rvListadoMascota;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    Query query;
    MascotaAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_mascota);
        btnCerrarSession = findViewById(R.id.btn_cerrar_session);
        btnAgregarMascotaV1 = findViewById(R.id.btn_agregar_mascota_v1);
        rvListadoMascota = findViewById(R.id.rv_listado_mascota);
        btnCerrarSession.setOnClickListener(this);
        btnAgregarMascotaV1.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        configuracionListado();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configuracionListado() {
        rvListadoMascota.setLayoutManager(new LinearLayoutManager(this));

        /*query = mFirestore.collection("mascotas").whereEqualTo("nombre","Mahdy");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                List<Mascota> lstMascota = value.toObjects(Mascota.class);
                buscarDueño(lstMascota.get(0));
            }
        });*/
        query = mFirestore.collection("mascotas");

        FirestoreRecyclerOptions<Mascota> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Mascota>()
                .setQuery(query, Mascota.class).build();



        mAdapter = new MascotaAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        rvListadoMascota.setAdapter(mAdapter);
    }

    private void buscarDueño(Mascota mascota) {
        Query consultaUsuario = mFirestore.collection("user").whereEqualTo("id", mascota.getIdUser());

        consultaUsuario.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                // Convert query snapshot to a list of chats
                List<Usuario> usuarios = snapshot.toObjects(Usuario.class);
                Log.d(">>>>> Dueño    ", usuarios.get(0).getName());

            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}