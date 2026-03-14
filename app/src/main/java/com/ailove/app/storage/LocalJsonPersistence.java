package com.ailove.app.storage;

import android.content.Context;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.model.QuestionnaireResponse;
import com.ailove.app.storage.CertificationRecordEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalJsonPersistence {
    private static final String FILE_NAME = "certifications.json";
    private static final Gson gson = new Gson();

    public static void saveCertificationRecord(Context context, CertificationRecord record) {
        // Persist to Room database
        AppDatabase db = AppDatabase.getInstance(context);
        CertificationRecordEntity entity = new CertificationRecordEntity(record.id, record.category, record.dataJson, record.status, record.timestamp);
        db.certificationRecordDao().insert(entity);
    }

    public static List<CertificationRecord> loadCertificationRecords(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        List<CertificationRecordEntity> entities = db.certificationRecordDao().getAll();
        List<CertificationRecord> records = new ArrayList<>();
        for (CertificationRecordEntity e : entities) {
            CertificationRecord r = new CertificationRecord();
            r.id = e.id; r.category = e.category; r.dataJson = e.dataJson; r.status = e.status; r.timestamp = e.timestamp;
            records.add(r);
        }
        return records;
    }

    public static void saveQuestionnaireResponse(Context context, QuestionnaireResponse q) {
        LocalQuestionStorage.saveQuestionnaire(context, q);
    }

    public static List<QuestionnaireResponse> loadAllQuestionnaires(Context context) {
        return LocalQuestionStorage.loadAll(context);
    }
}
