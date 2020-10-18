package com.example.bookmanagment.Driver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bookmanagment.Modal.User;
import com.example.bookmanagment.SQL.BookSqlHelper;
import com.example.bookmanagment.Schema.UserSchema;

import java.util.ArrayList;

import static com.example.bookmanagment.BookViewerActivity.TAG;

public class UserDataBaseDriver
{
    BookSqlHelper userSqlHelper;
    Context context;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<User> userList;
    int id = 1;

    public UserDataBaseDriver(Context context)
    {
        this.context = context;
        userSqlHelper = new BookSqlHelper(context);
        sqLiteDatabase = userSqlHelper.getWritableDatabase();
    }

    public ArrayList<User> getUserList()
    {
        userList = new ArrayList<>();

        String[] columns = {UserSchema._userId, UserSchema._userName, UserSchema._userPassword, UserSchema._userQuestion, UserSchema._questionAnswer, UserSchema._roomId};
        Cursor cursor = sqLiteDatabase.query(UserSchema._tableName, columns, null, null, null, null, null);

        return getAllUserList(cursor);
    }

    private ArrayList<User> getAllUserList(Cursor cursor)
    {
        if(cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(UserSchema._userId));
                String username = cursor.getString(cursor.getColumnIndex(UserSchema._userName));
                String user_pwd = cursor.getString(cursor.getColumnIndex(UserSchema._userPassword));
                String question = cursor.getString(cursor.getColumnIndex(UserSchema._userQuestion));
                String answer = cursor.getString(cursor.getColumnIndex(UserSchema._questionAnswer));
                int rid = cursor.getInt(cursor.getColumnIndex(UserSchema._roomId));
                User user = new User(id, username, user_pwd, question, answer, rid );
                userList.add(user);

            }while (cursor.moveToNext());
        }
        return userList;
    }

    public void insertNewUser(User user)
    {
        ContentValues contentValues = insertContentValues(user);
        long id = sqLiteDatabase.insert(UserSchema._tableName, null, contentValues);
        Log.d(TAG, "insertNewUser: new User of id " + id + " inserted into db");
    }

    private ContentValues insertContentValues(User user)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserSchema._userId, id++);
        contentValues.put(UserSchema._userName, user.getUserName());
        contentValues.put(UserSchema._userPassword, user.getUserPassword());
        contentValues.put(UserSchema._userQuestion, user.getQuestion());
        contentValues.put(UserSchema._questionAnswer, user.getAnswer());
        contentValues.put(UserSchema._roomId, user.getRoomId());
        return contentValues;
    }

    public String getSinlgeEntry(int roomId)
    {
        Cursor cursor=sqLiteDatabase.query(UserSchema._tableName, null, " room_id=?", new String[]{String.valueOf(roomId)}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password= cursor.getString(cursor.getColumnIndex(UserSchema._userPassword));
        cursor.close();
        return password;
    }

    public ArrayList<User> getUserFromSpecifRoom(int roomId)
    {
        String selection = UserSchema._roomId + " = ?" ;
        String[] selectionArgs = {String.valueOf(roomId)};
        String[] columns = {UserSchema._userId, UserSchema._userName, UserSchema._userPassword, UserSchema._userQuestion, UserSchema._questionAnswer, UserSchema._roomId};
        Cursor cursor = sqLiteDatabase.query(UserSchema._tableName, columns, selection, selectionArgs, null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(UserSchema._userId));
                String username = cursor.getString(cursor.getColumnIndex(UserSchema._userName));
                String user_pwd = cursor.getString(cursor.getColumnIndex(UserSchema._userPassword));
                String question = cursor.getString(cursor.getColumnIndex(UserSchema._userQuestion));
                String answer = cursor.getString(cursor.getColumnIndex(UserSchema._questionAnswer));
                int rid = cursor.getInt(cursor.getColumnIndex(UserSchema._roomId));
                User user = new User(id, username, user_pwd, question, answer, rid);
                userList.add(user);

            } while (cursor.moveToNext());
        }
        return userList;
    }
}
