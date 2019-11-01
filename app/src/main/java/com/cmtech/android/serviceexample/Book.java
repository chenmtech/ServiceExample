package com.cmtech.android.serviceexample;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Book extends LitePalSupport {
    private int id;

    public Person person;

    public int price;

    private Person getPerson() {
        return LitePal.where("book_id = ?", String.valueOf(id)).findFirst(Person.class);
    }

    @Override
    public String toString() {
        return getPerson() + " " + price;
    }
}
