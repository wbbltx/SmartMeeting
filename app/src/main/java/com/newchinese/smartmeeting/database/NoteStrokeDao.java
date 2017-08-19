package com.newchinese.smartmeeting.database;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.newchinese.smartmeeting.model.bean.NoteStroke;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTE_STROKE".
*/
public class NoteStrokeDao extends AbstractDao<NoteStroke, Long> {

    public static final String TABLENAME = "NOTE_STROKE";

    /**
     * Properties of entity NoteStroke.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PageId = new Property(1, long.class, "pageId", false, "PAGE_ID");
        public final static Property StrokeColor = new Property(2, int.class, "strokeColor", false, "STROKE_COLOR");
        public final static Property StrokeWidth = new Property(3, float.class, "strokeWidth", false, "STROKE_WIDTH");
    }

    private DaoSession daoSession;

    private Query<NoteStroke> notePage_StrokeListQuery;

    public NoteStrokeDao(DaoConfig config) {
        super(config);
    }
    
    public NoteStrokeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTE_STROKE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PAGE_ID\" INTEGER NOT NULL ," + // 1: pageId
                "\"STROKE_COLOR\" INTEGER NOT NULL ," + // 2: strokeColor
                "\"STROKE_WIDTH\" REAL NOT NULL );"); // 3: strokeWidth
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE_STROKE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NoteStroke entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPageId());
        stmt.bindLong(3, entity.getStrokeColor());
        stmt.bindDouble(4, entity.getStrokeWidth());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NoteStroke entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPageId());
        stmt.bindLong(3, entity.getStrokeColor());
        stmt.bindDouble(4, entity.getStrokeWidth());
    }

    @Override
    protected final void attachEntity(NoteStroke entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NoteStroke readEntity(Cursor cursor, int offset) {
        NoteStroke entity = new NoteStroke( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // pageId
            cursor.getInt(offset + 2), // strokeColor
            cursor.getFloat(offset + 3) // strokeWidth
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NoteStroke entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPageId(cursor.getLong(offset + 1));
        entity.setStrokeColor(cursor.getInt(offset + 2));
        entity.setStrokeWidth(cursor.getFloat(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NoteStroke entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NoteStroke entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NoteStroke entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "strokeList" to-many relationship of NotePage. */
    public List<NoteStroke> _queryNotePage_StrokeList(long pageId) {
        synchronized (this) {
            if (notePage_StrokeListQuery == null) {
                QueryBuilder<NoteStroke> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PageId.eq(null));
                notePage_StrokeListQuery = queryBuilder.build();
            }
        }
        Query<NoteStroke> query = notePage_StrokeListQuery.forCurrentThread();
        query.setParameter(0, pageId);
        return query.list();
    }

}