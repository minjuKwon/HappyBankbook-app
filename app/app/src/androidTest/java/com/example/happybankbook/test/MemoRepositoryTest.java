package com.example.happybankbook.test;

import static com.example.happybankbook.fake.FakeDatabaseModule.provideMemoDao;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class MemoRepositoryTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    RoomDB db;

    private MemoDao dao;

    @Before
    public void createDb(){
        hiltRule.inject();
        dao= provideMemoDao(db);
    }

    @After
    public void closeDb(){
        if (db!=null&&db.isOpen()) {
            db.clearAllTables();
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
    public void givenEmptyMemoList_whenGetToRowCount_thenSizeIsZero(){
        int cnt=dao.getRowCount();

        assertEquals(0,cnt);
    }

    @Test
    public void givenEmptyMemoList_whenMemoAdded_thenReturnsRowCount() {
        dao.insert(createTempData(1,10,20250101,"content1"));
        int cnt=dao.getRowCount();

        assertEquals(1,cnt);
    }

    @Test
    public void givenEmptyMemoList_whenMultipleMemoAdded_thenReturnsRowCount(){
        insertDataList();
        int cnt=dao.getRowCount();

        assertEquals(5,cnt);
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
        assertEquals(1, list.get(0).getNum());
        assertEquals(3, list.get(1).getNum());
        assertEquals(4, list.get(2).getNum());
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

    @Test
    public void givenMemoAdded_whenSortDescAndCnt_thenReturnsSortedList(){
        insertDataList();
        List<MemoData> list=dao.searchDesc(20250101, 20250501,3).blockingFirst();

        assertEquals(3,list.size());
        assertEquals(5,list.get(0).getNum());
        assertEquals(4,list.get(1).getNum());
        assertEquals(3,list.get(2).getNum());
    }

    @Test
    public void givenMemoAdded_whenSortDescAndDate_thenReturnsSortedList(){
        insertDataList();
        List<MemoData> list=dao.searchDesc(20250101, 20250201,5).blockingFirst();

        assertEquals(2,list.size());
        assertEquals(2,list.get(0).getNum());
        assertEquals(1,list.get(1).getNum());
    }

    @Test
    public void givenMemoAdded_whenFilterDate_thenReturnsFilteredListCount(){
        insertDataList();
        int cnt=dao.getRangeCount(20250301);

        assertEquals(3,cnt);
    }

    @Test
    public void givenMemoAdded_whenChangeNum_thenReturnsNumSortedList(){
        List<MemoData> list=new ArrayList<>();
        list.add(createTempData(1,20,20250301,"memo 2"));
        list.add(createTempData(1,10,20250201,"content 2"));

        for(MemoData data:list){
            dao.insert(data);
        }

        dao.changeNum(20250201);

        List<MemoData> result= dao.getAll().blockingFirst();
        assertEquals(2,result.size());
        assertEquals(2, result.get(0).getNum());
        assertEquals(20, result.get(0).getPrice());
        assertEquals(1, result.get(1).getNum());
        assertEquals(10, result.get(1).getPrice());
    }

    private MemoData createTempData(int num, int price, int date, String content){
        MemoData data=new MemoData();
        data.setNum(num);
        data.setPrice(price);
        data.setDate(date);
        data.setContent(content);
        return data;
    }

    private void insertDataList(){
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
