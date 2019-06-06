package com.anchieta.androidfirebase1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class TelaCadastro extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;   //Autenticação
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //db

    private EditText edtNome, edtCpf, edtEmail, edtSenha, edtTel, edtIdade;
    private Button btnGravar;
    private CheckBox chkSexoMasc, chkSexFem, chkMailing, chkTermo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        //Autenticação
        firebaseAuth = FirebaseAuth.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtEmail  = findViewById(R.id.edtEmail);
        edtSenha  = findViewById(R.id.edtSenha);
        edtTel  = findViewById(R.id.edtTel);
        edtIdade  = findViewById(R.id.edtIdade);
        edtCpf = findViewById(R.id.edtCpf);
        chkSexoMasc = findViewById(R.id.chkSexoMasc);
        chkSexFem = findViewById(R.id.chkSexoFem);
        chkMailing = findViewById(R.id.chkMailing);
        chkTermo = findViewById(R.id.chkTermos);
        btnGravar = findViewById(R.id.btnGravar);



        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtNome.getText().toString().equals("")) {
                    if (!edtEmail.getText().toString().equals("")) {
                        if (!edtSenha.getText().toString().equals("")) {
                            if (edtSenha.getText().toString().length() >= 6) {
                                if (!edtTel.getText().toString().equals("")) {
                                    if (!edtIdade.getText().toString().equals("")) {
                                        if (chkSexoMasc.isChecked() || chkSexFem.isChecked()) {
                                            if (chkTermo.isChecked()) {

                                                saveUser();

                                            } else {
                                                Toast.makeText(TelaCadastro.this, "Vc precisa concordar com os termos de uso.", Toast.LENGTH_SHORT).show();
                                            } //
                                        } else {
                                            Toast.makeText(TelaCadastro.this, "Sexo inválido!", Toast.LENGTH_LONG).show();
                                        } // chkSexo
                                    } else {
                                        Toast.makeText(TelaCadastro.this, " inválido!", Toast.LENGTH_LONG).show();
                                    } // edtIdade
                                } else {
                                    Toast.makeText(TelaCadastro.this, "Idade inválido!", Toast.LENGTH_LONG).show();
                                } //edtTel
                            } else {
                                Toast.makeText(TelaCadastro.this, "Senha precisa ter pelo menos 6 caracteres", Toast.LENGTH_LONG).show();
                            } // edtSennha >= 6
                        } else {
                            Toast.makeText(TelaCadastro.this, "Senha inválido!", Toast.LENGTH_LONG).show();
                        } // edtSenha branco
                    } else {
                        Toast.makeText(TelaCadastro.this, "Email inválido!", Toast.LENGTH_LONG).show();
                    } // edtEmail
                } else {
                    Toast.makeText(TelaCadastro.this, "Nome inválido!", Toast.LENGTH_LONG).show();
                } //edtNome

            } //onClick
        }); //btn

        chkSexFem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkSexoMasc.isChecked()) {
                    chkSexoMasc.setChecked(false);
                }
            }
        }); //chkFem

        chkSexoMasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkSexFem.isChecked()) {
                    chkSexFem.setChecked(false);
                }
            }
        }); //chkFem

    } //onCreate

    private void saveUser() {

        //Criando usuário no "Autenticador" do FireBase
        firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(), edtSenha.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(TelaCadastro.this, "Falha na gravação do Registro", Toast.LENGTH_LONG).show();
                        }  else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                saveDb(user);
                            }

                        } // if/else (!task)
                    } //onComplete
                });

        //Salva dados no Banco de Daodos


    } //saveUser


    private void saveDb(FirebaseUser user) {

        final Map<String, Object> newData = new HashMap<>();
        newData.put("Nome", edtNome.getText().toString());
        newData.put("Tel", edtTel.getText().toString());
        newData.put("Idade", edtIdade.getText().toString());
        newData.put("Token", "a");
        newData.put("Imagem", " ");
        newData.put("CPF", edtCpf.getText().toString());
        newData.put("Email", edtEmail.getText().toString());
        newData.put("Senha", edtSenha.getText().toString());

        if (chkMailing.isChecked()) {
            newData.put("Mailing", true);

            //Cria uma coleção separada (de mailling)
            final Map<String, Object> newDataEmail = new HashMap<>();
            newDataEmail.put("Nome", edtNome.getText().toString());
            newDataEmail.put("Email", edtEmail.getText().toString());
            db.collection("usersEmailNews").document(user.getUid()).set(newDataEmail);
        } else {
            newData.put("Mailing", false);
        }

        if (chkSexoMasc.isChecked()) {
            newData.put("Sexo", "Masculino");
        } else {
            newData.put("Sexo", "Feminino");
        }


        //Grava os dados no banco
        db.collection("users").document(user.getUid()).set(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(), "Registro gravado com Sucesso!!!!!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(TelaCadastro.this, TelaPrincipal.class));
                finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "Não foi possível salvar os dados no Banco de Dados", Toast.LENGTH_LONG).show();
            }
        });



    } //saveDb()


} //class
