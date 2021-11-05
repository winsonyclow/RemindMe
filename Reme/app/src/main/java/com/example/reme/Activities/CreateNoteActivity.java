package com.example.reme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reme.Broadcast.TimeReminderBroadcast;
import com.example.reme.Database.TimeDatabase;
import com.example.reme.Database.TimeEntity;
import com.example.reme.NoteDetail;
import com.example.reme.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class CreateNoteActivity extends AppCompatActivity{
    EditText inputNoteTitle, inputNoteSubtitle, inputNote;
    TextView inputNoteDateTime, inputNoteUid, inputUid;
    ImageView inputImageSave;
    View inputViewSubtitleIndicator;
    ImageView inputNoteImage;
    LinearLayout layoutBottomNav;
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;


    private String selectedNoteColor;
    public Uri imageUri = null;
    public boolean imageExisted = false;
    public boolean noteExisted = false;
    public String noteUid;
    public String uid;

    private AlertDialog dialogDeleteNote;
    private AlertDialog dialogSelectDateTime;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent i = getIntent();
        noteUid = i.getStringExtra("noteUid");

        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        uid = mFirebaseUser.getUid();

        inputNoteTitle = findViewById(R.id.editNoteTitle);
        inputNoteSubtitle = findViewById(R.id.editNoteSubtitle);
        inputNote = findViewById(R.id.editNote);
        inputNoteDateTime = findViewById(R.id.editNoteDateTime);
        inputImageSave = findViewById(R.id.editImageSave);
        inputViewSubtitleIndicator = findViewById(R.id.editViewSubtitleIndicator);
        inputNoteImage = findViewById(R.id.editNoteImage);
        inputNoteUid = findViewById(R.id.editNoteNoteUid);
        inputUid = findViewById(R.id.editNoteUid);
        layoutBottomNav = findViewById(R.id.layoutNoteBottomNav);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomNav);

        inputNoteDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        //default color
        selectedNoteColor = "#2C2C2C";

        //Get data from firebase when user clicked on existing note
        if(noteUid != null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("note");
            final NoteDetail[] d = new NoteDetail[1];
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.child("noteUid").getValue().toString().equals(noteUid)){
                            d[0] = ds.getValue(NoteDetail.class);
                        }
                    }
                    inputNoteTitle.setText(d[0].getTitle());
                    inputNoteSubtitle.setText(d[0].getSubtitle());
                    inputNote.setText(d[0].getNote());
                    inputNoteUid.setText(d[0].getNoteUid());
                    GradientDrawable gd = (GradientDrawable) inputViewSubtitleIndicator.getBackground();
                    gd.setColor(Color.parseColor(d[0].getColor()));
                    selectedNoteColor = d[0].getColor();
                    BottomColorChange(selectedNoteColor);
                    noteExisted = true;
                    findViewById(R.id.imageNoteDeleteNote).setVisibility(View.VISIBLE);

                    //fetch image
                    if(d[0].getImageUrl() != "*"){
                        Picasso.with(getApplicationContext()).load(d[0].getImageUrl()).into(inputNoteImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                findViewById(R.id.editNoteRemoveImage).setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onError() {

                            }
                        });
                        imageExisted = true;
                    }
                    else{
                        imageExisted = false;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        //Add a date time reminder
        findViewById(R.id.imageNoteReminderDateTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                addDateTimeReminder(inputNoteTitle.getText().toString(), inputNoteSubtitle.getText().toString());
            }
        });

        //Add a location reminder
        findViewById(R.id.imageNoteReminderLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                Intent i = new Intent(CreateNoteActivity.this, MapsActivity.class);
                i.putExtra("Title", inputNoteTitle.getText().toString());
                startActivity(i);
            }
        });

        //Save the note details into firebase
        inputImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
                finish();
            }
        });

        //Delete image
        findViewById(R.id.editNoteRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                inputNoteImage.setImageBitmap(null);
                inputNoteImage.setVisibility(View.GONE);
                findViewById(R.id.editNoteRemoveImage).setVisibility(View.GONE);
            }
        });

        //Delete note
        findViewById(R.id.imageNoteDeleteNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        BottomNavigation();
        initBack();
        setSubtitleIndicatorColor();
    }

    private void initBack() {
        //Go back to main activity
        ImageView imageBack = findViewById(R.id.editImageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void BottomColorChange(String color){
        final LinearLayout layoutBottomNav = findViewById(R.id.layoutNoteBottomNav);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior1 = BottomSheetBehavior.from(layoutBottomNav);
        final ImageView imageColor1 = layoutBottomNav.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutBottomNav.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutBottomNav.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutBottomNav.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutBottomNav.findViewById(R.id.imageColor5);

        switch(color){
            case "#2C2C2C" :
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                break;

            case "#BDAF2B" :
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                break;

            case "#AA401F" :
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                break;

            case "#8F0A0A" :
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                break;

            case "#1566e0" :
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                break;
        }
    }

    private void BottomNavigation() {
        //Choose color when palate icon has been clicked
        ImageView imageNoteChangeColor = findViewById(R.id.imageNoteChangeColor);
        imageNoteChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        findViewById(R.id.textNotePickColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final ImageView imageColor1 = layoutBottomNav.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutBottomNav.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutBottomNav.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutBottomNav.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutBottomNav.findViewById(R.id.imageColor5);

        layoutBottomNav.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#2C2C2C";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutBottomNav.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#BDAF2B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutBottomNav.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#AA401F";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutBottomNav.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#8F0A0A";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutBottomNav.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#1566e0";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });

        layoutBottomNav.findViewById(R.id.imageNoteAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }
                else{
                    selectImage();
                }
            }
        });

    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) inputViewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    private void selectImage(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            selectImage();
        }
        else{
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();
                if(imageUri != null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputNoteImage.setImageBitmap(bitmap);
                        inputNoteImage.setVisibility(View.VISIBLE);
                        findViewById(R.id.editNoteRemoveImage).setVisibility(View.VISIBLE);
                    }
                    catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void uploadImage(String title, String subtitle, String note, String uid, String dateTime, String noteUid, String selectedNoteColor){
        if(imageExisted){
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("images").child(noteUid + ".jpg");

            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "onFailure: did not delete file");
                }
            });
        }

        if(imageUri != null){
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("images").child(noteUid + ".jpg");
            UploadTask uploadTask = storageReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        NoteDetail noteDetail = new NoteDetail(title, subtitle, note, uid, dateTime, noteUid, selectedNoteColor, downloadUrl.toString());

                        //Save the data into realtime database
                        rootNode = FirebaseDatabase.getInstance();
                        reference = rootNode.getReference("note");
                        reference.child(noteUid).setValue(noteDetail);
                    }
                }
            });
            NoteDetail noteDetail = new NoteDetail(title, subtitle, note, uid, dateTime, noteUid, selectedNoteColor, imageUri.toString());

            //Save the data into realtime database
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("note");
            reference.child(noteUid).setValue(noteDetail);
        }
        else{
            NoteDetail noteDetail = new NoteDetail(title, subtitle, note, uid, dateTime, noteUid, selectedNoteColor, "*");

            //Save the data into realtime database
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("note");
            reference.child(noteUid).setValue(noteDetail);
        }
    }

    private void deleteNote(){
        if(dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );

            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageExisted){
                        storage = FirebaseStorage.getInstance();
                        storageReference = storage.getReference().child("images").child(noteUid + ".jpg");

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: deleted file");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG, "onFailure: did not delete file");
                            }
                        });

                    }
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("note").child(noteUid);
                    reference.removeValue();
                    Toast.makeText(CreateNoteActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
                    finish();
                }
            });

            view.findViewById(R.id.textCancelNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }

        dialogDeleteNote.show();

    }

    private void saveNote(){
        String title = inputNoteTitle.getText().toString();
        String subtitle = inputNoteSubtitle.getText().toString();
        String note = inputNote.getText().toString();

        if(title.isEmpty() && subtitle.isEmpty() && note.isEmpty()){
            Toast.makeText(CreateNoteActivity.this, "Fields are empty!",Toast.LENGTH_SHORT).show();
        }
        else if(title.isEmpty()){
            inputNoteTitle.setError("Please enter the note's title");
            inputNoteTitle.requestFocus();
        }
        else if(subtitle.isEmpty()){
            inputNoteSubtitle.setError("Please enter the note's subtitle");
            inputNoteSubtitle.requestFocus();
        }
        else if(note.isEmpty()){
            inputNote.setError("Note cannot be empty");
            inputNote.requestFocus();
        }
        else{
            //upload image
            if(noteUid == null){
                UUID generatedNoteUid = UUID.randomUUID();
                noteUid = generatedNoteUid.toString();
            }
            uploadImage(title, subtitle, note, uid, inputNoteDateTime.getText().toString(), noteUid, selectedNoteColor);

            //Toast a message
            if(noteExisted){
                Toast.makeText(CreateNoteActivity.this, "Note Updated!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(CreateNoteActivity.this, "Note Created!",Toast.LENGTH_SHORT).show();
                noteExisted = true;
            }
        }
    }

    private void addDateTimeReminder(String title, String subtitle){
        if(dialogSelectDateTime == null) {
            Calendar cTime = Calendar.getInstance();
            final int hour = cTime.get(Calendar.HOUR_OF_DAY);
            final int minute = cTime.get(Calendar.MINUTE);
            Calendar cDate = Calendar.getInstance();
            final int year = cDate.get(Calendar.YEAR);
            final int month = cDate.get(Calendar.MONTH);
            final int day = cDate.get(Calendar.DAY_OF_MONTH);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_select_datetime,
                    (ViewGroup) findViewById(R.id.layoutSelectDateTimeContainer)
            );

            builder.setView(view);
            dialogSelectDateTime = builder.create();
            if (dialogSelectDateTime.getWindow() != null) {
                dialogSelectDateTime.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView textDateReminder = view.findViewById(R.id.textDateReminder);
            TextView textTimeReminder = view.findViewById(R.id.textTimeReminder);

            view.findViewById(R.id.textDateReminder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            CreateNoteActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    cDate.set(Calendar.YEAR, year);
                                    cDate.set(Calendar.MONTH, month);
                                    cDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    textDateReminder.setText(df.format(cDate.getTime()));
                                }
                            },year,month,day
                    );

                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });

            view.findViewById(R.id.textTimeReminder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            CreateNoteActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    cTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    cTime.set(Calendar.MINUTE, minute);
                                    cTime.set(Calendar.SECOND, 0);

                                    SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    textTimeReminder.setText(df.format(cTime.getTime()));
                                }
                            },hour,minute,false
                    );

                    timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    timePickerDialog.show();
                }
            });

            view.findViewById(R.id.textApplyReminder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAlarm(cDate, cTime, title, subtitle);
                    dialogSelectDateTime.dismiss();
                }
            });

            view.findViewById(R.id.textCancelReminder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSelectDateTime.dismiss();
                }
            });
        }

        dialogSelectDateTime.show();

    }

    private void startAlarm(Calendar date, Calendar time, String title, String subtitle){
        //set all data in local database
        /*TimeEntity timeEntity = new TimeEntity(noteUid, title, date, time);
        TimeDatabase timeDatabase = TimeDatabase.getInstance(getApplicationContext());
        timeDatabase.timeDao().insert(timeEntity);*/

        Calendar alarmDateTime = Calendar.getInstance();
        alarmDateTime.setTimeInMillis(System.currentTimeMillis());
        alarmDateTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, TimeReminderBroadcast.class);
        i.putExtra("Title", title);
        i.putExtra("Subtitle", subtitle);
        i.putExtra("ID", noteUid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,i,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmDateTime.getTimeInMillis(), pendingIntent);


    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, TimeReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,i,0);

        alarmManager.cancel(pendingIntent);
    }


}