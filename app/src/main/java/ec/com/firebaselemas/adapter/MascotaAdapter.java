package ec.com.firebaselemas.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import ec.com.firebaselemas.R;
import ec.com.firebaselemas.activity.CrearMascotaActivity;
import ec.com.firebaselemas.entity.Mascota;

public class MascotaAdapter extends FirestoreRecyclerAdapter<Mascota, MascotaAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MascotaAdapter(@NonNull FirestoreRecyclerOptions<Mascota> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        e.printStackTrace();
    }

    @Override
    protected void onBindViewHolder(@NonNull MascotaAdapter.ViewHolder holder, int position, @NonNull Mascota model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nombre.setText(model.getNombre());
        holder.edad.setText(model.getEdad());
        holder.color.setText(model.getColor());
        holder.precioVacuna.setText(model.getPrecioVacuna());

        holder.imgEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CrearMascotaActivity.class);
                intent.putExtra("idMascota", id);
                activity.startActivity(intent);
            }
        });

        holder.imgEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @NonNull
    @Override
    public MascotaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_mascota, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, edad, color, precioVacuna;
        ImageView imagenMascota, imgEditar, imgEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            edad = itemView.findViewById(R.id.edad);
            color = itemView.findViewById(R.id.color);
            precioVacuna = itemView.findViewById(R.id.precio_vacuna);
            imagenMascota = itemView.findViewById(R.id.img_mascota);
            imgEditar = itemView.findViewById(R.id.btn_editar);
            imgEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
