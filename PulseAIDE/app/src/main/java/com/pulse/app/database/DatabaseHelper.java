package com.pulse.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.pulse.app.models.User;
import com.pulse.app.models.Message;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pulse.db";
    private static final int DB_VERSION = 1;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, displayName TEXT, bio TEXT, isOnline INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, senderId INTEGER, receiverId INTEGER, content TEXT, timestamp INTEGER, isRead INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    // USER METHODS
    public long registerUser(String username, String password, String displayName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("displayName", displayName);
        try { return db.insertOrThrow("users", null, cv); }
        catch (Exception e) { return -1; }
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        if (c.moveToFirst()) {
            User u = cursorToUser(c);
            c.close();
            setOnline(u.id, true);
            return u;
        }
        c.close();
        return null;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE id=?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) { User u = cursorToUser(c); c.close(); return u; }
        c.close(); return null;
    }

    public List<User> searchUsers(String query, int excludeId) {
        SQLiteDatabase db = getReadableDatabase();
        List<User> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE (username LIKE ? OR displayName LIKE ?) AND id != ?",
            new String[]{"%" + query + "%", "%" + query + "%", String.valueOf(excludeId)});
        while (c.moveToNext()) list.add(cursorToUser(c));
        c.close();
        return list;
    }

    public void setOnline(int userId, boolean online) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("isOnline", online ? 1 : 0);
        db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
    }

    public void updateProfile(int userId, String displayName, String bio) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("displayName", displayName);
        cv.put("bio", bio);
        db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
    }

    private User cursorToUser(Cursor c) {
        User u = new User();
        u.id = c.getInt(c.getColumnIndexOrThrow("id"));
        u.username = c.getString(c.getColumnIndexOrThrow("username"));
        u.password = c.getString(c.getColumnIndexOrThrow("password"));
        u.displayName = c.getString(c.getColumnIndexOrThrow("displayName"));
        u.bio = c.getString(c.getColumnIndexOrThrow("bio"));
        u.isOnline = c.getInt(c.getColumnIndexOrThrow("isOnline")) == 1;
        return u;
    }

    // MESSAGE METHODS
    public long sendMessage(int senderId, int receiverId, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("senderId", senderId);
        cv.put("receiverId", receiverId);
        cv.put("content", content);
        cv.put("timestamp", System.currentTimeMillis());
        cv.put("isRead", 0);
        return db.insert("messages", null, cv);
    }

    public List<Message> getMessages(int userId1, int userId2) {
        SQLiteDatabase db = getReadableDatabase();
        List<Message> list = new ArrayList<>();
        Cursor c = db.rawQuery(
            "SELECT * FROM messages WHERE (senderId=? AND receiverId=?) OR (senderId=? AND receiverId=?) ORDER BY timestamp ASC",
            new String[]{String.valueOf(userId1), String.valueOf(userId2), String.valueOf(userId2), String.valueOf(userId1)});
        while (c.moveToNext()) list.add(cursorToMessage(c));
        c.close();
        return list;
    }

    public List<Integer> getConversationPartners(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Integer> list = new ArrayList<>();
        Cursor c = db.rawQuery(
            "SELECT DISTINCT CASE WHEN senderId=? THEN receiverId ELSE senderId END as partner FROM messages WHERE senderId=? OR receiverId=? ORDER BY timestamp DESC",
            new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf(userId)});
        while (c.moveToNext()) list.add(c.getInt(0));
        c.close();
        return list;
    }

    public Message getLastMessage(int userId1, int userId2) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
            "SELECT * FROM messages WHERE (senderId=? AND receiverId=?) OR (senderId=? AND receiverId=?) ORDER BY timestamp DESC LIMIT 1",
            new String[]{String.valueOf(userId1), String.valueOf(userId2), String.valueOf(userId2), String.valueOf(userId1)});
        if (c.moveToFirst()) { Message m = cursorToMessage(c); c.close(); return m; }
        c.close(); return null;
    }

    public int getUnreadCount(int senderId, int receiverId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM messages WHERE senderId=? AND receiverId=? AND isRead=0",
            new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public void markAsRead(int senderId, int receiverId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("isRead", 1);
        db.update("messages", cv, "senderId=? AND receiverId=?", new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
    }

    private Message cursorToMessage(Cursor c) {
        Message m = new Message();
        m.id = c.getInt(c.getColumnIndexOrThrow("id"));
        m.senderId = c.getInt(c.getColumnIndexOrThrow("senderId"));
        m.receiverId = c.getInt(c.getColumnIndexOrThrow("receiverId"));
        m.content = c.getString(c.getColumnIndexOrThrow("content"));
        m.timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        m.isRead = c.getInt(c.getColumnIndexOrThrow("isRead")) == 1;
        return m;
    }
}
