package com.bytedance.todolist.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.bytedance.todolist.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

public class TodoListActivity extends AppCompatActivity {

    private TodoListAdapter mAdapter;
    private FloatingActionButton mFab;
    private RecyclerView recyclerView;
    private static int n=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list_activity_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TodoListAdapter();
        recyclerView.setAdapter(mAdapter);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                new Thread() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(TodoListActivity.this,AddDataActivity.class);
                        startActivity(intent);
                        /*这里有一个问题：AddDataActivity中添加数据到数据库之前，下面的代码就开始执行了，导致
                        mAdapter.setData(list1)发生在数据添加到数据库之前，所以此线程需要等待一段时间，即等数据添加到数据库之后才可运行
                        因为对多线程不了解，我能想到的办法也只有让此线程先sleep一会，好等待AddDataActivity执行完毕*/
                        try {
                            sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        TodoListDao dao = TodoListDatabase.inst(getApplicationContext()).todoListDao();
                        final List<TodoListEntity> list1 = dao.loadAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(list1);
                            }
                        });
                    }
                }.start();
            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        TodoListDao dao = TodoListDatabase.inst(getApplicationContext()).todoListDao();
                        dao.deleteAll();
                        for (int i = 0; i < n; ++i) {
                            dao.addTodo(new TodoListEntity("This is " + i + " item", new Date(System.currentTimeMillis())));
                        }
                        n++;
                        Snackbar.make(mFab, R.string.hint_insert_complete, Snackbar.LENGTH_SHORT).show();
                        final List<TodoListEntity> list2 = dao.loadAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(list2);
                            }
                        });
                    }
                }.start();
                return true;
            }
        });
        loadFromDatabase();
        mAdapter.notifyDataSetChanged();
    }

    private void loadFromDatabase() {
        new Thread() {
            @Override
            public void run() {
                TodoListDao dao = TodoListDatabase.inst(getApplicationContext()).todoListDao();
                final List<TodoListEntity> entityList = dao.loadAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(entityList);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        }.start();
    }
}
