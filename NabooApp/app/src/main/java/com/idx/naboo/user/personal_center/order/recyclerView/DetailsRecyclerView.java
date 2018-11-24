package com.idx.naboo.user.personal_center.order.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.user.personal_center.order.orderbean.Items;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;

import java.util.List;

/**
 * Created by ryan on 18-5-14.
 * Email: Ryan_chan01212@yeah.net
 */

public class DetailsRecyclerView extends RecyclerView.Adapter<DetailsRecyclerView.MyHolder> {

    private List<Items> items;
    private MyHolder holder;


    public DetailsRecyclerView(List<Items> items) {
        this.items = items;
    }

    @Override
    public DetailsRecyclerView.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(NabooApplication.getInstance().getBaseContext()).inflate(R.layout.details_item,parent,false);
        holder = new DetailsRecyclerView.MyHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(DetailsRecyclerView.MyHolder holder, int position) {
        holder.name.setText(String.valueOf(position + 1) + "、" + items.get(position).getName());

        holder.count.setText("×"+ items.get(position).getCount());

        double price = items.get(position).getPrice() / 100;
        holder.money.setText("¥" + price);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView count;
        TextView money;

        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.detail_name);
            count = itemView.findViewById(R.id.count);
            money = itemView.findViewById(R.id.money);
        }
    }

}
