package de.chartsexplorer.chartsexplorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.chartsexplorer.chartsexplorer.R;
import de.chartsexplorer.chartsexplorer.util.SongSearch;

public class CustomSongSearchAdapter extends BaseAdapter {

    private static ArrayList<SongSearch> songArrayList;
    private LayoutInflater mInflater;

    public CustomSongSearchAdapter(Context context, ArrayList<SongSearch> songs){
        songArrayList = songs;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return songArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.song_search_item, null);
            holder = new ViewHolder();
            holder.songPosition = (TextView) convertView.findViewById(R.id.songPosition);
            holder.songYear = (TextView) convertView.findViewById(R.id.songYear);
            holder.songInterpret = (TextView) convertView
                    .findViewById(R.id.songInterpret);
            holder.songTitle = (TextView) convertView.findViewById(R.id.songTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.songPosition.setText(songArrayList.get(position).getPosition());
        holder.songYear.setText(songArrayList.get(position).getYear());
        holder.songInterpret.setText(songArrayList.get(position)
                .getInterpret());
        holder.songTitle.setText(songArrayList.get(position).getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView songPosition;
        TextView songYear;
        TextView songInterpret;
        TextView songTitle;
    }
}
