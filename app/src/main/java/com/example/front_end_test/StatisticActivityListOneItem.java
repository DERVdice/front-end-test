package com.example.front_end_test;

public class StatisticActivityListOneItem {
    private String company;
    private String count;

    StatisticActivityListOneItem(String _company, String _count){
        company = _company;
        count = _count;
    }

    public String getCompany() {
        return company;
    }

    public String getCount() {
        return count;
    }
}
