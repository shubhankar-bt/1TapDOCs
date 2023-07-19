package com.shubhankarsudip.a1tapdocs.Listeners;

import androidx.cardview.widget.CardView;

import com.shubhankarsudip.a1tapdocs.Models.Notes;


public interface NoteClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
