package com.example.reme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.reme.Fragments.FaceScanFragment;
import com.example.reme.LoginDetail;
import com.example.reme.Fragments.NoteFragment;
import com.example.reme.Fragments.ProfileFragment;
import com.example.reme.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentFirebaseUser.getUid();

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
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                drawer = findViewById(R.id.drawer_layout);
                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MainActivity.this);
                View header = navigationView.getHeaderView(0);
                TextView header_name = header.findViewById(R.id.header_name);
                header_name.setText(ld.getName());

                TextView header_email = header.findViewById(R.id.header_email);
                header_email.setText(ld.getEmail());

                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                if(savedInstanceState == null) {
                    NoteFragment noteFragment = new NoteFragment();
                    Bundle b = new Bundle();
                    b.putString("uid", uid);
                    noteFragment.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteFragment).commit();
                    navigationView.setCheckedItem(R.id.nav_note);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle b = new Bundle();
        b.putString("uid", uid);

        switch (item.getItemId()){
            case R.id.nav_note:
                NoteFragment noteFragment = new NoteFragment();
                noteFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteFragment).commit();
                break;
            case R.id.nav_faceScan:
                FaceScanFragment faceScanFragment = new FaceScanFragment();
                faceScanFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, faceScanFragment).commit();
                break;
            case R.id.nav_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this, Login.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
}