package com.example.dabbawala;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import java.io.File;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> moviesList;
    PRDownloader   prDownloader;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mTextField;
        private final FrameLayout mLeftArrow;
        private final FrameLayout mRightArrow;
        private final RelativeLayout mMessageContainer;
        private final LinearLayout mMessage;
        private final int mGreen300;
        private final int mGray300;

        public MyViewHolder(View view) {
            super(view);
            mNameField = itemView.findViewById(R.id.name_text);
            mTextField = itemView.findViewById(R.id.message_text);
            mLeftArrow = itemView.findViewById(R.id.left_arrow);
            mRightArrow = itemView.findViewById(R.id.right_arrow);
            mMessageContainer = itemView.findViewById(R.id.message_container);
            mMessage = itemView.findViewById(R.id.message);
            mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
            mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
        //    prDownloader.initialize(itemView.getContext());
            context=itemView.getContext();
            PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                    .setDatabaseEnabled(true)
                    .build();
            prDownloader.initialize(itemView.getContext(), config);

        }
    }


    public ChatAdapter(List<Chat> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Chat chat = moviesList.get(position);
       int color=0;
holder. mNameField.setText(chat.getmName());
holder. mTextField.setText(chat.getmMessage());
holder.mTextField.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(chat.getFile()!=null){
         //   Uri uri=Uri.parse(chat.getFile());

            final String fileName = Constants.getDateTimeID()+".pdf";
            Log.i("CHATADAPTER", "onClick: file name="+fileName);
           prDownloader.download(chat.getFile(), Environment.getExternalStorageDirectory().getPath(), fileName)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.i("CHATADAPTER", "onDownloadComplete: file downloaded");
                            File file = new File(Environment.getExternalStorageDirectory(),
                                    fileName);
                            Uri path = Uri.fromFile(file);
                            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pdfOpenintent.setDataAndType(path, "application/pdf");
                            try {
                                context.startActivity(pdfOpenintent);
                            }
                            catch (ActivityNotFoundException e) {
e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            Log.i("CHATADAPTER", "error: file error"+error.toString());
                        }
                    });
        }
    }
});

        if (chat.getSender()) {
            color =  holder.mGreen300;
            holder. mLeftArrow.setVisibility(View.GONE);
            holder.mRightArrow.setVisibility(View.VISIBLE);
            holder.mMessageContainer.setGravity(Gravity.END);
        } else {
            color =  holder.mGray300;
            holder.mLeftArrow.setVisibility(View.VISIBLE);
            holder.mRightArrow.setVisibility(View.GONE);
            holder.mMessageContainer.setGravity(Gravity.START);
        }

        ((GradientDrawable)  holder.mMessage.getBackground()).setColor(color);

        ((RotateDrawable)  holder.mLeftArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);

        ((RotateDrawable)  holder.mRightArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}



