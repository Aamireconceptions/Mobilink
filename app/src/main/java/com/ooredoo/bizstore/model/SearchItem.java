package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Pehlaj Rai
 * @since 6/24/2015.
 */
@Table(name = "obs_recent_searches")
public class SearchItem extends Model {

    @Column(name = "searchId", notNull = true)
    public int id;

    @Column(name = "keyword")
    public String keyword;

    @Column(name = "results")
    public int resultCount;

    public SearchItem() {
        id = 0;
        keyword = "";
        resultCount = 0;
    }

    public SearchItem(int id, String keyword, int resultCount) {
        this.id = id;
        this.keyword = keyword;
        this.resultCount = resultCount;
    }
}
