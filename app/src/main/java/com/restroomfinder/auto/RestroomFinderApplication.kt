package com.restroomfinder.auto

import android.app.Application
import com.restroomfinder.auto.data.repository.RestroomRepository

class RestroomFinderApplication : Application() {

    lateinit var restroomRepository: RestroomRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        restroomRepository = RestroomRepository(this)
    }

    companion object {
        lateinit var instance: RestroomFinderApplication
            private set
    }
}
