package com.ooredoo.bizstore.model;

import android.database.DatabaseUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

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
        this.id = 0;
        this.keyword = "";
        this.resultCount = 0;
    }

    public SearchItem(int id, String keyword, int resultCount) {
        this.id = id;
        this.keyword = keyword;
        this.resultCount = resultCount;
    }

    public static void addToRecentSearches(SearchItem searchItem) {
        if(searchItem != null && isNotNullOrEmpty(searchItem.keyword)) {
            String keyword = searchItem.keyword.replaceAll("'", "''");
            List<SearchItem> searchItems = new Select().all().from(SearchItem.class).where("keyword='" + keyword + "'").execute();
            if(searchItems != null && searchItems.size() > 0) {
                for(SearchItem item : searchItems) {
                    item.delete();
                }
            }
            searchItem.save();
        }
    }
}
