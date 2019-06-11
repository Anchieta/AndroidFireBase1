package com.anchieta.androidfirebase1;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TelaPerfil extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    private ImageView imgPerfil;
    private Button btnGravar, btnResetSenha, btnResetEmal;
    private EditText edtNome, edtCpf, edtTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);

        imgPerfil = findViewById(R.id.imgPerfil);
        edtNome = findViewById(R.id.edtNome);
        edtCpf = findViewById(R.id.edtCpf);
        edtTel= findViewById(R.id.edtTel);
        btnGravar = findViewById(R.id.btnGravar);
        btnResetEmal = findViewById(R.id.btnResetEmal);
        btnResetSenha = findViewById(R.id.btnResetSenha);

        user = FirebaseAuth.getInstance().getCurrentUser();

        lePerfil();

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, Object> updtData = new HashMap<>();
                updtData.put("Nome", edtNome.getText().toString());
                updtData.put("Tel", edtTel.getText().toString());
                updtData.put("imgPerfil", " ");
                updtData.put("CPF", edtCpf.getText().toString());

                db.collection("users").document(user.getUid())
                        .update(updtData).addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Dados alterados com sucesso!!", Toast.LENGTH_LONG).show();
                        TelaPerfil.super.onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Não foi possível ALTERAR os dados", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }); //btnGravar.setOnClickListener()


        btnResetEmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(TelaPerfil.this, android.R.style.Theme_DeviceDefault_Dialog));
                LayoutInflater inflater = getLayoutInflater();
                final View viewAlerta = inflater.inflate(R.layout.activity_dialog_att_email, null);
                final EditText edtEmail = viewAlerta.findViewById(R.id.edtEmail);
                final EditText edtSenha = viewAlerta.findViewById(R.id.edtSenha);

                //edtEmail.setVisibility(View.INVISIBLE);
                //edtSenha.setVisibility(View.INVISIBLE);

                alerta.setTitle("** Troca Email **");
                alerta.setIcon(R.mipmap.ic_info);
                alerta.setCancelable(true);

                alerta.setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!edtEmail.getText().toString().equals("")) {
                            if (!edtSenha.getText().toString().equals("")) {
                                if (user != null) {
                                    AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), edtSenha.getText().toString());
                                    user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.updateEmail(edtEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getBaseContext(), "Email alterado no Auth com Sucesso", Toast.LENGTH_LONG).show();
                                                    UpdataEmailDoc(edtEmail.getText().toString());
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getBaseContext(), "FAILURE! NÃO FOI POSSIVEL ATUALIZAR O EMAIL", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        } //onSucess
                                    }); // user.reauthenticate(authCredential).addOnSuccessListener()
                                } else {
                                    Toast.makeText(getBaseContext(), "Usuário não encontrado", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Digite o novo SENHA", Toast.LENGTH_LONG).show();
                            } // if (!edtSenha.getText().toString().equals(""))
                        } else {
                            Toast.makeText(getBaseContext(), "Digite o novo EMAIL", Toast.LENGTH_LONG).show();
                        } //  if (!edtEmail.getText().toString().equals(""))

                    } //onClick alerta.setPositiveButton()
                });

                alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {  }
                }); // alerta.setNegativeButton()

                alerta.setView(viewAlerta);
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            } //onClick do btnResetEmail
        }); //btnResetEmal

        btnResetSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(TelaPerfil.this, android.R.style.Theme_DeviceDefault_Dialog));
                LayoutInflater inflater = getLayoutInflater();
                final View viewAlerta = inflater.inflate(R.layout.activity_dialog_att_senha , null);
                final EditText edtSenhaAntiga = viewAlerta.findViewById(R.id.edtSenhaAntiga);
                final EditText edtSenhaNova = viewAlerta.findViewById(R.id.edtSenhaNova);
                alerta.setIcon(R.mipmap.ic_info);
                alerta.setCancelable(true);

                alerta.setPositiveButton("Confirma a atualizacao?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!edtSenhaAntiga.getText().toString().equals("")) {
                            if (!edtSenhaNova.getText().toString().equals("")) {
                                if (edtSenhaNova.getText().toString().length() >= 6) {

                                    //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        UpdateSenhaAuth(edtSenhaAntiga.getText().toString(), edtSenhaNova.getText().toString());
                                    } else {
                                        Toast.makeText(getBaseContext(), "Náo foi encontrado um usu[ario logado para alterar", Toast.LENGTH_LONG).show();
                                    }// if (user != null)

                                } else {
                                    Toast.makeText(getBaseContext(), "A Nova Senha deve ser >= 6 caracteres", Toast.LENGTH_LONG).show();
                                } //edtSenhaNova >= 6
                            } else {
                                Toast.makeText(getBaseContext(), "Você esqueceu de digitar a Nova Senha", Toast.LENGTH_LONG).show();
                            } //edtSenhaNova ! =""
                        } else {
                            Toast.makeText(getBaseContext(), "Você esqueceu de digitar a Senha Antiga", Toast.LENGTH_LONG).show();
                        }//if (edtSenhaAntiga.getText().toString().equals(""))

                    }
                }); //alerta.setPositiveButton()

                alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {  }
                }); // alerta.setNegativeButton()

                alerta.setView(viewAlerta);
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            } //onClick do btnResetSenha


        }); //btnResetSenha



/*
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(onCreateView().getContext(), android.R.style.Theme_DeviceDefault_Dialog));
                alerta.setMessage("Deseja Realmente mudar a foto");
                alerta.setCancelable(true);
                alerta.setPositiveButton("Sim", (dialog, which) -> {
                   Intent intent = new Intent(Intent.ACTION_PICK);
                   intent.setType("image/*");
                   startActivityForResult(intent, 2);
                });
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            } //onClick
        }); // imgPerfil.setOnClickListener()
*/

    } //onCreate

    private void UpdateSenhaAuth(String senhaAntiga, String senhaNova) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), senhaAntiga);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(senhaNova).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "SENHA alterada no Auth\n\n " + user.getDisplayName() + "\n\n" + user.getEmail() , Toast.LENGTH_LONG).show();
                        UpdataSenhaDoc(senhaNova);


                    } //onSuccess
                }); //user.updatePassword(edtSenhaNova.getText().toString())
            } //onSuccess(Void aVoid)
        }); //user.reauthenticate(authCredential).addOnSuccessListener()
    } //UpdateSenhaAuth()

    private void UpdataSenhaDoc(String senhaNova) {
        final Map<String, Object> updtData = new HashMap<>();
        updtData.put("Senha", senhaNova);

        db.collection("users").document(user.getUid())
                .update(updtData).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(), "Senha nova alterada no DOCUMENTO", Toast.LENGTH_LONG).show();
                TelaPerfil.super.onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "NÃO foi possível alterar a senha no DOCUMENTO", Toast.LENGTH_LONG).show();
            }
        });
    } //UpdataSenhaDoc()

    private void UpdataEmailDoc(String emailNovo) {
        final Map<String, Object> updtData = new HashMap<>();
        updtData.put("Email", emailNovo);

        db.collection("users").document(user.getUid())
                .update(updtData).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(), "Novo email alterada no DOCUMENTO", Toast.LENGTH_LONG).show();
                TelaPerfil.super.onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "NÃO foi possível alterar o email no DOCUMENTO", Toast.LENGTH_LONG).show();
            }
        });
    } //UpdataSenhaDoc()

    private void lePerfil() {

        db.collection("users").document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    edtNome.setText((String) document.getData().get("Nome"));
                    edtCpf.setText((String) document.getData().get("CPF"));
                    edtTel.setText((String) document.getData().get("Tel"));

                    if (!document.getData().get("Imagem").equals("")) {
                        Glide.with(getApplicationContext()).load((String) document.getData().get("Imagem"))
                                .into(imgPerfil);
                    }
                } //if
            } //.get
        }); //db.collection(
    } //lePerfil

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (user != null) {
                if (imgPerfil != null) {
                    //if (imgPerfil.contais)


                } //if (imgPerfil != null)


            } //if (user != null)
        } //if (requestCode == 2 && resultCode == RESULT_OK)


    } // onActivityResult()
*/


} //class
