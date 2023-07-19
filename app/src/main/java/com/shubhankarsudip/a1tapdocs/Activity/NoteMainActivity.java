package com.shubhankarsudip.a1tapdocs.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shubhankarsudip.a1tapdocs.Adapters.NotesListAdapter;
import com.shubhankarsudip.a1tapdocs.DashboardActivity;
import com.shubhankarsudip.a1tapdocs.Database.RoomDB;
import com.shubhankarsudip.a1tapdocs.Listeners.NoteClickListener;
import com.shubhankarsudip.a1tapdocs.Models.Notes;
import com.shubhankarsudip.a1tapdocs.R;

import java.util.ArrayList;
import java.util.List;

public class NoteMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    Toolbar toolbar;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    Notes selectedNote;
    SearchView search_view_home;
    TextView textView_placeholder;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LAYOUT = "layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_main);


        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        recyclerView = findViewById(R.id.recycler_notes);
        toolbar = findViewById(R.id.toolbar_home);
        fab_add = findViewById(R.id.fab_add);
        search_view_home = findViewById(R.id.search_view_home);
        textView_placeholder = findViewById(R.id.textView_placeholder);


        if(toolbar.getOverflowIcon()!=null)
            toolbar.getOverflowIcon().setTint(Color.WHITE);
        toolbar.inflateMenu(R.menu.home_menu);

        database = RoomDB.getInstance(this);
        notes =database.mainDAO().getAll();

        updateRecycler(loadLayoutStyle());


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteMainActivity.this, NoteTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        search_view_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.layout:
                        if (loadLayoutStyle().equals("linear")){
                            saveLayoutStyle("grid");
                            updateRecycler(loadLayoutStyle());
                        }
                        else {
                            saveLayoutStyle("linear");
                            updateRecycler(loadLayoutStyle());
                        }

                        Toast.makeText(NoteMainActivity.this, "Layout updated!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.pinned:
                        Toast.makeText(NoteMainActivity.this, "Will be available soon!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(NoteMainActivity.this, "Will be available soon!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    private void saveLayoutStyle(String layout) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LAYOUT, layout);
        editor.apply();
    }
    private String loadLayoutStyle() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String layout = sharedPreferences.getString(LAYOUT, "");
        return layout;
    }

    private void updateRecycler(String layout){
        if (!notes.isEmpty()){
            textView_placeholder.setVisibility(View.GONE);
        }
        recyclerView.setHasFixedSize(true);
        if (layout.equals("linear")){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        }
        notesListAdapter = new NotesListAdapter(this, notes, noteClickListener);
        recyclerView.setAdapter(notesListAdapter);
        layoutAnimation(recyclerView);
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes){
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList, newText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101){
            if(resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_note);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                this.recreate();
                if (!notes.isEmpty()){
                    textView_placeholder.setVisibility(View.GONE);
                }

            }
            return;
        }
        else if (requestCode==102){
            if(resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_note.getID(), new_note.getTitle(), new_note.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                this.recreate();
                if (!notes.isEmpty()){
                    textView_placeholder.setVisibility(View.GONE);
                }
            }
            return;
        }
        Toast.makeText(NoteMainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();

    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(NoteMainActivity.this, NoteTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }
    };

    public void showPopup(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }


    private void layoutAnimation(RecyclerView recyclerView){
        Context context = recyclerView.getContext();
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycler_anim);
        recyclerView.setLayoutAnimation(animationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pin:
                if (selectedNote.isStarred()){
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(NoteMainActivity.this, "Unpinned!", Toast.LENGTH_SHORT).show();
                }
                else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(NoteMainActivity.this, "Pinned!", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                this.recreate();
                Toast.makeText(NoteMainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onBackPressed(){
        startActivity(new Intent(NoteMainActivity.this, DashboardActivity.class));
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}