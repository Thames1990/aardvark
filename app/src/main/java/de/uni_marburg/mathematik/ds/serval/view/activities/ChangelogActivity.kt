package de.uni_marburg.mathematik.ds.serval.view.activities

import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.buildIsLollipopAndUp
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import de.uni_marburg.mathematik.ds.serval.R

class ChangelogActivity : ElasticRecyclerActivity() {

    private val adapter = FastItemAdapter<IItem<*, *>>()

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        recycler.adapter = adapter
        adapter.add(listOf(
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                },
                CardIItem {
                    titleRes = R.string.app_name
                    descRes = R.string.app_version
                }
        ))
        setOutsideTapListener {
            if (buildIsLollipopAndUp) finishAfterTransition()
            else finish()
        }
        return true
    }

}