package com.globant.common.di

import com.globant.common.network.DispatcherDefault
import com.globant.common.network.DispatcherIO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @DispatcherIO
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DispatcherDefault
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}