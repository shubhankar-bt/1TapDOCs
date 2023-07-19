package com.shubhankarsudip.a1tapdocs.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shubhankarsudip.a1tapdocs.R;
import com.shubhankarsudip.a1tapdocs.ui.View_PDF_Files;
import com.shubhankarsudip.a1tapdocs.ui.uploadPDF;
import com.shubhankarsudip.a1tapdocs.uploadCARDS;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PdfListAdapter2 extends RecyclerView.Adapter<PdfListAdapter2.PdfListHolder> {

    private List<uploadPDF> list;
    private Context context;
    InputStream stream;


    public PdfListAdapter2(List<uploadPDF> list, Context context) {
        this.list = list;
        this.context = context;
    }




    @NonNull
    @Override
    public PdfListAdapter2.PdfListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_home_layout, parent,false);

        return new PdfListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfListAdapter2.PdfListHolder holder, int position) {
        uploadPDF item = list.get(position);


        holder.name.setText(item.getName());

        String url = item.url;

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
                .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .pageSnap(false) // snap pages to screen boundaries
                .pageFling(false)
                .load();
        new RetrivePDFfromUrl(holder.imgPdf).execute(url);
        holder.moreBtn.setVisibility(View.GONE);

    }



    @Override
    public int getItemCount() {
        return list.size();

    }


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
                                .enableSwipe(true)
                                .pageFitPolicy(FitPolicy.BOTH)
                                .enableAntialiasing(true)
                                .load();

                    }
                }
            }
        }
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
