package com.shubhankarsudip.a1tapdocs.ui.slideshow;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhankarsudip.a1tapdocs.CardsAdapter;
import com.shubhankarsudip.a1tapdocs.R;
import com.shubhankarsudip.a1tapdocs.ViewCardActivity;
import com.shubhankarsudip.a1tapdocs.uploadCARDS;

import java.util.ArrayList;
import java.util.List;

public class CreditCardFragment extends Fragment {

    RecyclerView CreditCardPart;
    LinearLayout CreditNoData;
    private DatabaseReference reference,dbRef;
    private CardsAdapter adapter;
    ProgressBar ProgressBarloader;
    private List<uploadCARDS> list2;



    public CreditCardFragment() {
        // required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debit_cards, container, false);


        CreditCardPart = view.findViewById(R.id.DebitCardR);
        CreditNoData = view.findViewById(R.id.DebitCardNoData);
        ProgressBarloader = view.findViewById(R.id.cardViewLoader);

        reference = FirebaseDatabase.getInstance().getReference().child("World").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        fetchDebitCards();



        return view;
    }

    private void fetchDebitCards() {
        ProgressBarloader.setVisibility(View.VISIBLE);
        dbRef= reference.child("Credit Card");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                list2 = new ArrayList<>();
                if(!datasnapshot.exists()){
                    CreditNoData.setVisibility(View.VISIBLE);
                    CreditCardPart.setVisibility(View.GONE);
                    ProgressBarloader.setVisibility(View.GONE);
                }else {
                    CreditNoData.setVisibility(View.GONE);
                    CreditCardPart.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        uploadCARDS data = snapshot.getValue(uploadCARDS.class);
                        list2.add(data);
                    }
                    ProgressBarloader.setVisibility(View.GONE);
                    CreditCardPart.setHasFixedSize(true);
                    CreditCardPart.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new CardsAdapter(list2, getContext(), 1);
                    CreditCardPart.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                ProgressBarloader.setVisibility(View.GONE);
            }
        });
    }
}