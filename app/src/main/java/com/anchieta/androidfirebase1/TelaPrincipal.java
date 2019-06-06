package com.anchieta.androidfirebase1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TelaPrincipal extends AppCompatActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnLogout;
    private TextView txtNome,txtEmail, txtUid, txtTel;
    private ImageView ivFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtTel = findViewById(R.id.txtTel);
        txtUid = findViewById(R.id.txtUid);
        btnLogout = findViewById(R.id.btnLogout);
        ivFoto = findViewById(R.id.ivFoto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            txtNome.setText((String) document.getData().get("Nome"));
                            txtEmail.setText((String) document.getData().get("Email"));
                            txtTel.setText((String) document.getData().get("Tel"));
                            //txtUid.setText((String) document.getData().keySet().toString());


                            //Glide.with(getApplicationContext()).load((String) document.getData().get("Imagem")).into(ivFoto);

                            Log.d("Main", (String) document.getData().get("Imagem"));
                        }

                    } //onSuccess
                }); //.get().addOnSuccessListener()



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(view.getContext(), android.R.style.Theme_DeviceDefault_Dialog));
                alerta.setMessage("Você deseja realmente sair");
                alerta.setIcon(R.mipmap.ic_info);
                alerta.setCancelable(true);
                alerta.setPositiveButton("Sim", (dialog, whitch) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(TelaPrincipal.this, MainActivity.class));
                    finishAffinity();
                });
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();
            } //onClick
        }); //btnLogout


    } //onCreate




} //class
