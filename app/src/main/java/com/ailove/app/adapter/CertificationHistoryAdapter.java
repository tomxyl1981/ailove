package com.ailove.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.model.CertificationRecord;
import java.util.List;

public class CertificationHistoryAdapter extends RecyclerView.Adapter<CertificationHistoryAdapter.ViewHolder> {
    private List<CertificationRecord> records;

    public CertificationHistoryAdapter(List<CertificationRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificationRecord rec = records.get(position);
        holder.tvId.setText(rec.id);
        holder.tvCategory.setText(rec.category);
        holder.tvStatus.setText(rec.status);
        holder.tvTime.setText(String.valueOf(rec.timestamp));
        String snippet = rec.dataJson != null ? (rec.dataJson.length() > 60 ? rec.dataJson.substring(0, 60) + "..." : rec.dataJson) : "";
        holder.tvPreview.setText(snippet);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvCategory, tvStatus, tvTime, tvPreview;
        ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPreview = itemView.findViewById(R.id.tv_preview);
        }
    }
}
