package com.cmtech.android.serviceexample;

import org.litepal.crud.LitePalSupport;

public class Person extends LitePalSupport {
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
