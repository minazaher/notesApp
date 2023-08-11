package com.example.notesapp.adapters;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notesapp.Activity.CreateNoteActivity;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.Viewholder> {

    List<Note> notes;
    Context context;

    public NotesAdapter(Context context,List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(
                LayoutInflater
                        .from(parent.getContext())
                .inflate
                        (R.layout.note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class Viewholder extends RecyclerView.ViewHolder{
        TextView title, subtitle, date;
        LinearLayout layout;
        RoundedImageView imageView;
         public Viewholder(@NonNull View itemView) {
             super(itemView);
             title = itemView.findViewById(R.id.tv_title);
             subtitle = itemView.findViewById(R.id.tv_subtitle);
             date = itemView.findViewById(R.id.tv_dateTime);
             layout = itemView.findViewById(R.id.layout_note);
             imageView = itemView.findViewById(R.id.note_imagePreview);
         }
         void setNote(Note note){
             title.setText(note.getTitle());
             subtitle.setText(note.getSubtitle());
             date.setText(note.getDateTime());
             GradientDrawable gradientDrawable = (GradientDrawable) layout.getBackground();
             if (note.getColor() == null){
                 gradientDrawable.setColor(Color.parseColor("#333333"));
             }
             else {
                 gradientDrawable.setColor(Color.parseColor(note.getColor()));
             }

             if (note.getImagePath() == null)
                 imageView.setVisibility(View.GONE);
             else{
                 imageView.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                 imageView.setVisibility(View.VISIBLE);
             }
         }
     }

}
