package com.zy.filmticket.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zy.filmticket.R;
import com.zy.filmticket.entity.FilmReleaseEntity;

import java.util.List;

public class ReleaseFilmAdapter extends BaseAdapter {
    private Context context;
    private List<FilmReleaseEntity> filmReleaseEntityList;

    public ReleaseFilmAdapter(Context context, List<FilmReleaseEntity> filmReleaseEntityList) {
        this.context = context;
        this.filmReleaseEntityList = filmReleaseEntityList;
    }

    @Override
    public int getCount() {
        return filmReleaseEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return filmReleaseEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context, R.layout.releasefilmplay,null);
            new  ViewHolder(convertView);
        }
        new ViewHolder(convertView);
        FilmReleaseEntity entity= (FilmReleaseEntity) getItem(position);
        final ViewHolder viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.filmName.setText(entity.getFilmName());
        viewHolder.filmReleaseDate.setText(entity.getReleaseDate());
        viewHolder.filmReleaseTime.setText(entity.getReleaseTime());
        viewHolder.filmNum.setText("场次："+entity.getReleaseNum());
        viewHolder.palyPosition.setText("影厅："+entity.getReleasePosition());
        return convertView;
    }

    class ViewHolder{
        public TextView filmName;
        public TextView filmReleaseDate;
        public TextView filmReleaseTime;
        public TextView palyPosition;
        public TextView filmNum;

        public ViewHolder(View view){
            filmReleaseDate=view.findViewById(R.id.releaseDate);
            filmNum=view.findViewById(R.id.filmType);
            filmReleaseTime=view.findViewById(R.id.releaseTime);
            filmName=view.findViewById(R.id.filmName);
            palyPosition=view.findViewById(R.id.playPosition);
            view.setTag(this);
        }
    }


}
