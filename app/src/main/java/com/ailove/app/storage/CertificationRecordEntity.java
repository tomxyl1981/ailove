package com.ailove.app.storage;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "certification_records")
public class CertificationRecordEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String category;
    public String dataJson;
    public String status;
    public long timestamp;

    public CertificationRecordEntity() {}

    @Ignore
    public CertificationRecordEntity(String id, String category, String dataJson, String status, long timestamp) {
        this.id = id;
        this.category = category;
        this.dataJson = dataJson;
        this.status = status;
        this.timestamp = timestamp;
    }
}
