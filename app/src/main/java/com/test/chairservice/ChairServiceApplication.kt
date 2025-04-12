package com.test.chairservice

import android.app.Application
import com.test.chairservice.di.repositoryModule
import com.test.chairservice.di.viewModelModule
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