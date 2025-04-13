package com.pistartech.postureperfect

import android.app.Application
import com.pistartech.postureperfect.di.repositoryModule
import com.pistartech.postureperfect.di.viewModelModule
import org.koin.core.context.startKoin

/**
 * Created by Siru malayil on 26-03-2025.
 */
class ChairServiceApplication: Application() {


    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(listOf(
                viewModelModule,
                repositoryModule
            ))
        }
    }
}