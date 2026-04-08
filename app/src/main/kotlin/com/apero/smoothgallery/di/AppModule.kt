package com.apero.smoothgallery.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Hilt module — ImageLoader configured via SingletonImageLoader.Factory in SmoothGalleryApp. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
