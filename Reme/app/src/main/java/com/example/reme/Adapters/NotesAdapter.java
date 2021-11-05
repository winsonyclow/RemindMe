package com.example.reme.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reme.NoteDetail;
import com.example.reme.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private static Context context;
    private ArrayList<NoteDetail> notes;
    private OnItemClickListener mListener;
    private Timer timer;
    private ArrayList<NoteDetail> notesSource;

    public NotesAdapter(ArrayList<NoteDetail> notes) {
        notesSource = notes;
        this.notes = notes;
    }

    public interface OnItemClickListener{
        void OnCompleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_container_note, parent, false);
        NoteViewHolder mvh = new NoteViewHolder(v, mListener);

        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
    }

    @Override
    public int getItemCount() { return notes.size(); }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);

            layoutNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnCompleteClick(position);
                        }
                    }
                }
            });
        }

        void setNote(NoteDetail note){
            textTitle.setText(note.getTitle());
            textSubtitle.setText(note.getSubtitle());
            textDateTime.setText(note.getDateTime());

            if(note.getImageUrl() != "*"){
                Picasso.with(context).load(note.getImageUrl()).into(imageNote);
                imageNote.setVisibility(View.VISIBLE);
            }
            else{
                imageNote.setVisibility(View.GONE);
            }

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != "#2C2C2C"){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#2C2C2C"));
            }
        }

    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    notes = notesSource;
                }
                else {
                    ArrayList<NoteDetail> temp = new ArrayList<>();
                    for(NoteDetail note : notesSource){
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.getNote().toLowerCase().contains((searchKeyword.toLowerCase()))){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run(){
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
