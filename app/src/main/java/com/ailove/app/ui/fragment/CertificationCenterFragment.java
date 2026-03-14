package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.ailove.app.ui.activity.CertificationQuestionnaireActivity;
import java.util.List;
import com.ailove.app.ui.activity.CertificationHistoryActivity;
import com.ailove.app.model.FaceAnalyzeResult;

/** Certification Center UI - 展示各种认证入口的 Fragment */
public class CertificationCenterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用简易布局，包含多节认证入口
        return inflater.inflate(R.layout.fragment_certification_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始更新认证概览
        updateOverview(view);

        // 身份证实名认证
        EditText etName = view.findViewById(R.id.et_name);
        EditText etIdCard = view.findViewById(R.id.et_idcard);
        Button btnIdentity = view.findViewById(R.id.btn_identity_verify);
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
                    // Persist identity verification result to local JSON
                    CertificationRecord rec = new CertificationRecord();
                    rec.id = "identity_" + System.currentTimeMillis();
                    rec.category = "IDENTITY_CERT";
                    rec.dataJson = new Gson().toJson(result);
                    rec.status = "completed";
                    rec.timestamp = System.currentTimeMillis();
                    LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 真实颜原相机照片视频认证（示意：调用 mock API）
        Button btnPhoto = view.findViewById(R.id.btn_photo_verify);
        Button btnVideo = view.findViewById(R.id.btn_video_verify);
        btnPhoto.setOnClickListener(v -> {
            // 跳转到拍照/摄像认证的演示页（待接真实相机实现）
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            startActivity(intent);
        });
        btnVideo.setOnClickListener(v -> {
            // 跳转视频认证演示页，后续接入真实视频拍摄能力
            Intent intent = new Intent(getContext(), CameraCertificationActivity.class);
            intent.putExtra("mode", "video");
            startActivity(intent);
        });

        // 车辆/房产/学历/健康等认证入口示意
        Button btnVehicle = view.findViewById(R.id.btn_vehicle_verify);
        Button btnEducation = view.findViewById(R.id.btn_education_verify);
        Button btnHealth = view.findViewById(R.id.btn_health_verify);
        Button btnAssets = view.findViewById(R.id.btn_assets_verify);
        Button btnCertificates = view.findViewById(R.id.btn_certificates_verify);
        // 查看认证历史
        Button btnHistory = view.findViewById(R.id.btn_history_verify);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.ailove.app.ui.activity.CertificationHistoryActivity.class);
            startActivity(intent);
        });

        btnVehicle.setOnClickListener(v -> ApiClient.getInstance().verifyVehicle(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        btnEducation.setOnClickListener(v -> ApiClient.getInstance().verifyEducation(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        btnHealth.setOnClickListener(v -> ApiClient.getInstance().verifyHealth(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        btnAssets.setOnClickListener(v -> ApiClient.getInstance().verifyAssets(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));

        btnCertificates.setOnClickListener(v -> ApiClient.getInstance().verifyCertificates(new ApiClient.Callback<VerifyResult>() {
            @Override
            public void onSuccess(VerifyResult result) {
                Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                updateOverview(view);
            }
            @Override
            public void onError(String error) { Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show(); }
        }));
        
        // 调整入口：调查问卷按钮，未来项使用对话式加载
        Button btnQuestionnaire = view.findViewById(R.id.btn_questionnaire);
        btnQuestionnaire.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CertificationQuestionnaireActivity.class);
            startActivity(intent);
        });
    }

    private void updateOverview(View root) {
        // 简易概览展示：统计已记录的认证类别数量
        List<CertificationRecord> records = com.ailove.app.storage.LocalJsonPersistence.loadCertificationRecords(getContext());
        int total = records.size();
        int identity = 0, photo = 0, vehicle = 0, education = 0, health = 0, assets = 0, certificates = 0;
        for (CertificationRecord r : records) {
            switch (r.category) {
                case "IDENTITY_CERT": identity++; break;
                case "PHOTO_CERT": photo++; break;
                case "VEHICLE_CERT": vehicle++; break;
                case "EDUCATION_CERT": education++; break;
                case "HEALTH_CERT": health++; break;
                case "ASSETS_CERT": assets++; break;
                case "CERTIFICATES_CERT": certificates++; break;
            }
        }
        String overview = "总认证: " + total
                + "  身份证: " + identity
                + "  照片/视频: " + photo
                + "  车辆: " + vehicle
                + "  学历: " + education
                + "  健康: " + health
                + "  资产: " + assets
                + "  证书: " + certificates;
        TextView tv = root.findViewById(R.id.tv_overview);
        tv.setText(overview);
    }
}
