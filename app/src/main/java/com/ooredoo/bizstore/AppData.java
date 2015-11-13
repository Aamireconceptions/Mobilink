package com.ooredoo.bizstore;

import com.ooredoo.bizstore.model.PopularSearches;
import com.ooredoo.bizstore.model.Results;
import com.ooredoo.bizstore.model.SearchSuggestions;
import com.ooredoo.bizstore.model.UserAccount;

/**
 * @author Pehlaj Rai
 * @since 06-Jul-2015.
 */

public final class AppData {

    public static final UserAccount userAccount = new UserAccount();

    public static Results searchResults;

    public static SearchSuggestions searchSuggestions;

    public static PopularSearches popularSearches = new PopularSearches(); //Pre-defined searches fetched from server
}
