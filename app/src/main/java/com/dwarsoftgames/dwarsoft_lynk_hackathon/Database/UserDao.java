package com.dwarsoftgames.dwarsoft_lynk_hackathon.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_table")
    List<user_table> getAll();

    @Query("SELECT * FROM user_table ORDER BY uid DESC LIMIT 1")
    user_table getUserData();

    @Query("SELECT volunteer_id FROM user_table WHERE user_id = 1")
    int getVolunteerID();

    @Query("SELECT phoneNo FROM user_table WHERE user_id = 1")
    String getPhoneNumber();

    @Query("UPDATE user_table SET volunteer_id = :volunteerID WHERE user_id = 1")
    void updateVolunteerID(int volunteerID);

    @Query("UPDATE user_table SET phoneNo = :phoneNo WHERE user_id = 1")
    void updatePhoneNo(String phoneNo);

    @Insert
    void insertAll(user_table... user_tables);
}