package com.example.happybankbook.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.happybankbook.db.MemoData;

import java.util.List;

public class MemoDiffUtilCallback extends DiffUtil.Callback {

    private final List<MemoData> oldList;
    private final List<MemoData> newList;

    public MemoDiffUtilCallback(List<MemoData> oldList, List<MemoData> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getIdx() == newList.get(newItemPosition).getIdx();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        MemoData oldItem= oldList.get(oldItemPosition);
        MemoData newItem= newList.get(newItemPosition);
        return oldItem.getNum()==newItem.getNum();
    }

}