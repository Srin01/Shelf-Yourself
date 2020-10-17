package com.example.bookmanagment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.bookmanagment.Adapter.RoomAdapter;
import com.example.bookmanagment.Driver.RoomDatabaseDriver;
import com.example.bookmanagment.Expert.RoomExpert;
import com.example.bookmanagment.Modal.Room;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RoomAdapter.OnRoomListerner
{
    public static final String TAG = "myTag";
    public static final String ROOM_NAME = "roomName";
    public static final String ROOM_ID = "roomID";
    public static final String SHELF_NUMBER = "shelfNumber";
    RoomDatabaseDriver roomDatabaseDriver;
    RoomAdapter roomAdapter;
    RoomExpert roomExpert;
    RecyclerView roomsViewRecycler;
    Toolbar toolbar;
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomDatabaseDriver = new RoomDatabaseDriver(this);
        roomExpert = new RoomExpert(roomDatabaseDriver);
        roomAdapter = new RoomAdapter(this, roomExpert,this);
        bindViews();
        setUpNavigationDrawerIcon();
        setUpToolbar();
        setUpListeners();
    }

    private void bindViews()
    {
        roomsViewRecycler = findViewById(R.id.shelves_recyclerView);
        roomsViewRecycler.setLayoutManager(new LinearLayoutManager(this));
        roomsViewRecycler.setAdapter(roomAdapter);
        printDetails();
    }
    private void setUpListeners()
    {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(searchIntent);
                        return true;
                }
                return false;
            }
        });
    }

    private void setUpNavigationDrawerIcon()
    {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.closed);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }



    public void printDetails()
    {
        ArrayList<Room> Rooms = roomDatabaseDriver.getAllRoomList();
        
        System.out.println("Your database has " + Rooms.size() + " rooms ");
        for (int i = 0; i < Rooms.size(); i++)
        {
            System.out.println(Rooms.get(i).getId() + " " +Rooms.get(i).getShelfNumber() + " " + Rooms.get(i).getRoomName());
        }
    }

    public void OnclickAddExtraRoom(View view)
    {
        Intent addRoomIntent = new Intent(this,AddExtraRoom.class);
        startActivityForResult(addRoomIntent, 1);
        roomAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                String roomName = data.getStringExtra("roomName");
                int numberOfShelves = data.getIntExtra("numberOfShelves", 1);
                Room room = new Room();
                room.setShelfNumber(numberOfShelves);
                room.setRoomName(roomName);
                roomExpert.addNewRoom(room);
                roomAdapter.notifyDataSetChanged();
            }
        }
        printDetails();
    }

    @Override
    public void onRoomClick(int position)
    {
        Log.d(TAG, "onRoomClick: " + roomExpert.getRoomName(position));
        Intent intent = new Intent(this, ShelfBookActivity.class);
        intent.putExtra(ROOM_NAME, roomExpert.getRoomName(position));
        intent.putExtra(ROOM_ID, roomExpert.getRoomID(position));
        intent.putExtra(SHELF_NUMBER, roomExpert.getShelfNumber(position));
        startActivity(intent);
    }
}