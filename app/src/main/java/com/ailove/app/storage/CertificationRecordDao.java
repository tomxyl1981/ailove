package com.ailove.app.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CertificationRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CertificationRecordEntity entity);

    @Query("SELECT * FROM certification_records ORDER BY timestamp DESC")
    List<CertificationRecordEntity> getAll();

    @Query("SELECT * FROM certification_records WHERE category = :category ORDER BY timestamp DESC")
    List<CertificationRecordEntity> getByCategory(String category);

    @Query("SELECT * FROM certification_records WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    List<CertificationRecordEntity> getInRange(long start, long end);

    @Query("DELETE FROM certification_records")
    void deleteAll();
}
