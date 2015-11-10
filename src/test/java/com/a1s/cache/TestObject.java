package com.a1s.cache;

import java.io.Serializable;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public class TestObject implements Serializable {
    private String field;

    public TestObject(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

}
