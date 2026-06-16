package com.apero.composa.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Hilt module — ImageLoader configured via SingletonImageLoader.Factory in ComposaApp. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
