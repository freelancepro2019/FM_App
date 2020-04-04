package com.taibah.fm_app.activities_fragments.activity_session_reservations.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_session_details.SessionDetailsActivity;
import com.taibah.fm_app.activities_fragments.activity_session_reservations.SessionReservationActivity;
import com.taibah.fm_app.adapters.SessionsAdapter;
import com.taibah.fm_app.databinding.FragmentOrderPendingCurrentBinding;
import com.taibah.fm_app.models.SessionModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Fragment_Session_Pending_Order extends Fragment {
    private FragmentOrderPendingCurrentBinding binding;
    private SessionReservationActivity activity;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private List<SessionModel> sessionModelList;
    private SessionsAdapter adapter;
    private DatabaseReference dRef;


    public static Fragment_Session_Pending_Order newInstance() {

        return new Fragment_Session_Pending_Order();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_pending_current, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_SESSIONS);
        sessionModelList = new ArrayList<>();

        activity = (SessionReservationActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);

        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");


        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        adapter = new SessionsAdapter(sessionModelList, activity,this);
        binding.recView.setAdapter(adapter);

        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary,R.color.red,R.color.green,R.color.color8);
        binding.swipeRefresh.setOnRefreshListener(this::getData);

        getData();


    }

    private void getData() {

        dRef.child(userModel.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        sessionModelList.clear();
                        adapter.notifyDataSetChanged();

                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                SessionModel model = ds.getValue(SessionModel.class);

                                if (model != null && !model.isAccepted()) {
                                    sessionModelList.add(model);
                                }
                            }

                            if (sessionModelList.size() > 0) {
                                binding.tvNoOrder.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            } else {
                                binding.tvNoOrder.setVisibility(View.VISIBLE);

                            }
                        } else {
                            binding.tvNoOrder.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void setItemData(SessionModel model) {
        Intent intent = new Intent(activity, SessionDetailsActivity.class);
        intent.putExtra("data",model);
        startActivity(intent);
    }
}
