package com.example.reme.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reme.Fragments.NoteFragment;
import com.example.reme.LoginDetail;
import com.example.reme.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    EditText textEditProfileName, textEditProfilePhoneNumber;
    TextView textEditProfileEmail;
    LinearLayout layoutEditProfileConfirm, layoutEditProfileCancel;

    FirebaseAuth firebaseAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();

        textEditProfileName = findViewById(R.id.textEditProfileName);
        textEditProfileEmail = findViewById(R.id.textEditProfileEmail);
        textEditProfilePhoneNumber = findViewById(R.id.textEditProfilePhoneNumber);
        layoutEditProfileConfirm = findViewById(R.id.layoutEditProfileConfirm);
        layoutEditProfileCancel = findViewById(R.id.layoutEditProfileCancel);

        final LoginDetail[] d = new LoginDetail[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("uid").getValue().toString().equals(uid)){
                        d[0] = new LoginDetail(ds.child("email").getValue().toString(), ds.child("name").getValue().toString(),
                                ds.child("uid").getValue().toString(), ds.child("phoneNumber").getValue().toString());
                    }
                }
                LoginDetail ld = d[0];
                textEditProfileName.setText(ld.getName());
                textEditProfileEmail.setText(ld.getEmail());
                textEditProfilePhoneNumber.setText(ld.getPhoneNumber());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        layoutEditProfileConfirm.setOnClickListener(v -> {
            UpdateData(textEditProfileEmail.getText().toString(), textEditProfileName.getText().toString(),
                    uid, textEditProfilePhoneNumber.getText().toString());
        });

        layoutEditProfileCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void UpdateData(String email, String name, String uid, String phoneNumber) {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("user");

        LoginDetail ld = new LoginDetail(email, name, uid, phoneNumber);
        reference.child(uid).setValue(ld);
        Toast.makeText(EditProfileActivity.this, "Update Complete!",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, 4000);
    }


}