package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class ConstellationFragment extends Fragment {
    private Spinner spSunSign;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    private String[] constellations = {
        "白羊座", "金牛座", "双子座", "巨蟹座", 
        "狮子座", "处女座", "天秤座", "天蝎座", 
        "射手座", "摩羯座", "水瓶座", "双鱼座"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_constellation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spSunSign = view.findViewById(R.id.sp_sun_sign);
        btnCalc = view.findViewById(R.id.btn_constellation_calc);
        cardResult = view.findViewById(R.id.card_result);
        tvScore = view.findViewById(R.id.tv_constellation_score);
        tvDetails = view.findViewById(R.id.tv_constellation_details);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        spSunSign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnCalc.setOnClickListener(v -> {
            int position = spSunSign.getSelectedItemPosition();
            String selectedConstellation = constellations[position];

            ApiClient.getInstance().calculateConstellation(selectedConstellation, new ApiClient.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    showResult(selectedConstellation);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showResult(String constellation) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("匹配度: 78%");
        tvDetails.setText("星座分析：" + constellation + "\n\n" +
            "你们在星座匹配上具有较好的兼容性。\n" +
            "建议：尊重彼此的个性特点，相互包容");
    }
}
