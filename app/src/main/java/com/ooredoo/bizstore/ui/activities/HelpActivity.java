package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ExpandableHelpListAdapter;
import com.ooredoo.bizstore.model.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class HelpActivity extends BaseActivity {

    ExpandableListView mExpListView;
    ExpandableHelpListAdapter mExpListAdapter;

    List<String> categories;
    Map<String, List<Question>> map;

    public HelpActivity() {
        super();
        layoutResId = R.layout.activity_help;
    }

    @Override
    public void init() {
        setupToolbar();
        populateHelpData();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void populateHelpData() {
        initCategories();
        initQuestionAnswers();

        mExpListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        mExpListAdapter = new ExpandableHelpListAdapter(this, categories, map);
        mExpListView.setAdapter(mExpListAdapter);

        setGroupIndicatorBounds();

        mExpListView.expandGroup(0);
        mExpListView.expandGroup(1);
    }

    private void initCategories() {
        categories = new ArrayList<>();
        categories.add("Top Questions");
        categories.add("More Questions");
    }

    private void initQuestionAnswers() {
        map = new HashMap<>();

        List<Question> topQuestions = new ArrayList<>();
        List<Question> moreQuestions = new ArrayList<>();

        topQuestions.add(new Question(1, "Q1", "A1"));
        topQuestions.add(new Question(1, "Q2", "A2"));
        topQuestions.add(new Question(1, "Q3", "A3"));

        moreQuestions.add(new Question(1, "Q1", "A1"));
        moreQuestions.add(new Question(1, "Q1", "A2"));
        moreQuestions.add(new Question(1, "Q3", "A3"));

        map.put(categories.get(0), topQuestions);
        map.put(categories.get(1), moreQuestions);
    }

    private void setGroupIndicatorBounds() {
        float width = getResources().getDisplayMetrics().widthPixels;

        int left = (int) (width - convertDpToPixels(50));
        int right = (int) (width - convertDpToPixels(20));

        mExpListView.setIndicatorBounds(left, right);
        if(android.os.Build.VERSION.SDK_INT >= 18) {
            mExpListView.setIndicatorBoundsRelative(left, right);
        }
    }
}