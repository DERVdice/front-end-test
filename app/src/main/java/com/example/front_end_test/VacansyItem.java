package com.example.front_end_test;

public class VacansyItem {

    private String vacancy_name;
    private String payment;
    private String company;
    private String tasks;
    private String requirements;
    private String link;

    VacansyItem(String _vacancy_name, String _payment, String _company, String _tasks, String _requirements, String _link){
        vacancy_name = _vacancy_name;
        payment = _payment;
        company = _company;
        tasks = _tasks;
        requirements = _requirements;
        link = _link;
    }

    public String getVacancy_name() {
        return vacancy_name;
    }

    public String getPayment() {
        return payment;
    }

    public String getCompany() {
        return company;
    }

    public String getTasks() {
        return tasks;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getLink() {
        return link;
    }
}
