package com.romazelenin.addressbook.di

import com.romazelenin.addressbook.data.ImplUsersServiceApi
import com.romazelenin.addressbook.data.ServiceApi
import com.romazelenin.addressbook.domain.UsersServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private val baseUrl = "https://stoplight.io/mocks/kode-education/trainee-test/25143926/"

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideServiceApi(retrofit: Retrofit): ServiceApi {
        return retrofit.create()
    }

    @Singleton
    @Provides
    fun provideUsersServiceApi(serviceApi: ServiceApi):UsersServiceApi{
        return ImplUsersServiceApi(serviceApi)
    }

}