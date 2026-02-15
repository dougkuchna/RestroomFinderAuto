package com.restroomfinder.auto

import android.app.Application
import com.restroomfinder.auto.data.PreferencesManager
import com.restroomfinder.auto.data.repository.RestroomRepository

class RestroomFinderApplication : Application() {

    lateinit var restroomRepository: RestroomRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesManager = PreferencesManager(this)
        restroomRepository = RestroomRepository(this)
    }

    companion object {
        lateinit var instance: RestroomFinderApplication
            private set
    }
}
