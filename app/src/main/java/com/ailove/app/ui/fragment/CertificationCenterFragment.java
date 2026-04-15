package com.ailove.app.ui.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.VerifyResult;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;
import com.ailove.app.utils.LocalHttpServerManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import android.content.Intent;
import com.ailove.app.ui.activity.CameraCertificationActivity;
import com.ailove.app.ui.activity.PhoneSettingsActivity;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Call;

public class CertificationCenterFragment extends Fragment {

    private LinearLayout itemPhone, itemIdentity, itemPhoto, itemVehicle, itemEducation, itemAssets, itemCertificates;
    private TextView btnPhone, btnIdentity, btnPhoto, btnVideo, btnVehicle, btnEducation, btnAssets, btnCertificates;
    private EditText etName, etIdCard;
    
    private JsonObject certificationData = new JsonObject();
    private OkHttpClient httpClient = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certification_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadCertificationFromServer();
        
        btnIdentity.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String idCard = etIdCard.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idCard)) {
                Toast.makeText(getContext(), "请输入姓名与身份证号码", Toast.LENGTH_SHORT).show();
                return;
            }
            certificationData.addProperty("identity_name", name);
            certificationData.addProperty("identity_idcard", idCard);
            certificationData.addProperty("identity_status", "pending");
            certificationData.addProperty("identity_time", System.currentTimeMillis());
            saveCertificationToServer();
            Toast.makeText(getContext(), "已提交实名认证申请", Toast.LENGTH_SHORT).show();
        });

        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            startActivity(intent);
        });

        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            intent.putExtra("mode", "video");
            startActivity(intent);
        });

        btnVehicle.setOnClickListener(v -> {
            Toast.makeText(getContext(), "行驶证认证开发中", Toast.LENGTH_SHORT).show();
        });

        btnEducation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "学历认证开发中", Toast.LENGTH_SHORT).show();
        });

        btnAssets.setOnClickListener(v -> {
            Toast.makeText(getContext(), "资产认证开发中", Toast.LENGTH_SHORT).show();
        });

        btnCertificates.setOnClickListener(v -> {
            Toast.makeText(getContext(), "证书认证开发中", Toast.LENGTH_SHORT).show();
        });

        btnPhone.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PhoneSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadCertificationFromServer() {
        String url = LocalHttpServerManager.getInstance().getCertificationGetUrl();
        Request request = new Request.Builder().url(url).get().build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                android.util.Log.e("Certification", "Failed to load: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    if (json != null && !json.isEmpty() && !json.equals("{}")) {
                        certificationData = JsonParser.parseString(json).getAsJsonObject();
                        getActivity().runOnUiThread(() -> updateUIFromCertification());
                    }
                }
            }
        });
    }

    private void updateUIFromCertification() {
        if (certificationData.has("identity_status") && "completed".equals(certificationData.get("identity_status").getAsString())) {
            setCertified(itemIdentity, btnIdentity, "实名认证");
            if (certificationData.has("identity_name")) {
                etName.setText(certificationData.get("identity_name").getAsString());
            }
            if (certificationData.has("identity_idcard")) {
                etIdCard.setText(certificationData.get("identity_idcard").getAsString());
            }
        } else {
            setUncertified(itemIdentity, btnIdentity, "开始实名认证");
        }

        if (certificationData.has("photo_status") && "completed".equals(certificationData.get("photo_status").getAsString())) {
            setCertified(itemPhoto, btnPhoto, "拍照认证");
        } else {
            setUncertified(itemPhoto, btnPhoto, "拍照认证");
        }

        if (certificationData.has("video_status") && "completed".equals(certificationData.get("video_status").getAsString())) {
            setCertified(itemPhoto, btnVideo, "视频认证");
        } else {
            setUncertified(itemPhoto, btnVideo, "视频认证");
        }

        if (certificationData.has("vehicle_status") && "completed".equals(certificationData.get("vehicle_status").getAsString())) {
            setCertified(itemVehicle, btnVehicle, "行驶证认证");
        } else {
            setUncertified(itemVehicle, btnVehicle, "行驶证认证");
        }

        if (certificationData.has("education_status") && "completed".equals(certificationData.get("education_status").getAsString())) {
            setCertified(itemEducation, btnEducation, "学历认证");
        } else {
            setUncertified(itemEducation, btnEducation, "学历认证");
        }

        if (certificationData.has("assets_status") && "completed".equals(certificationData.get("assets_status").getAsString())) {
            setCertified(itemAssets, btnAssets, "资产认证");
        } else {
            setUncertified(itemAssets, btnAssets, "资产认证");
        }

        if (certificationData.has("certificates_status") && "completed".equals(certificationData.get("certificates_status").getAsString())) {
            setCertified(itemCertificates, btnCertificates, "证书认证");
        } else {
            setUncertified(itemCertificates, btnCertificates, "证书认证");
        }

        if (certificationData.has("phone_status") && "completed".equals(certificationData.get("phone_status").getAsString())) {
            setCertified(itemPhone, btnPhone, "手机绑定");
        } else {
            setUncertified(itemPhone, btnPhone, "手机绑定");
        }
    }

    private void saveCertificationToServer() {
        String url = LocalHttpServerManager.getInstance().getCertificationSaveUrl();
        String json = certificationData.toString();
        
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                android.util.Log.e("Certification", "Failed to save: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws java.io.IOException {
                android.util.Log.d("Certification", "Saved: " + response.code());
            }
        });
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etIdCard = view.findViewById(R.id.et_idcard);
        
        itemPhone = view.findViewById(R.id.item_phone);
        itemIdentity = view.findViewById(R.id.item_identity);
        itemPhoto = view.findViewById(R.id.item_photo);
        itemVehicle = view.findViewById(R.id.item_vehicle);
        itemEducation = view.findViewById(R.id.item_education);
        itemAssets = view.findViewById(R.id.item_assets);
        itemCertificates = view.findViewById(R.id.item_certificates);

        btnPhone = view.findViewById(R.id.btn_phone_settings);
        btnIdentity = view.findViewById(R.id.btn_identity_verify);
        btnPhoto = view.findViewById(R.id.btn_photo_verify);
        btnVideo = view.findViewById(R.id.btn_video_verify);
        btnVehicle = view.findViewById(R.id.btn_vehicle_verify);
        btnEducation = view.findViewById(R.id.btn_education_verify);
        btnAssets = view.findViewById(R.id.btn_assets_verify);
        btnCertificates = view.findViewById(R.id.btn_certificates_verify);
        
        setUncertified(itemPhoto, btnPhoto, "拍照认证");
        setUncertified(itemPhoto, btnVideo, "视频认证");
        setUncertified(itemPhone, btnPhone, "手机绑定");
    }

    private void setCertified(LinearLayout item, TextView btn, String text) {
        btn.setBackgroundResource(R.drawable.bg_cert_button_certified);
        btn.setText("✓ " + text);
        btn.setTextColor(Color.WHITE);
        btn.setPaintFlags(btn.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void setUncertified(LinearLayout item, TextView btn, String text) {
        btn.setBackgroundResource(R.drawable.bg_cert_button);
        btn.setText(text);
        btn.setTextColor(Color.GRAY);
    }
}