package com.sahwaz.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainActivity extends AppCompatActivity {

    public static TextInputEditText mUserName;
    private Button mLoginBtn;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName=(TextInputEditText)findViewById(R.id.main_userName);
        mLoginBtn=(Button)findViewById(R.id.main_btn);


        try{
            realm = Realm.getDefaultInstance();

        }catch (RealmMigrationNeededException e){

            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RealmResults<UserInfo> results = realm.where(UserInfo.class).findAllAsync();
                if(mUserName.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Am I a joke to you?", Toast.LENGTH_LONG).show();
                }
                else {
                    RealmQuery<UserInfo> query = realm.where(UserInfo.class).equalTo("userName", mUserName.getText().toString());
                    RealmResults<UserInfo> result = query.findAllAsync();
                    result.load();

                    if (result.size() == 0) {
                        realm.beginTransaction();
                        UserInfo obj = realm.createObject(UserInfo.class);
                        obj.setUserName(mUserName.getText().toString());
                        obj.setOthers("lol");
                        realm.commitTransaction();

                        Toast.makeText(MainActivity.this, "New User Added", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Welcome again", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent=new Intent(MainActivity.this, TaskActivity.class);
                    intent.putExtra("UserName",mUserName.getText().toString());
                    startActivity(intent);
                    finish();

                }
            }
        });
    }
}
