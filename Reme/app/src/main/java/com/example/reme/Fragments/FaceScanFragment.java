package com.example.reme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reme.Activities.AddFaceActivity;
import com.example.reme.Activities.FaceSettingActivity;
import com.example.reme.Activities.ScanFaceActivity;
import com.example.reme.R;

public class FaceScanFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_scan, container, false);

        Bundle b = getArguments();
        String uid = b.getString("uid");

        view.findViewById(R.id.layoutAddFace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFaceActivity.class));
            }
        });

        view.findViewById(R.id.layoutScanFace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanFaceActivity.class));
            }
        });

        view.findViewById(R.id.layoutSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FaceSettingActivity.class));
            }
        });

        return view;
    }
}
