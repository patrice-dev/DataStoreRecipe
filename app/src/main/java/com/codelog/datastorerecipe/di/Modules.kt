package com.codelog.datastorerecipe.di

import android.content.Context
import com.codelog.datastorerecipe.data.repository.datastore.PreferenceDSManager
import com.codelog.datastorerecipe.data.repository.datastore.PreferenceProtoDSManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {


    @Singleton
    @Provides
    fun providePreferenceDS (@ApplicationContext context: Context) : PreferenceDSManager =
        PreferenceDSManager(context)

    @Singleton
    @Provides
    fun providePreferenceProtoDSManager(@ApplicationContext context: Context) : PreferenceProtoDSManager =
        PreferenceProtoDSManager(context)


}

