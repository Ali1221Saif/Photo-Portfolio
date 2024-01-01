package com.example.photoportfolio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PictureAdapter  extends ArrayAdapter<AssetModal> {

    public PictureAdapter(@NonNull Context context, ArrayList<AssetModal> sectionModelArrayList) {
        super(context, 0, sectionModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_picture, parent, false);
        }

        AssetModal assetModal = getItem(position);
        TextView assetTV = listitemView.findViewById(R.id.idTVSection);
        ImageView assetIV = listitemView.findViewById(R.id.idIVsection);
        TextView urlText = listitemView.findViewById(R.id.idUrl);
        TextView urlType = listitemView.findViewById(R.id.imgType);

        assetTV.setText(assetModal.getSectionName());
        if (!assetModal.getU().isEmpty()) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        return BitmapFactory.decodeStream(new URL(assetModal.getU()).openStream());
                    } catch (IOException e) {
                        Log.e("YourTag", "Error loading image from URL: " + e.getMessage(), e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        assetIV.setImageBitmap(bitmap);
                        urlText.setText(assetModal.getU());
                        urlType.setText(assetModal.getType());
                    } else {
                        Log.e("YourTag", "Bitmap is null, image couldn't be loaded from URL");
                    }
                }
            }.execute();
        } else {
            assetIV.setImageURI(assetModal.getImgid());
        }
        return listitemView;
    }



}
