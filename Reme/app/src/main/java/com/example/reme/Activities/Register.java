package com.example.reme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reme.LoginDetail;
import com.example.reme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText et_email, et_name, et_phoneNumber, et_password, et_cPassword;
    TextView register, login;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mFirebaseAuth= FirebaseAuth.getInstance();
        et_email = findViewById(R.id.register_email_input);
        et_name = findViewById(R.id.register_name_input);
        et_phoneNumber = findViewById(R.id.register_phoneNumber_input);
        et_password = findViewById(R.id.register_password_input);
        et_cPassword = findViewById(R.id.register_cpassword_input);
        register = findViewById(R.id.register_register);
        login = findViewById(R.id.register_login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                final String name = et_name.getText().toString();
                String phoneNumber = et_phoneNumber.getText().toString();
                String password = et_password.getText().toString();
                String cPassword = et_cPassword.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(Register.this, "Fields are empty!",Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()){
                    et_email.setError("Please enter email!");
                    et_email.requestFocus();
                }
                else if(name.isEmpty()){
                    et_name.setError("Please enter name!");
                    et_name.requestFocus();
                }
                else if(phoneNumber.isEmpty()){
                    et_password.setError("Please enter phone number!");
                    et_password.requestFocus();
                }
                else if(password.isEmpty()){
                    et_password.setError("Please enter password!");
                    et_password.requestFocus();
                }
                else if(password.length() < 6){
                    et_password.setError("Password too short! At least 6 alphanumeric");
                    et_password.requestFocus();
                }
                else if(password.length() > 15){
                    et_password.setError("Password too long!");
                    et_password.requestFocus();
                }
                else if(cPassword.isEmpty()){
                    et_cPassword.setError("Please enter confirm password!");
                    et_cPassword.requestFocus();
                }
                else if(!password.equals(cPassword)){
                    et_password.setError("");
                    et_cPassword.setError("Password are not the same!");
                    et_cPassword.requestFocus();
                }
                else if(!(email.isEmpty() && name.isEmpty() && phoneNumber.isEmpty() && password.isEmpty() && cPassword.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Register.this, "Register Fail",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Register.this, "Register Successfully",Toast.LENGTH_SHORT).show();

                                rootNode = FirebaseDatabase.getInstance();
                                reference = rootNode.getReference("user");
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                String email = currentFirebaseUser.getEmail();
                                String name = et_name.getText().toString();
                                final String uid = currentFirebaseUser.getUid();
                                String phoneNumber = et_phoneNumber.getText().toString();

                                LoginDetail ld = new LoginDetail(email, name, uid, phoneNumber);
                                reference.child(uid).setValue(ld);

                                Intent i = new Intent(Register.this, MainActivity.class);
                                finish();
                                startActivity(i);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this, "Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Register.this, Login.class));
            }
        });

    }
}