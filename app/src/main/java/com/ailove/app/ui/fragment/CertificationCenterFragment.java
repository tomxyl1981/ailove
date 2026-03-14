package com.ailove.app.ui.fragment;

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
import com.google.gson.Gson;
import android.content.Intent;
import com.ailove.app.ui.activity.CameraCertificationActivity;
import com.ailove.app.ui.activity.PhoneSettingsActivity;
import java.util.List;

public class CertificationCenterFragment extends Fragment {

    private LinearLayout itemPhone, itemIdentity, itemPhoto, itemVehicle, itemEducation, itemAssets, itemCertificates;
    private TextView btnPhone, btnIdentity, btnPhoto, btnVideo, btnVehicle, btnEducation, btnAssets, btnCertificates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certification_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadCertificationStatus(view);

        // 身份证实名认证
        EditText etName = view.findViewById(R.id.et_name);
        EditText etIdCard = view.findViewById(R.id.et_idcard);
        btnIdentity.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String idCard = etIdCard.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idCard)) {
                Toast.makeText(getContext(), "请输入姓名与身份证号码", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiClient.getInstance().verifyIdentity(name, idCard, new ApiClient.Callback<VerifyResult>() {
                @Override
                public void onSuccess(VerifyResult result) {
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    CertificationRecord rec = new CertificationRecord();
                    rec.id = "identity_" + System.currentTimeMillis();
                    rec.category = "IDENTITY_CERT";
                    rec.dataJson = new Gson().toJson(result);
                    rec.status = "completed";
                    rec.timestamp = System.currentTimeMillis();
                    LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                    setCertified(itemIdentity, btnIdentity, "开始实名认证");
    
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 拍照认证
        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            startActivity(intent);
        });

        // 视频认证
        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            intent.putExtra("mode", "video");
            startActivity(intent);
        });

        // 车辆认证
        btnVehicle.setOnClickListener(v -> ApiClient.getInstance().verifyVehicle(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                CertificationRecord rec = new CertificationRecord();
                rec.id = "vehicle_" + System.currentTimeMillis();
                rec.category = "VEHICLE_CERT";
                rec.dataJson = new Gson().toJson(result);
                rec.status = "completed";
                rec.timestamp = System.currentTimeMillis();
                LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                setCertified(itemVehicle, btnVehicle, "行驶证认证");

            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        // 学历认证
        btnEducation.setOnClickListener(v -> ApiClient.getInstance().verifyEducation(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                CertificationRecord rec = new CertificationRecord();
                rec.id = "education_" + System.currentTimeMillis();
                rec.category = "EDUCATION_CERT";
                rec.dataJson = new Gson().toJson(result);
                rec.status = "completed";
                rec.timestamp = System.currentTimeMillis();
                LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                setCertified(itemEducation, btnEducation, "学信网对接");

            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        // 资产认证
        btnAssets.setOnClickListener(v -> ApiClient.getInstance().verifyAssets(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                CertificationRecord rec = new CertificationRecord();
                rec.id = "assets_" + System.currentTimeMillis();
                rec.category = "ASSETS_CERT";
                rec.dataJson = new Gson().toJson(result);
                rec.status = "completed";
                rec.timestamp = System.currentTimeMillis();
                LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                setCertified(itemAssets, btnAssets, "资产认证");

            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        // 证书认证
        btnCertificates.setOnClickListener(v -> ApiClient.getInstance().verifyCertificates(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                CertificationRecord rec = new CertificationRecord();
                rec.id = "certificates_" + System.currentTimeMillis();
                rec.category = "CERTIFICATES_CERT";
                rec.dataJson = new Gson().toJson(result);
                rec.status = "completed";
                rec.timestamp = System.currentTimeMillis();
                LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                setCertified(itemCertificates, btnCertificates, "证书验证");

            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        // 手机设置
        btnPhone.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PhoneSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void initViews(View view) {
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
    }

    private void setCertified(LinearLayout item, TextView btn, String text) {
        btn.setBackgroundResource(R.drawable.bg_cert_button_certified);
        btn.setText("✓ " + text);
        btn.setTextColor(getResources().getColor(R.color.white, null));
        btn.setPaintFlags(btn.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void loadCertificationStatus(View view) {
        List<CertificationRecord> records = LocalJsonPersistence.loadCertificationRecords(getContext());
        for (CertificationRecord r : records) {
            if (r.status.equals("completed")) {
                if (r.category.equals("IDENTITY_CERT")) {
                    setCertified(itemIdentity, btnIdentity, "开始实名认证");
                } else if (r.category.equals("VEHICLE_CERT")) {
                    setCertified(itemVehicle, btnVehicle, "行驶证认证");
                } else if (r.category.equals("EDUCATION_CERT")) {
                    setCertified(itemEducation, btnEducation, "学信网对接");
                } else if (r.category.equals("ASSETS_CERT")) {
                    setCertified(itemAssets, btnAssets, "资产认证");
                } else if (r.category.equals("CERTIFICATES_CERT")) {
                    setCertified(itemCertificates, btnCertificates, "证书验证");
                }
            }
        }
    }
}
