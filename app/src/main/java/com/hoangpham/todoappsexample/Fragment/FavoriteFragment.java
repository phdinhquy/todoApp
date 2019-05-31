package com.hoangpham.todoappsexample.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.Adapter.FavoriteAdap;
import com.hoangpham.todoappsexample.Adapter.TodoAdapter;
import com.hoangpham.todoappsexample.R;
import com.hoangpham.todoappsexample.models.TodoDTO;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";
    Activity mContext = this.getActivity();
    private String dbNameTodo;


    DatabaseReference myRef;
    RecyclerView mRecyclerView;
    FavoriteAdap mAdapter;
    List<TodoDTO> mData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
        dbNameTodo = getResources().getString(R.string.db_name_todo);
        mRecyclerView = v.findViewById(R.id.favoriteList);
        initRecyclerView();
        getFavoriteList();

        return v;
    }

    private void initRecyclerView() {
        mData = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new FavoriteAdap(getActivity(), mData);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void getFavoriteList() {
        myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child(dbNameTodo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: data" + ds);
                        TodoDTO todoDTO = ds.getValue(TodoDTO.class);
                        if (todoDTO.isFavorite()) {
                            mData.add(todoDTO);
                        }

                    }
                    Log.d(TAG, "onDataChange: mData" + mData.size());
                    if (mData.size() != 0) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Empty list", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
