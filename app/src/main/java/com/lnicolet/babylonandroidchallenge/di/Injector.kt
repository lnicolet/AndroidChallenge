package com.lnicolet.babylonandroidchallenge.di

import android.app.Application
import com.lnicolet.babylonandroidchallenge.BabylonApp
import com.lnicolet.babylonandroidchallenge.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivitiesModule::class,
        ViewModelModule::class,
        AppModule::class,
        ApiModule::class,
        RepositoryModule::class
    ]
)
interface Injector : AndroidInjector<BabylonApp> {
    @Component.Builder
    interface Builder {
        fun build(): Injector

        @BindsInstance
        fun application(application: Application): Builder
    }

    fun inject(application: Application)
}