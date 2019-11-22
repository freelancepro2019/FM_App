package com.creative.share.apps.homecare.adapters;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.creative.share.apps.homecare.models.ServicesDataModel;
import com.creative.share.apps.homecare.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;

public class Services_Adapter extends RecyclerView.Adapter<Services_Adapter.Service_Holder> {
    private List<ServicesDataModel.ServiceModel> serviceModelList;
    private Context context;
    private String current_lang;
    private Fragment_Main fragment_main;

     ServicesDataModel.ServiceModel model;

    public Services_Adapter(List<ServicesDataModel.ServiceModel> serviceModelList, Context context, Fragment_Main fragment_main) {
        this.serviceModelList = serviceModelList;
        this.context = context;
        this.fragment_main = fragment_main;
        Paper.init(context);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public Service_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_layout_row, viewGroup, false);
        return  new Service_Holder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final Service_Holder viewHolder, final int i) {
        ServicesDataModel.ServiceModel model = serviceModelList.get(i);

        viewHolder.BindData(model);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicesDataModel.ServiceModel model = serviceModelList.get(i);

                fragment_main.showSubDialog(model.getService_id());
                Log.e("id",model.getService_id()+"");


            }
        });


    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class Service_Holder extends RecyclerView.ViewHolder {
        private TextView title;
        private RoundedImageView image;

        public Service_Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            image = itemView.findViewById(R.id.image);


        }
        public void BindData(ServicesDataModel.ServiceModel serviceModel)
        {
            Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            image.setBackgroundColor(currentColor);
            title.setTextColor(currentColor);
            title.setText(serviceModel.getWords().getTitle());

            Picasso.with(context).load(Uri.parse(Tags.IMAGE_AVATAR + serviceModel.getLogo())).fit().into(image);
        }

    }

}
