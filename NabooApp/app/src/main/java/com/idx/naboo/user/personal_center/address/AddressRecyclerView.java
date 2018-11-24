package com.idx.naboo.user.personal_center.address;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.user.personal_center.order.OrderDetailsActivity;

import java.util.List;

/**
 * Created by ryan on 18-4-13.
 * Email: Ryan_chan01212@yeah.net
 */

public class AddressRecyclerView extends RecyclerView.Adapter<AddressRecyclerView.AddressHolder> {
    private List<DataBean> lists;
    private Context context;
    private static final String TAG = "AddressRecyclerView";
    public AddressRecyclerView(Context context, List<DataBean> lists) {
        this.context = context;
        this.lists = lists;
    }
    private OnItemClickListener mClickListener;
    private OnItemClickListenerDelete mClickListenerDelete;
    private OnSelectAddressListener mAddressListener;

    @Override
    public AddressRecyclerView.AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(NabooApplication.getInstance().getBaseContext()).inflate(R.layout.address_item,parent,false);
        AddressHolder holder = new AddressHolder(view,mClickListener,mClickListenerDelete,mAddressListener);
        return holder;
    }


//    @Override
//    public void onBindViewHolder(AddressHolder holder, int position, List<Object> payloads) {
//        if (payloads.isEmpty()){
//            onBindViewHolder(holder,position);
//        }else {
//            String payload = (String) payloads.get(0);
//            Log.d(TAG, "onBindViewHolder: "+ payload);
//
//            //需要更新的控件
//            final DataBean dataBean = lists.get(position);
//            holder.text_id.setText(String.valueOf(position+1));
//            holder.list_address_name.setText(dataBean.getConsignee());
//            holder.list_address_phone.setText(dataBean.getConsigneeMobile());
//            holder.list_address_select.setText(dataBean.getAddressRegion());
//            holder.list_address_detailed.setText(dataBean.getAddressDetail());
//            holder.labels.setText(dataBean.getAddressAlias());
//        }
//    }

    @Override
    public void onBindViewHolder(final AddressRecyclerView.AddressHolder holder, int position) {
        final DataBean dataBean = lists.get(position);
        holder.text_id.setText(String.valueOf(position+1));
        holder.list_address_name.setText(dataBean.getConsignee());
        holder.list_address_phone.setText(dataBean.getConsigneeMobile());
        holder.list_address_select.setText(dataBean.getAddressRegion());
        holder.list_address_detailed.setText(dataBean.getAddressDetail());

        if (!TextUtils.isEmpty(dataBean.getAddressAlias())) {
            holder.labels.setVisibility(View.VISIBLE);
            if (dataBean.getAddressAlias().equals("家")) {
                holder.labels.setBackgroundColor(Color.RED);
                holder.labels.setTextColor(context.getResources().getColor(R.color.figure_experience_text_color));
                holder.labels.setText(dataBean.getAddressAlias());
                return;
            }
            if (dataBean.getAddressAlias().equals("公司")) {
                holder.labels.setBackgroundColor(Color.BLUE);
                holder.labels.setTextColor(context.getResources().getColor(R.color.figure_experience_text_color));
                holder.labels.setText(dataBean.getAddressAlias());
                return;
            }
            holder.labels.setBackgroundColor(Color.GRAY);
            holder.labels.setTextColor(context.getResources().getColor(R.color.figure_experience_text_color));
            holder.labels.setText(dataBean.getAddressAlias());

        }else{
            holder.labels.setVisibility(View.GONE);
        }
//        holder.relative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: 更新");
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                NabooApplication.getContext().startActivity(intent);
//
//
//            }
//        });
//        if (mAddressListener!=null) {
//            holder.mRoot.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mAddressListener.onSelectAddressListener(dataBean.getConsignee(),dataBean.getConsigneeMobile(),dataBean.getAddressRegion(),dataBean.getAddressDetail());
//                }
//            });
//        }
    }


    public interface OnSelectAddressListener{
        void onSelectAddressListener(String username,String phone,String address,String detail);}

    public void setOnSelectAddressListener(
            OnSelectAddressListener onSelectAddressListener){mAddressListener=onSelectAddressListener;}

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemClickListenerDelete {
        void onItemClickDelete(View view, int position);
    }

    public void setOnItemClickListenerDelete(OnItemClickListenerDelete listener) {
        this.mClickListenerDelete = listener;}

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;}

    @Override
    public int getItemCount() {
        return lists.size();
    }
    class AddressHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnSelectAddressListener mOnSelectAddressListener;
        private OnItemClickListener mListener;// 声明自定义的接口
        LinearLayout mRoot;
        private OnItemClickListenerDelete mListener_delete;// 声明自定义的接口
        TextView list_address_detailed;
        TextView list_address_select;
        TextView list_address_phone;
        TextView list_address_name;
        TextView text_id;
        LinearLayout delete;
        LinearLayout bianji;
        TextView labels;

        public AddressHolder(View itemView,OnItemClickListener listener,OnItemClickListenerDelete listenerDelete,OnSelectAddressListener onSelectAddressListener1) {
            super(itemView);
            mListener = listener;
            mOnSelectAddressListener = onSelectAddressListener1;
            mRoot=itemView.findViewById(R.id.line1);
            mListener_delete = listenerDelete;
            text_id = itemView.findViewById(R.id.text_id);
            labels = itemView.findViewById(R.id.labels);
            list_address_name = itemView.findViewById(R.id.list_address_name);
            list_address_phone = itemView.findViewById(R.id.list_address_phone);
            list_address_select = itemView.findViewById(R.id.list_address_select);
            list_address_detailed = itemView.findViewById(R.id.list_address_detailed);
            delete = itemView.findViewById(R.id.linearLayout_delete);
            bianji = itemView.findViewById(R.id.linearLayout_bianji);
            delete.setOnClickListener(this);
            bianji.setOnClickListener(this);
            mRoot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // getpostion()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去

            switch (v.getId()){
                case R.id.linearLayout_delete:
                    mListener_delete.onItemClickDelete(v,getPosition());
                    break;
                case R.id.linearLayout_bianji:
                    mListener.onItemClick(v, getPosition());

                    break;

                case R.id.line1:
                    String name =  list_address_name.getText().toString().trim();
                    String phone = list_address_phone.getText().toString().trim();
                    String address = list_address_select.getText().toString().trim();
                    String detail =list_address_detailed.getText().toString().trim();
                    mOnSelectAddressListener.onSelectAddressListener(name,phone,address,detail);
                    break;
            }
        }
    }

}
