package de.uni_marburg.mathematik.ds.serval.model.event

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList

class EventViewModel(application: Application) : AndroidViewModel(application) {

    val dao: EventDao by lazy {
        EventDatabase.get(application).eventDao()
    }

    val events: LiveData<PagedList<Event>> by lazy {
        LivePagedListBuilder(
            dao.getAllPaged(),
            PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
                .build()
        ).build()
    }

    fun reload() = ioThread {
        dao.deleteAll()
        dao.insert(EventRepository.fetch())
    }

    companion object {
        private const val PAGE_SIZE = 100
        private const val ENABLE_PLACEHOLDERS = true
    }
}