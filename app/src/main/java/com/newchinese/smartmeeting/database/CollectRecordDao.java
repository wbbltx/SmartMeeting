package com.newchinese.smartmeeting.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.newchinese.smartmeeting.model.bean.CollectRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COLLECT_RECORD".
*/
public class CollectRecordDao extends AbstractDao<CollectRecord, Long> {

    public static final String TABLENAME = "COLLECT_RECORD";

    /**
     * Properties of entity CollectRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClassifyName = new Property(1, String.class, "classifyName", false, "CLASSIFY_NAME");
        public final static Property CollectRecordName = new Property(2, String.class, "collectRecordName", false, "COLLECT_RECORD_NAME");
        public final static Property CollectDate = new Property(3, long.class, "collectDate", false, "COLLECT_DATE");
    }

    private DaoSession daoSession;


    public CollectRecordDao(DaoConfig config) {
        super(config);
    }
    
    public CollectRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COLLECT_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CLASSIFY_NAME\" TEXT," + // 1: classifyName
                "\"COLLECT_RECORD_NAME\" TEXT," + // 2: collectRecordName
                "\"COLLECT_DATE\" INTEGER NOT NULL );"); // 3: collectDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COLLECT_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CollectRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String classifyName = entity.getClassifyName();
        if (classifyName != null) {
            stmt.bindString(2, classifyName);
        }
 
        String collectRecordName = entity.getCollectRecordName();
        if (collectRecordName != null) {
            stmt.bindString(3, collectRecordName);
        }
        stmt.bindLong(4, entity.getCollectDate());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CollectRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String classifyName = entity.getClassifyName();
        if (classifyName != null) {
            stmt.bindString(2, classifyName);
        }
 
        String collectRecordName = entity.getCollectRecordName();
        if (collectRecordName != null) {
            stmt.bindString(3, collectRecordName);
        }
        stmt.bindLong(4, entity.getCollectDate());
    }

    @Override
    protected final void attachEntity(CollectRecord entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CollectRecord readEntity(Cursor cursor, int offset) {
        CollectRecord entity = new CollectRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // classifyName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // collectRecordName
            cursor.getLong(offset + 3) // collectDate
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CollectRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClassifyName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCollectRecordName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCollectDate(cursor.getLong(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CollectRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CollectRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CollectRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
