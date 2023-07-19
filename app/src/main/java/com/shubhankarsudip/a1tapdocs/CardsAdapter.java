package com.shubhankarsudip.a1tapdocs;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewAdapter> {

    private List<uploadCARDS> list;
    private Context context;
    private int typeOfCard;
    DatabaseReference reference, dbRef;


    public CardsAdapter(List<uploadCARDS> list, Context context, int type) {
        this.list = list;
        this.context = context;
        this.typeOfCard = type;
    }

    @NonNull
    @Override
    public CardsViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_items_layout, parent,false);

        return new CardsViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewAdapter holder, int position) {

        uploadCARDS item = list.get(position);
        String productId = item.getUniqueKey();

        holder.name.setText(item.getCardSaverName());
        holder.cardNo.setText(item.getCardNumber());
        holder.Bname.setText(item.getBankName());
        holder.Expiry.setText(item.getExpDate());
        holder.usercvv.setText(item.getCVVno());

        if (typeOfCard == 0){
           // holder.cardBac.setBackgroundResource(R.drawable.debit_card2);
            holder.cardBac.setBackground(ContextCompat.getDrawable(context, R.drawable.debit_card2));

        }else if (typeOfCard == 1){
            holder.cardBac.setBackground(ContextCompat.getDrawable(context, R.drawable.credit_card));
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.moreBtn);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_card);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.download:
                                //handle menu1 click
                                Bitmap bitmap = getScreenShotFromView(holder.cardView);

                                // if bitmap is not null then
                                // save it to gallery
                                if (bitmap != null) {
                                    saveMediaToStorage(bitmap);
                                }
                                return true;
                            case R.id.delete:
                                if (productId != null){
                                    deleteDialog(productId);
                                }else{
                                    Toast.makeText(v.getContext(),"Sorry, this item can't be deleted", Toast.LENGTH_SHORT).show();
                                }

                                //handle menu2 click

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

    }

    private void saveMediaToStorage(Bitmap bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + context.getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + context.getString(R.string.app_name));

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                Toast.makeText(context, "Card saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void deleteDialog(String id) {
        CharSequence options[]=new CharSequence[]{
                // select any from the value
                "Yes",
                "Cancel",
        };
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Are you sure?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if delete option is choosed
                // then call delete function
                if(which==0) {
                    deleteCard(id);
                }

            }
        });
        builder.show();
    }

    private Bitmap getScreenShotFromView(View v) {
        Bitmap screenshot = null;
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            // Now draw this bitmap on a canvas
            Canvas canvas = new Canvas(screenshot);
            v.draw(canvas);
        } catch (Exception e) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.getMessage());
        }


        return screenshot;
    }

    private void deleteCard(String id) {
        reference = FirebaseDatabase.getInstance().getReference().child("World").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (typeOfCard == 0){
            dbRef= reference.child("Debit Card");
        }else if (typeOfCard == 1){
            dbRef= reference.child("Credit Card");
        }

        // we are use add listerner
        // for event listener method
        // which is called with query.
        Query query=dbRef.child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed "+String.valueOf(databaseError), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardsViewAdapter extends RecyclerView.ViewHolder {

        private TextView name,cardNo,Bname,Expiry , usercvv ;
        private RelativeLayout cardBac;
        private ImageView moreBtn;
        private MaterialCardView cardView;

        public CardsViewAdapter(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.UserCardName2);
            cardNo = itemView.findViewById(R.id.UserCardNumber2);
            Bname = itemView.findViewById(R.id.UserBankName2);
            Expiry = itemView.findViewById(R.id.UserCardExpiryDate2);
            usercvv = itemView.findViewById(R.id.UserCardCvvNumber2);
            cardBac = itemView.findViewById(R.id.cardBac);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            cardView = itemView.findViewById(R.id.cardView8);


           // category = itemView.findViewById(R.id.UserCardCategory);



        }
    }
}
