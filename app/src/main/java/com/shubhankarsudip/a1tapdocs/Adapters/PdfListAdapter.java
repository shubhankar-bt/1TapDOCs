package com.shubhankarsudip.a1tapdocs.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.shubhankarsudip.a1tapdocs.R;
import com.shubhankarsudip.a1tapdocs.ui.uploadPDF;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.PdfListHolder> {

    private List<uploadPDF> list;
    private Context context;
    InputStream stream;

    DatabaseReference reference, dbRef;



    public PdfListAdapter(List<uploadPDF> list, Context context) {
        this.list = list;
        this.context = context;
    }




    @NonNull
    @Override
    public PdfListAdapter.PdfListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_home_layout, parent,false);

        return new PdfListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfListAdapter.PdfListHolder holder, int position) {
        uploadPDF item = list.get(position);


        holder.name.setText(item.getName());
        holder.moreBtn.setVisibility(View.GONE);

        String url = item.url;
        String key = item.uniqeKey;

        Uri finalUri = Uri.parse(url);
        holder.imgPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (finalUri.toString().contains(".doc") || finalUri.toString().contains(".docx")) {
                        // Word document
                        intent.setDataAndType(finalUri, "application/msword");
                    } else if (finalUri.toString().contains(".pdf")) {
                        // PDF file
                        intent.setDataAndType(finalUri, "application/pdf");
                    } else if (finalUri.toString().contains(".ppt") || finalUri.toString().contains(".pptx")) {
                        // Powerpoint file
                        intent.setDataAndType(finalUri, "application/vnd.ms-powerpoint");
                    } else if (finalUri.toString().contains(".xls") || finalUri.toString().contains(".xlsx")) {
                        // Excel file
                        intent.setDataAndType(finalUri, "application/vnd.ms-excel");
                    } else {
                        intent.setDataAndType(finalUri, "*/*");
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }catch (Exception e){
                    Log.d("----->", e.getMessage());
                }

            }
        });

        holder.imgPdf.fromAsset("pdf_holder.pdf")
                .defaultPage(1)
                .enableSwipe(true)
                .enableAntialiasing(true)
                .pageFitPolicy(FitPolicy.BOTH) // mode to fit pages in the view
                .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .pageSnap(false) // snap pages to screen boundaries
                .pageFling(false)
                .load();

        new RetrivePDFfromUrl(holder.imgPdf).execute(url);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.moreBtn);
                //inflating menu from xml resource
                popup.inflate(R.menu.delete_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete1:
                                if (key != null){
                                    deleteDialog(key, holder.getAdapterPosition());
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



    // create an async task class for loading pdf file from URL.
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {

        private final WeakReference<PDFView> imageViewReference;

        public RetrivePDFfromUrl(PDFView imageView) {
            imageViewReference = new WeakReference<PDFView>(imageView);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            if (imageViewReference != null) {
                PDFView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (inputStream != null) {
                        imageView.fromStream(inputStream)
                                .enableAntialiasing(true)
                                .pageFling(true)
                                .fitEachPage(true)
                                .pageFitPolicy(FitPolicy.WIDTH)
                                .load();

                    } else {

                        imageView.fromAsset("pdf_holder.pdf")
                                .defaultPage(1)
                                .pageFitPolicy(FitPolicy.BOTH)
                                .enableSwipe(true)
                                .enableAntialiasing(true)
                                .load();


                    }
                }
            }
        }
    }


    private void deleteDialog(String id, int pos) {
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
                    deleteCard(id, pos);
                }

            }
        });
        builder.show();
    }

    private void deleteCard(String id, int pos) {
        reference = FirebaseDatabase.getInstance().getReference().child("uploads").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // we are use add listerner
        // for event listener method
        // which is called with query.
        Query query=reference.child(id);
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

    public class PdfListHolder extends RecyclerView.ViewHolder {

        private TextView name;
        RelativeLayout layout5;
        PDFView imgPdf;
        ImageView moreBtn;


        public PdfListHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textview4);
            layout5 = itemView.findViewById(R.id.layout5);
            imgPdf = itemView.findViewById(R.id.pdfimage);
            moreBtn = itemView.findViewById(R.id.more);




        }
    }


}
