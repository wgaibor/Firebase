package ec.com.firebaselemas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ec.com.firebaselemas.activity.ListadoMascotaActivity;
import ec.com.firebaselemas.activity.RegistroActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegistrar;
    Button btnIngresar;
    Button btnVisitante;
    SignInButton signInButton;
    EditText etCorreoLogin;
    EditText etPasswordLogin;
    String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;

    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegistrar = findViewById(R.id.btn_registrar);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnVisitante = findViewById(R.id.btn_visitante);
        signInButton = findViewById(R.id.signInButton);
        btnRegistrar.setOnClickListener(this);
        btnIngresar.setOnClickListener(this);
        btnVisitante.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        etCorreoLogin = findViewById(R.id.et_correo_login);
        etPasswordLogin = findViewById(R.id.et_password_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
            case R.id.signInButton:
                signIn();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            llamarSiguienteActividad();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}