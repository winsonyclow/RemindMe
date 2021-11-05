package com.example.reme.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reme.R;
import com.example.reme.SimilarityClassifier;
import com.google.firebase.database.collection.LLRBNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceSettingActivity extends AppCompatActivity {

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
    int OUTPUT_SIZE=192; //Output size of model
    Context context=FaceSettingActivity.this;

    private AlertDialog dialogUpdateFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_setting);

        registered=readFromSP(); //Load saved faces from memory when app starts

        findViewById(R.id.settingLayoutUpdateFace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNameListview();
                insertToSP(registered,false);
            }
        });

        findViewById(R.id.settingLayoutDeleteFace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearnameList();
            }
        });
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String, SimilarityClassifier.Recognition> readFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        String json=sharedPreferences.getString("map",defValue);
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());

        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
        {
            float[][] output=new float[1][OUTPUT_SIZE];
            ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);

        }

        return retrievedMap;
    }

    private void updateNameListview() {
       AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(registered.isEmpty()) {
            builder.setTitle("No Faces Added!!");
            builder.setPositiveButton("OK",null);
        }
        else {
            builder.setTitle("Select Recognition to delete:");

            // add a checkbox list
            String[] names = new String[registered.size()];
            boolean[] checkedItems = new boolean[registered.size()];
            int i = 0;
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet()) {
                names[i] = entry.getKey();
                checkedItems[i] = false;
                i = i + 1;

            }

            builder.setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // user checked or unchecked a box
                    checkedItems[which] = isChecked;

                }
            });


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            registered.remove(names[i]);
                        }
                    }
                    Toast.makeText(context, "Recognitions Updated", Toast.LENGTH_SHORT).show();
                    insertToSP(registered,false);
                }
            });
            builder.setNegativeButton("Cancel", null);
        }

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.yellow_700));
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.red_500));
    }

    private  void clearnameList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_update_faces,
                (ViewGroup) findViewById(R.id.layoutUpdateFaceContainer)
        );
        if(dialogUpdateFace == null) {
            builder.setView(view);
            dialogUpdateFace = builder.create();
            if (dialogUpdateFace.getWindow() != null) {
                dialogUpdateFace.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView textUpdateFaceTitle = view.findViewById(R.id.textUpdateFaceTitle);
            TextView textUpdateFaceName = view.findViewById(R.id.textUpdateFaceName);
            TextView faceUpdateAccept = view.findViewById(R.id.faceUpdateAccept);
            TextView faceUpdateCancel = view.findViewById(R.id.faceUpdateCancel);

            textUpdateFaceTitle.setText("Delete All Face");

            if(registered.isEmpty()) {
                textUpdateFaceName.setText("No Faces Added!!");
                faceUpdateAccept.setText("OK");
                faceUpdateCancel.setVisibility(View.GONE);
                view.findViewById(R.id.faceUpdateAccept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUpdateFace.dismiss();
                    }
                });
            }
            else{
                textUpdateFaceName.setText("Do you want to delete all Recognitions?");
                faceUpdateAccept.setText("Yes");
                faceUpdateCancel.setText("No");
                faceUpdateCancel.setVisibility(View.VISIBLE);
                view.findViewById(R.id.faceUpdateAccept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registered.clear();
                        Toast.makeText(context, "Recognitions Cleared", Toast.LENGTH_SHORT).show();
                        insertToSP(registered,true);
                        dialogUpdateFace.dismiss();
                    }
                });
                view.findViewById(R.id.faceUpdateCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUpdateFace.dismiss();
                    }
                });
            }
        }
        dialogUpdateFace.show();
    }

    private void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap,boolean clear) {
        if(clear)
            jsonMap.clear();
        else
            jsonMap.putAll(registered);

        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        editor.apply();
    }
}