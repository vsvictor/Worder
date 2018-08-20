package com.education.worder.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.education.worder.R;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder>{
    public interface OnRow{
        void onRowClick(Word word);
        void onRowLongClick(Word word);
    }
    private OnRow onRow;
    private ArrayList<Word> list;
    public WordAdapter(Words list){
        this.list = list.getData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cv = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup,false);
        ViewHolder holder = new ViewHolder(cv);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int item) {
        final Word word = list.get(item);
        viewHolder.tvWord.setText(word.getWord());
        viewHolder.cbSelect.setChecked(word.isSelected());
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word.setSelected(!word.isSelected());
                if(onRow != null) onRow.onRowClick(word);
            }
        });

        viewHolder.llItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onRow != null) onRow.onRowLongClick(word);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llItem;
        private AppCompatCheckBox cbSelect;
        private AppCompatTextView tvWord;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.llItem);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            tvWord = itemView.findViewById(R.id.tvWord);
        }
    }
    public void setData(Words words){
        this.list = words.getData();
        notifyDataSetChanged();
    }
    public void setOnRowListener(OnRow listener){
        this.onRow = listener;
    }
}
