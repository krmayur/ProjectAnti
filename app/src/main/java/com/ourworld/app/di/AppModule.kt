package com.ourworld.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ourworld.app.data.repository.AuthRepository
import com.ourworld.app.data.repository.CoupleRepository
import com.ourworld.app.data.repository.GardenRepository
import com.ourworld.app.data.repository.GiftRepository
import com.ourworld.app.data.repository.MemoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepository(auth, firestore)

    @Provides
    @Singleton
    fun provideCoupleRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): CoupleRepository = CoupleRepository(firestore, auth)

    @Provides
    @Singleton
    fun provideGardenRepository(
        firestore: FirebaseFirestore
    ): GardenRepository = GardenRepository(firestore)

    @Provides
    @Singleton
    fun provideGiftRepository(
        firestore: FirebaseFirestore
    ): GiftRepository = GiftRepository(firestore)

    @Provides
    @Singleton
    fun provideMemoryRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): MemoryRepository = MemoryRepository(firestore, storage)
}
