package com.taibah.fm_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_session_reservations.fragments.Fragment_Session_Current_Order;
import com.taibah.fm_app.activities_fragments.activity_session_reservations.fragments.Fragment_Session_Pending_Order;
import com.taibah.fm_app.databinding.SessionReservationRowBinding;
import com.taibah.fm_app.models.SessionModel;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SessionModel> list;
    private Context context;
    private LayoutInflater inflater;
    private int[] resource = {R.drawable.circle_color1, R.drawable.circle_color2, R.drawable.circle_color3, R.drawable.circle_color4, R.drawable.circle_color5, R.drawable.circle_color6};
    private Fragment fragment;
    public SessionsAdapter(List<SessionModel> list, Context context,Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SessionReservationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.session_reservation_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        int pos = position % list.size();
        myHolder.binding.fl.setBackgroundResource(resource[pos]);
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setId(position + 1);

        myHolder.itemView.setOnClickListener(view -> {

            SessionModel model = list.get(myHolder.getAdapterPosition());

            if (fragment instanceof Fragment_Session_Pending_Order)
            {
                Fragment_Session_Pending_Order fragment_session_pending_order = (Fragment_Session_Pending_Order) fragment;
                fragment_session_pending_order.setItemData(model);
            }else if (fragment instanceof Fragment_Session_Current_Order)
            {
                Fragment_Session_Current_Order fragment_session_current_order = (Fragment_Session_Current_Order) fragment;
                fragment_session_current_order.setItemData(model);
            }



        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SessionReservationRowBinding binding;

        public MyHolder(@NonNull SessionReservationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
