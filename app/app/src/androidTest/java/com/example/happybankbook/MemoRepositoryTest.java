package com.example.happybankbook;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MemoRepositoryTest {

    private RoomDB db;
    private MemoDao dao;

    @Before
    public void createDb(){
        Context contest = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db= Room.inMemoryDatabaseBuilder(contest, RoomDB.class)
                .allowMainThreadQueries()
                .build();
        dao=db.memoDao();
    }

    @After
    public void closeDb(){
        if (db!=null&&db.isOpen()) {
            db.close();
        }
    }

    @Test
    public void givenEmptyMemoList_whenGetToSize_thenSizeIsZero(){
        List<MemoData> list=dao.getAll().blockingFirst();

        assertEquals(0,list.size());
    }

    @Test
    public void givenEmptyMemoList_whenMemoAdded_thenSizeIncrease(){
        dao.insert(createTempData(1,10,20250101,"content1"));
        List<MemoData> list=dao.getAll().blockingFirst();

        assertEquals(1,list.size());
        assertEquals(10, list.get(0).getPrice());
    }

    @Test
    public void givenEmptyMemoList_whenMultipleMemoAdded_thenSizeIncrease(){
        insertDataList();
        List<MemoData> list=dao.getAll().blockingFirst();

        assertEquals(5,list.size());
    }

    @Test
    public void givenEmptyMemoList_whenGetTotalCount_thenReturnsZero(){
        long actualPrice=dao.getTotalPrice();

        assertEquals(0,actualPrice);
    }

    @Test
    public void givenMemoAdded_whenGetTotalCount_thenReturnsCorrectValue(){
        insertDataList();
        long actualPrice=dao.getTotalPrice();

        assertEquals(150,actualPrice);
    }

    @Test
    public void givenMemoAdded_whenSearchBlank_thenReturnsAllList(){
        insertDataList();
        List<MemoData> list=dao.searchKeyword("").blockingFirst();

        assertEquals(5,list.size());
    }

    @Test
    public void givenMemoAdded_whenSearchKeyword_thenReturnsFilteredList(){
        insertDataList();
        List<MemoData> list=dao.searchKeyword("memo").blockingFirst();

        assertEquals(3,list.size());
    }

    @Test
    public void givenMemoAdded_whenSortAScAndCnt_thenReturnsSortedList(){
        insertDataList();
        List<MemoData> list=dao.searchAsc(20250101, 20250501,3).blockingFirst();

        assertEquals(3,list.size());
        assertEquals(1,list.get(0).getNum());
        assertEquals(2,list.get(1).getNum());
        assertEquals(3,list.get(2).getNum());
    }

    @Test
    public void givenMemoAdded_whenSortAscAndDate_thenReturnsSortedList(){
        insertDataList();
        List<MemoData> list=dao.searchAsc(20250101, 20250201,5).blockingFirst();

        assertEquals(2,list.size());
        assertEquals(1,list.get(0).getNum());
        assertEquals(2,list.get(1).getNum());
    }

    public MemoData createTempData(int num, int price, int date, String content){
        MemoData data=new MemoData();
        data.setNum(num);
        data.setPrice(price);
        data.setDate(date);
        data.setContent(content);
        return data;
    }

    public void insertDataList(){
        List<MemoData> list=new ArrayList<>();
        list.add(createTempData(1,10,20250101,"memo 1"));
        list.add(createTempData(2,20,20250201,"content 2"));
        list.add(createTempData(3,30,20250301,"memo 3"));
        list.add(createTempData(4,40,20250401,"memo 4"));
        list.add(createTempData(5,50,20250501,"content 5"));
        for(MemoData data:list){
            dao.insert(data);
        }
    }

}
