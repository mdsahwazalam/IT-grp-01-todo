package com.sahwaz.mytodo;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rv;
    Adapter mAdapter;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.content_task);


        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent tmpIntent = getIntent();
        toolbar.setTitle(tmpIntent.getStringExtra("UserName"));
        setSupportActionBar(toolbar);

        coordinatorLayout=findViewById(R.id.coordLayout);
//        //recylcer view
//        rv=findViewById(R.id.recyclerTodo);
//        Realm r = Realm.getDefaultInstance();
//        RealmQuery<Todo> query = r.where(Todo.class).equalTo("userName", getIntent().getStringExtra("UserName"));
//        RealmResults<Todo> result = query.findAll();
//        //Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
//        Adapter m = new Adapter(result,this);
//        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setAdapter(m);

        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton remove = findViewById(R.id.remove);


        //"+" button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                String name=i.getStringExtra("UserName");
                Intent intent=new Intent(TaskActivity.this, TodoActivity.class);
                intent.putExtra("UserName",name);
                startActivity(intent);
            }
        });

        //"X" button
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Realm r = Realm.getDefaultInstance();
//                r.beginTransaction();
//                RealmResults<Todo> query = r.where(Todo.class).equalTo("userName", getIntent().getStringExtra("UserName")).findAll();
//                query.deleteAllFromRealm();
//                r.commitTransaction();
//                onStart();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

//        Intent tmpIntent = getIntent();
//        TextView namHeadText=findViewById(R.id.nav_header_txt);
//        namHeadText.setText(tmpIntent.getStringExtra("UserName"));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //recylcer view
        rv=findViewById(R.id.recyclerTodo);
        Realm r = Realm.getDefaultInstance();
        RealmQuery<Todo> query = r.where(Todo.class).equalTo("userName", getIntent().getStringExtra("UserName")).sort("todoItem");
        RealmResults<Todo> result = query.findAll();
        //Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        mAdapter = new Adapter(result,this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Todo item = mAdapter.getData().get(position);
                String tcolor = item.getColor();
                String tUname = item.getUserName();
                String tdet = item.getDetails();
                String tdate = item.getDuedate();
                String ttodo = item.getTodoItem();

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mAdapter.restoreItem(tcolor,tUname,tdet,tdate,ttodo, position);
                        //rv.scrollToPosition(position);
                        onStart();
                        float x=2;
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rv);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Realm r = Realm.getDefaultInstance();
            r.beginTransaction();
            RealmResults<Todo> query = r.where(Todo.class).equalTo("userName", getIntent().getStringExtra("UserName")).findAll();
            query.deleteAllFromRealm();
            r.commitTransaction();
            Toast.makeText(TaskActivity.this,"Cleared all todo",Toast.LENGTH_SHORT).show();
            onStart();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
