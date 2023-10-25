package org.nosemaj.rickmorty.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nosemaj.rickmorty.data.net.RickAndMortyService

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesRickAndMortyService(): RickAndMortyService {
        return RickAndMortyService.create()
    }

    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application
    }
}