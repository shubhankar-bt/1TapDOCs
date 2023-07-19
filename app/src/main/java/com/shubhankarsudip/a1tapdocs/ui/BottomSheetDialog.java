package com.shubhankarsudip.a1tapdocs.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhankarsudip.a1tapdocs.Models.Thoughts;
import com.shubhankarsudip.a1tapdocs.R;
import com.shubhankarsudip.a1tapdocs.Util.MyCustomAnimation;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetDialog extends BottomSheetDialogFragment {


    TextInputEditText editThought;
    PowerSpinnerView powerSpinnerView;
    TextView save, changeThoughtBtn, thought;
    ImageView back;
    List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
    String mood = " ";
    DatabaseReference reference, dbRef;
    ProgressBar pd;
    RelativeLayout relativeLayout2, relativeLayout1;
    int height;
    public String thoughtFrom;

    public BottomSheetDialog(@NotNull String thought) {
        thoughtFrom = thought;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {View v = inflater.inflate(R.layout.start_daynight_mode_dialog,
                container, false);


        //init views
        editThought = v.findViewById(R.id.editThought);
        powerSpinnerView = v.findViewById(R.id.chooseMode);
        save = v.findViewById(R.id.saveBtn);
        pd = v.findViewById(R.id.pd);
        relativeLayout2 = v.findViewById(R.id.relativeLayout2);
        relativeLayout1 = v.findViewById(R.id.relativeLayout1);
        changeThoughtBtn = v.findViewById(R.id.changeThoughtBtn);
        back = v.findViewById(R.id.back);
        thought = v.findViewById(R.id.thought);

        Log.d("FromDash", thoughtFrom);
        if (thoughtFrom != null){
            thought.setText(thoughtFrom);
        }else{
            thought.setText("No thought added");

        }


        //set views
        relativeLayout1.setVisibility(View.GONE);
        relativeLayout2.setVisibility(View.VISIBLE);

        changeThoughtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(relativeLayout2.getVisibility() == View.VISIBLE){
                    relativeLayout1.setVisibility(View.VISIBLE);
                    changeThoughtBtn.setVisibility(View.GONE);
                    relativeLayout2.setVisibility(View.GONE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(relativeLayout1.getVisibility() == View.VISIBLE){
                    relativeLayout2.setVisibility(View.VISIBLE);
                    changeThoughtBtn.setVisibility(View.VISIBLE);
                    relativeLayout1.setVisibility(View.GONE);
                }else{
                    dismiss();
                }
            }
        });






        //set spinner
        iconSpinnerItems.add(new IconSpinnerItem("Select your current mood", Drawable.createFromPath(String.valueOf(R.drawable.happy))));
        iconSpinnerItems.add(new IconSpinnerItem("Happy", Drawable.createFromPath(String.valueOf(R.drawable.happy))));
        iconSpinnerItems.add(new IconSpinnerItem("Funny", Drawable.createFromPath(String.valueOf(R.drawable.laughing))));
        iconSpinnerItems.add(new IconSpinnerItem("Sad", Drawable.createFromPath(String.valueOf(R.drawable.crying))));
        iconSpinnerItems.add(new IconSpinnerItem("Love", Drawable.createFromPath(String.valueOf(R.drawable.love))));
        iconSpinnerItems.add(new IconSpinnerItem("Confused", Drawable.createFromPath(String.valueOf(R.drawable.thinking))));
        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(powerSpinnerView);
        powerSpinnerView.setSpinnerAdapter(iconSpinnerAdapter);
        powerSpinnerView.setItems(iconSpinnerItems);
        powerSpinnerView.selectItemByIndex(0);
        powerSpinnerView.setLifecycleOwner(this);



        //onClickListeners

        powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex,
                                       IconSpinnerItem newItem) {
                mood = (String) newItem.getText();
                Log.d("SelectMood---->", mood);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thoughtText = editThought.getEditableText().toString();
                if (thoughtText.isEmpty()){
                    Snackbar.make(requireView().getRootView(), "Please write something", Snackbar.LENGTH_SHORT).show();
                }else if (mood.isEmpty()){
                    Snackbar.make(requireView().getRootView(), "Please select your current mood", Snackbar.LENGTH_SHORT).show();

                }else if (mood.equalsIgnoreCase("Select your current mood")){
                    Snackbar.make(requireView().getRootView(), "Please select your current mood", Snackbar.LENGTH_SHORT).show();
                }else if (powerSpinnerView.getSelectedIndex() == 0){
                    Snackbar.make(requireView().getRootView(), "Please select your current mood", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    addDatatoFirebase(thoughtText, mood);
                    pd.setVisibility(View.VISIBLE);
                }
            }
        });



        return v;


    }

    private void addDatatoFirebase(String thought, String mood) {
        // below 3 lines of code is used to set
        // data in our object class.
      //  thoughts.setThoughtsText(thought);
        //thoughts.setMoodText(mood);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        dbRef = reference.child("thoughts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final String uniqueKey = dbRef.push().getKey();
        Thoughts thoughts = new Thoughts();
        thoughts.setThoughts(thought);
        thoughts.setMood(mood);

        dbRef.setValue(thoughts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.setVisibility(View.GONE);
                Toast.makeText(getContext(), "added", Toast.LENGTH_SHORT).show();
                editThought.setText(null);
                powerSpinnerView.selectItemByIndex(0);
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception ) {
                pd.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
