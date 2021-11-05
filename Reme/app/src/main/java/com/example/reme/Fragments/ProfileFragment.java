package com.example.reme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reme.LoginDetail;
import com.example.reme.R;
import com.example.reme.Activities.EditProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle b = getArguments();
        String uid = b.getString("uid");
        final LoginDetail[] d = new LoginDetail[1];

        TextView textProfileName, textProfileEmail, textProfilePhoneNumber;
        LinearLayout layoutProfileEdit, layoutProfileDelete;

        textProfileName = view.findViewById(R.id.textProfileName);
        textProfileEmail = view.findViewById(R.id.textProfileEmail);
        textProfilePhoneNumber = view.findViewById(R.id.textProfilePhoneNumber);
        layoutProfileEdit = view.findViewById(R.id.layoutProfileEdit);
        layoutProfileDelete = view.findViewById(R.id.layoutProfileDelete);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                textProfileName.setText(ld.getName());
                textProfileEmail.setText(ld.getEmail());
                textProfilePhoneNumber.setText(ld.getPhoneNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        layoutProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });



        return view;
    }


}