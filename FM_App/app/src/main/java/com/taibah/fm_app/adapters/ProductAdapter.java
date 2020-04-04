package com.taibah.fm_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_health_food.HealthFoodActivity;
import com.taibah.fm_app.databinding.ProductRowBinding;
import com.taibah.fm_app.models.ProductModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity activity;
    public ProductAdapter(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        activity = (AppCompatActivity) context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

        if (activity instanceof HealthFoodActivity)
        {
            myHolder.binding.setType(1);
        }else
            {
                myHolder.binding.setType(2);


            }

        myHolder.itemView.setOnClickListener(view -> {
            ProductModel model = list.get(myHolder.getAdapterPosition());
            if (activity instanceof HealthFoodActivity)
            {
                HealthFoodActivity healthFoodActivity = (HealthFoodActivity) activity;
                healthFoodActivity.setItemData(model);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductRowBinding binding;

        public MyHolder(@NonNull ProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
