package com.day.star.apps.homecare.activities_fragments.activity_order_details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.day.star.apps.homecare.R;
import com.day.star.apps.homecare.databinding.ActivityOrderDetailsBinding;
import com.day.star.apps.homecare.interfaces.Listeners;
import com.day.star.apps.homecare.language.LanguageHelper;
import com.day.star.apps.homecare.models.SingleOrderDataModel;
import com.day.star.apps.homecare.models.UserModel;
import com.day.star.apps.homecare.preferences.Preferences;
import com.day.star.apps.homecare.remote.Api;
import com.day.star.apps.homecare.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityOrderDetailsBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    private String order_id;
    private String from;
    private SingleOrderDataModel.OrderModel orderModel= null;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("order_id"))
        {
            order_id = intent.getStringExtra("order_id");
            from = intent.getStringExtra("from");

        }
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setUserId(userModel.getUser_id());
        binding.setFrom(from);
        binding.btnCall.setOnClickListener(view ->
        {
            String phone;

            if (userModel.getUser_type().equals("1"))
            {
                if (orderModel.getProvider()==null)
                {
                    Toast.makeText(this,R.string.order_is_pending, Toast.LENGTH_SHORT).show();
                }else
                    {
                        phone = orderModel.getProvider().getPhone_code()+orderModel.getProvider().getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                        startActivity(intent);
                    }

            }else
                {
                    phone = orderModel.getClient().getPhone_code()+orderModel.getClient().getPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                    startActivity(intent);
                }


        });

        binding.btnWhats.setOnClickListener(view ->
        {

            String phone;

            if (userModel.getUser_type().equals("1"))
            {
                if (orderModel.getProvider()==null)
                {
                    Toast.makeText(this,R.string.order_is_pending, Toast.LENGTH_SHORT).show();
                }else
                {
                    phone = orderModel.getProvider().getPhone_code()+orderModel.getProvider().getPhone();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+phone));
                    startActivity(intent);
                }

            }else
            {
                phone = orderModel.getProvider().getPhone_code()+orderModel.getProvider().getPhone();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+phone));
                startActivity(intent);
            }






        });

        binding.btnAccept.setOnClickListener(view ->
                accept()
                );

        binding.btnRefuse.setOnClickListener(view ->
                refuse()
        );
        getOrderData();

    }

    private void accept() {
        Api.getService(Tags.base_url)
                .providerAcceptOrder(lang,userModel.getToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void refuse() {

        Api.getService(Tags.base_url)
                .providerRefuseOrder(lang,userModel.getToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null )
                        {
                            Toast.makeText(OrderDetailsActivity.this, R.string.suc, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }



    private void getOrderData()
    {

        Api.getService(Tags.base_url).
                getOrderDetails(lang,userModel.getToken(),order_id).
                enqueue(new Callback<SingleOrderDataModel>() {
                    @Override
                    public void onResponse(Call<SingleOrderDataModel> call, Response<SingleOrderDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            orderModel = response.body().getOrder();
                            binding.setOrderModel(response.body().getOrder());
                            binding.llContainer.setVisibility(View.VISIBLE);
                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleOrderDataModel> call, Throwable t) {

                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }



                    }
                });
    }


    @Override
    public void back() {
        finish();
    }

}
