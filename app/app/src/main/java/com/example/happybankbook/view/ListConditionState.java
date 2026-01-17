package com.example.happybankbook.view;

import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_COUNT;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_FROM_DATE;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_SORT;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_TO_DATE;

public class ListConditionState {
    boolean isNewestSort=DEFAULT_SORT;
    int fromDate=DEFAULT_FROM_DATE;
    int toDate=DEFAULT_TO_DATE;
    int count=DEFAULT_COUNT;
}