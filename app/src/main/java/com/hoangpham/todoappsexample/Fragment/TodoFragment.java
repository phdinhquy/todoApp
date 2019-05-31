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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.Adapter.TodoAdapter;
import com.hoangpham.todoappsexample.R;
import com.hoangpham.todoappsexample.models.TodoDTO;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment{
    private static final String TAG = "TodoFragment";
    Activity mContext = this.getActivity();
    private String DB_NAME_TODO;

    RecyclerView mRecyclerView;
    TodoAdapter mAdapter;
    List<TodoDTO> mData;

    DatabaseReference myRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mRecyclerView = v.findViewById(R.id.todoList);
        myRef = FirebaseDatabase.getInstance().getReference();
        DB_NAME_TODO = getResources().getString(R.string.db_name_todo);
        initRecyclerView();
        init();
        return v;
    }

    private void initRecyclerView() {
        mData = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TodoAdapter(getActivity(), mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void init() {
        if (isAdded()) {
            Log.d(TAG, "init: retrieve List to do ");
            Query query = myRef.child(DB_NAME_TODO);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: ");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            TodoDTO todoDTO = ds.getValue(TodoDTO.class);
                            mData.add(todoDTO);

                        }
                        if (mData.size() != 0){
                            mAdapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "onDataChange: lists" + mData.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private List<TodoDTO> getList() {
        List<TodoDTO> todoDTOS = new ArrayList<>();
        todoDTOS.add(new TodoDTO(true, "To do 1", false));
        todoDTOS.add(new TodoDTO(false, "To do 2", false));
        todoDTOS.add(new TodoDTO(true, "To do 3", true));
        todoDTOS.add(new TodoDTO(false, "To do 4", false));
        todoDTOS.add(new TodoDTO(true, "To do 5", true));
        return todoDTOS;
    }

    public void update(TodoDTO todoDTO){
        Log.d(TAG, "update: updated");
        mData.add(todoDTO);
        mAdapter.notifyDataSetChanged();
    }

}
