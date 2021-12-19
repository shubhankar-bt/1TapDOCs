package com.shubhankarsudip.a1tapdocs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewAdapter> {

    private List<uploadCARDS> list;
    private Context context;

    public CardsAdapter(List<uploadCARDS> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CardsViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_items_layout, parent,false);

        return new CardsViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewAdapter holder, int position) {

        uploadCARDS item = list.get(position);
        holder.name.setText(item.getCardSaverName());
        holder.cardNo.setText(item.getCardNumber());
        holder.Bname.setText(item.getBankName());
        holder.Expiry.setText(item.getExpDate());
        holder.usercvv.setText(item.getCVVno());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardsViewAdapter extends RecyclerView.ViewHolder {

        private TextView name,cardNo,Bname,Expiry , usercvv ;

        public CardsViewAdapter(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.UserCardName);
            cardNo = itemView.findViewById(R.id.UserCardNumber);
            Bname = itemView.findViewById(R.id.UserBankName);
            Expiry = itemView.findViewById(R.id.UserCardExpiryDate);
            usercvv = itemView.findViewById(R.id.UserCardCvvNumber);
           // category = itemView.findViewById(R.id.UserCardCategory);



        }
    }
}
