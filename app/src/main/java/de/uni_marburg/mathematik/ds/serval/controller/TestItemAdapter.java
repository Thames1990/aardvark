package de.uni_marburg.mathematik.ds.serval.controller;

import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;

/**
 * Created by thames1990 on 22.08.17.
 */
public class TestItemAdapter extends BaseAdapter<TestItem, TestItemViewHolder> {

    public TestItemAdapter(List<TestItem> items) {
        super(items);
    }

    @Override
    public TestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestItemViewHolder(parent, R.layout.item_card);
    }

    @Override
    protected void onBindViewHolder(TestItemViewHolder holder, TestItem item, int position) {

    }

}
