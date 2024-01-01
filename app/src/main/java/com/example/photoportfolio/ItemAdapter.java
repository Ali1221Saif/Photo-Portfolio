package com.example.photoportfolio;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
public class ItemAdapter extends ArrayAdapter<SectionModal>{

    public ItemAdapter(@NonNull Context context, ArrayList<SectionModal> sectionModelArrayList) {
        super(context, 0, sectionModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_main, parent, false);
        }

        SectionModal sectionModel = getItem(position);
        TextView sectionTV = listitemView.findViewById(R.id.idTVSection);
        ImageView sectionIV = listitemView.findViewById(R.id.idIVsection);

        sectionTV.setText(sectionModel.getSectionName());
        sectionIV.setImageResource(sectionModel.getImgid());
        return listitemView;
    }

}
