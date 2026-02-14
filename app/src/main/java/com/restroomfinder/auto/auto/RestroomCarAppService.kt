package com.restroomfinder.auto.auto

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class RestroomCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return RestroomSession()
    }
}

class RestroomSession : Session() {

    override fun onCreateScreen(intent: Intent): Screen {
        return RestroomListScreen(carContext)
    }
}
