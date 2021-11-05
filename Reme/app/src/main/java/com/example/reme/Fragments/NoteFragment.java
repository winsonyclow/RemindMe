package com.example.reme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.reme.NoteDetail;
import com.example.reme.R;
import com.example.reme.Activities.CreateNoteActivity;
import com.example.reme.Adapters.NotesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteFragment extends Fragment{

    DatabaseReference mDatabaseReference;

    private NotesAdapter notesAdapter;
    private RecyclerView notesRecyclerView;
    private ArrayList<NoteDetail> noteDetails = new ArrayList<>();
    private String noteUid;

    ImageView imageAddNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        Bundle b = getArguments();
        String uid = b.getString("uid");

        imageAddNote = view.findViewById(R.id.imageAddNote);

        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        //Fetch data from the firebase that based on users' uid
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("note");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteDetails.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(noteDetails.size()>0){
                        boolean contain = false;
                        for(NoteDetail n : noteDetails) {
                            if(n.getNoteUid().equals(ds.child("noteUid").getValue().toString())) {
                                contain = true;
                            }
                        }
                        if(!contain){
                            if(ds.child("uid").getValue().toString().equals(uid)){
                                noteDetails.add(ds.getValue(NoteDetail.class));
                            }
                        }
                    }
                    else{
                        if(ds.child("uid").getValue().toString().equals(uid)){
                            noteDetails.add(ds.getValue(NoteDetail.class));
                        }
                    }
                }

                notesAdapter = new NotesAdapter(noteDetails);
                notesRecyclerView.setAdapter(notesAdapter);

                notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
                    @Override
                    public void OnCompleteClick(int position) {
                        noteUid = noteDetails.get(position).getNoteUid();
                        Intent i = new Intent(getActivity(), CreateNoteActivity.class);
                        i.putExtra("noteUid", noteUid);
                        startActivity(i);
                        getActivity().finish();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //Search note
        EditText inputSearch = view.findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noteDetails.size() != 0){
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });

        //Create a new note
        imageAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateNoteActivity.class);
                i.putExtra("uid", uid);
                startActivity(i);
                getActivity().finish();
            }
        });

        return view;
    }
}

