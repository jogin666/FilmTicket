package com.zy.filmticket.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zy.filmticket.R;
import com.zy.filmticket.entity.CinemaEntity;

import java.util.List;
import java.util.Random;

public class CinemaAdaptor extends BaseAdapter {

    private Context context;
    private List<CinemaEntity> cinemaEntityList;

    public CinemaAdaptor(Context context, List<CinemaEntity> cinemaEntityList) {
        this.context = context;
        this.cinemaEntityList = cinemaEntityList;
    }

    @Override
    public int getCount() {
        return cinemaEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cinemaEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context, R.layout.cinema_layout,null);
            new ViewHelper(convertView);
        }
        new ViewHelper(convertView);
        final ViewHelper holder=(ViewHelper)convertView.getTag();
        CinemaEntity entity= (CinemaEntity) getItem(position);
        holder.tv_cinemaName.setText(entity.getCinemaName());
        holder.tv_address.setText(entity.getCinemaAddrss());
        holder.tv_content.setText(conment());
        return convertView;
    }

    class ViewHelper{
        public TextView tv_cinemaName;
        public TextView tv_address;
        public TextView tv_content;
        public ViewHelper(View view){
            tv_cinemaName=view.findViewById(R.id.cinemaName);
            tv_address=view.findViewById(R.id.cinemaPlace);
            tv_content=view.findViewById(R.id.content);
            view.setTag(this);
        }
    }


    public String conment(){
        Random random=new Random();
        int content=random.nextInt(100-80)+80;
        return  "好评:"+content+"%";
    }
}
