package com.ooredoo.bizstore.model;

import java.util.List;

/**
 * @author Pehlaj Rai.
 * @since 03/20/2014.
 */
public class Deals {

    public final String jsonData;

    final public List<Deal> list;

    public Deals(List<Deal> list, String jsonData) {
        this.list = list;
        this.jsonData = jsonData;
    }
}