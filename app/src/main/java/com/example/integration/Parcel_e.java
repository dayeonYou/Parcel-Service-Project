package com.example.integration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Parcel_e extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> arrayList;
    static FirebaseDatabase database;
    static DatabaseReference databaseReference;
    SharedPreferences sp;
    static int saveInt;
    @Override
    protected void onCreate(@Nullable Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.parcel_end);
        setTitle("회수된 택배");

        sp = getSharedPreferences("sp2", MODE_PRIVATE);
        saveInt = sp.getInt("save2", 0);

        recyclerView = findViewById(R.id.rvE);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("E"); // DB 테이블 연결
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    arrayList.add(user);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        adapter = new CustomAdapter(arrayList, this,"end");
        recyclerView.setAdapter(adapter);

        saveInt++;
        save(saveInt);

    }
    static void writeNewUserE(String tv_id) {
        String index = "nUser_0" + saveInt;
        database.getReference("E").child(index).child("id").setValue(tv_id);
    }
    public void save(int s){
        sp = getSharedPreferences("sp2",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putInt("save2",s);
        editor.commit();
    }
    static void deleteDataE(String tv_id) {

        Query query = databaseReference.orderByChild("id").equalTo(tv_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot dataSnapshot1 : datasnapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}