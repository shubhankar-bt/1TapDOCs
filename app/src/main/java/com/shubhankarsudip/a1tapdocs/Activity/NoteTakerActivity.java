package com.shubhankarsudip.a1tapdocs.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.shubhankarsudip.a1tapdocs.Models.Notes;
import com.shubhankarsudip.a1tapdocs.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteTakerActivity extends AppCompatActivity {
    RelativeLayout toolbar_notes;
    ImageButton button_save, button_back;
    EditText editText_title, editText_notes;
    Notes note;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        editText_notes = findViewById(R.id.editText_notes);
        editText_title = findViewById(R.id.editText_title);
        button_save = findViewById(R.id.button_save);
        toolbar_notes = findViewById(R.id.rl);
        button_back = findViewById(R.id.button_back);

        note = new Notes();

        try {
            note = (Notes) getIntent().getSerializableExtra("old_note");
            editText_title.setText(note.getTitle());
            editText_notes.setText(note.getNotes());
            isOldNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editText_title.getText().toString();
                String details = editText_notes.getText().toString();
                if (details.isEmpty()){
                    Toast.makeText(NoteTakerActivity.this, "Please add a note!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if (!isOldNote){
                    note = new Notes();
                }

                note.setTitle(title);
                note.setNotes(details);
                note.setDate(format.format(date));

                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}