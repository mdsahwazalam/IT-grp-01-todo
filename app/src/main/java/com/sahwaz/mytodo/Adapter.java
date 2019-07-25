package com.sahwaz.mytodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    RealmResults<Todo> realmResults;
    Context mContext;
    public Adapter(RealmResults<Todo> todos, Context context){
        realmResults = todos;
        mContext = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Todo todo = realmResults.get(position);

        assert todo != null;
        holder.todo.setText(todo.getTodoItem());
        holder.details.setText(todo.getDetails());
        holder.due.setText(todo.getDuedate());
        //holder.card.setBackgroundColor(Color.parseColor(todo.getColor()));
        String color = todo.getColor();
        if(color.equals("Green"))
            holder.colorLayout.setBackgroundResource(R.drawable.recycler_shape_green);
        else if(color.equals("Red"))
            holder.colorLayout.setBackgroundResource(R.drawable.recycler_shape_red);
        else if(color.equals("Yellow"))
            holder.colorLayout.setBackgroundResource(R.drawable.recycler_shape_yellow);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Swipe left to delete",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public RealmResults<Todo> getData() {
        return realmResults;
    }

    public void removeItem(int position) {
        //realmResults.remove(position);
        Realm r = Realm.getDefaultInstance();
        r.beginTransaction();
        Todo todo = realmResults.get(position);
        todo.deleteFromRealm();
        r.commitTransaction();

        notifyItemRemoved(position);
    }

    public void restoreItem(String tcolor,String tUname,String tdet,String tdate,String ttodo, int position) {
        //realmResults.add(position, item);
        Realm r = Realm.getDefaultInstance();
        r.beginTransaction();
        //r.insert(item);
        //Log.d("log111", item);
        Todo todo = r.createObject(Todo.class);
        todo.setUserName(tUname);
        todo.setTodoItem(ttodo);
        todo.setDetails(tdet);
        todo.setDuedate(tdate);
        todo.setColor(tcolor);

        r.commitTransaction();
        r.close();

        //notifyItemInserted(realmResults.size()-1);
        notifyItemChanged(realmResults.size()-1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView todo;
        private TextView details;
        private TextView due;
        private ConstraintLayout card;
        private ConstraintLayout colorLayout;
        private ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            todo = view.findViewById(R.id.todo_tv);
            details = view.findViewById(R.id.details_tv);
            due = view.findViewById(R.id.due_tv);
            card=view.findViewById(R.id.rootView);
            colorLayout=view.findViewById(R.id.recycler_color_consLayout);
            imageView=view.findViewById(R.id.recycler_swipe_img);
        }
    }
}
