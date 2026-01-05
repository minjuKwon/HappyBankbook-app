package com.example.happybankbook.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.happybankbook.db.UiMemoData;

import java.util.List;

public class MemoDiffUtilCallback extends DiffUtil.Callback {

    private final List<UiMemoData> oldList;
    private final List<UiMemoData> newList;

    public MemoDiffUtilCallback(List<UiMemoData> oldList, List<UiMemoData> newList) {
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
        UiMemoData oldItem= oldList.get(oldItemPosition);
        UiMemoData newItem= newList.get(newItemPosition);
        return oldItem.getNum()==newItem.getNum();
    }

}