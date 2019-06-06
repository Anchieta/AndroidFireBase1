package com.anchieta.androidfirebase1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TelaLogin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private TextView tvEsqueceuSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        firebaseAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        tvEsqueceuSenha = findViewById(R.id.tvEsqueceuSenha);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo aciveNetwork = cm.getActiveNetworkInfo();
                if (aciveNetwork != null) {
                    if (!edtEmail.getText().toString().equals("")) {
                        if (!edtSenha.getText().toString().equals("")) {

                            firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(TelaLogin.this, "LOGOU!!!!!!!!", Toast.LENGTH_LONG ).show();
                                                startActivity(new Intent(TelaLogin.this, TelaPrincipal.class));
                                                finish();
                                            } else {
                                                Toast.makeText(TelaLogin.this, "Falha na autenticação\n\n" + task.getException().getMessage().toString(), Toast.LENGTH_LONG ).show();
                                                //Log.d("login", task.getException().getMessage().toString());
                                                //finishAffinity();
                                            } // if !task.isSucessfull()
                                        } //onComplete
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            //Fazer algo
                                            Log.d("login", e.getMessage().toString());
                                        } //onFailure
                            }); //firebaseAuth
                        } else {
                            Toast.makeText(TelaLogin.this, "Digite a Senha", Toast.LENGTH_LONG ).show();
                        } // if edtSenha
                    } else {
                        Toast.makeText(TelaLogin.this, "Digite o Email", Toast.LENGTH_LONG ).show();
                    }//if (!edtEmail.

                } //if (aciveNetwork != null)

                String  teste = "=> Email: " + edtEmail.getText().toString() + "\n\nSenha: " + edtSenha.getText().toString();
                Toast.makeText(getBaseContext(), teste, Toast.LENGTH_LONG).show();
            } //onClick(View view)
        });  // btnLogin.setOnClickListener


        tvEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtEmail.getText().toString().equals("")) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(edtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TelaLogin.this, "Cheque seu e-mail para ver instruções de como resetar sua senha", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Não foi poss[ivel resetar a senha", Toast.LENGTH_LONG).show();
                            }
                        } //public void onComplete(@
                    }); //auth.sendPasswordResetEmail(
                } else {
                    Toast.makeText(getBaseContext(), "Você precisa digitar o Email", Toast.LENGTH_LONG).show();
                }
            } //onClick(View view)
        }); //tvEsqueceuSenha.setOnClickListener

    } // onCreate



} //class
