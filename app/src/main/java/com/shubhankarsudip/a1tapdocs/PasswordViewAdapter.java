package com.shubhankarsudip.a1tapdocs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PasswordViewAdapter extends RecyclerView.Adapter<PasswordViewAdapter.PasswordAdapter> {

private List<uploadPASSWORDS> list;
private Context context;

public PasswordViewAdapter(List<uploadPASSWORDS> list, Context context) {
        this.list = list;
        this.context = context;
        }

@NonNull
@Override
public PasswordAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_password_item, parent,false);

        return new PasswordAdapter(view);
        }

@Override
public void onBindViewHolder(@NonNull PasswordAdapter holder, int position) {

    uploadPASSWORDS item = list.get(position);
        holder.websiteName.setText(item.getWebsiteName());
        holder.UserName.setText(item.getWebsiteUser());
        holder.password.setText(item.getWebsitePassword());

        }

@Override
public int getItemCount() {
        return list.size();
        }

public class PasswordAdapter extends RecyclerView.ViewHolder {

    private TextView websiteName,UserName,password;

    public PasswordAdapter(@NonNull View itemView) {
        super(itemView);

        websiteName = itemView.findViewById(R.id.ShowWebsiteName);
        UserName = itemView.findViewById(R.id.ShowUserName);
        password = itemView.findViewById(R.id.ShowPassword);



    }
}
}
