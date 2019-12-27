package com.zy.filmticket.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zy.filmticket.R;
import com.zy.filmticket.entity.FilmEntity;

import org.w3c.dom.Text;

import java.util.List;

public class FilmAdaptor extends BaseAdapter {
    private List<FilmEntity> filmEntityList;
    private Context context;
    private String activityType;

    public FilmAdaptor(List<FilmEntity> filmEntityList, Context context,String activityType) {
        this.filmEntityList = filmEntityList;
        this.context = context;
        this.activityType=activityType;
    }

    @Override
    public int getCount() {
        return filmEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return filmEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context, R.layout.main_film_layout,null);
            new ViewHolder(convertView);
        }
        new ViewHolder(convertView);
        final ViewHolder holder=(ViewHolder)convertView.getTag();
        FilmEntity entity= (FilmEntity) getItem(position);
        Glide.with(context).load(entity.getCoverUrl()).into(holder.imageView);
        holder.filmName.setText(entity.getFilmName());
        Double rating=entity.getRating();
        if ("0.0".equals(rating)){
            holder.filmRating.setText("暂未评分");
        }else{
            holder.filmRating.setText(entity.getRating()+"");
        }
        if ("mainFragement".equals(activityType)){
            holder.tv_buyTicket.setText("购票");
            holder.tv_buyTicket.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else{
            holder.tv_buyTicket.setText("");
        }
        holder.filmType.setText(entity.getScreenTypes());
        return convertView;
    }

    class ViewHolder{
        public ImageView imageView;
        public TextView filmName;
        public TextView filmRating;
        public TextView filmType;
        public TextView tv_buyTicket;
        public ViewHolder(View view){
            tv_buyTicket=view.findViewById(R.id.buyFilmTicket);
            filmRating=view.findViewById(R.id.filmRating);
            filmType=view.findViewById(R.id.filmType);
            imageView=view.findViewById(R.id.main_show_film);
            filmName=view.findViewById(R.id.main_film_name);
            view.setTag(this);
        }
    }
}
