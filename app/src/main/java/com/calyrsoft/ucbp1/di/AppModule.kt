package com.calyrsoft.ucbp1.di

import NotificationViewModel
import com.calyrsoft.ucbp1.core.AuthManager
import com.calyrsoft.ucbp1.features.dollar.data.database.AppRoomDatabase
import com.calyrsoft.ucbp1.features.dollar.data.datasource.DollarLocalDataSource
import com.calyrsoft.ucbp1.features.dollar.domain.repository.IDollarRepository
import com.calyrsoft.ucbp1.features.dollar.domain.usecase.FetchDollarUseCase
import com.calyrsoft.ucbp1.features.dollar.presentation.DollarHistoryViewModel
import com.calyrsoft.ucbp1.features.dollar.presentation.DollarViewModel
import com.calyrsoft.ucbp1.features.github.data.api.GithubService
import com.calyrsoft.ucbp1.features.github.data.datasource.GithubRemoteDataSource
import com.calyrsoft.ucbp1.features.github.data.repository.GithubRepository
import com.calyrsoft.ucbp1.features.github.domain.repository.IGithubRepository
import com.calyrsoft.ucbp1.features.github.domain.usecase.FindByNickNameUseCase
import com.calyrsoft.ucbp1.features.github.presentation.GithubViewModel
import com.calyrsoft.ucbp1.features.login.data.LoginRepositoryImpl
import com.calyrsoft.ucbp1.features.login.domain.repository.LoginRepository
import com.calyrsoft.ucbp1.features.login.domain.usecase.LoginUseCase
import com.calyrsoft.ucbp1.features.login.presentation.LoginViewModel
import com.calyrsoft.ucbp1.features.movies.data.api.MovieService
import com.calyrsoft.ucbp1.features.movies.data.datasource.remote.MovieRemoteDataSource
import com.calyrsoft.ucbp1.features.movies.data.repository.MovieRepositoryImpl
import com.calyrsoft.ucbp1.features.movies.domain.repository.MovieRepository
import com.calyrsoft.ucbp1.features.movies.domain.usecase.GetPopularMoviesUseCase
import com.calyrsoft.ucbp1.features.movies.presentation.viewmodel.MoviesViewModel
import com.calyrsoft.ucbp1.features.notification.data.repository.NotificationRepository
import com.calyrsoft.ucbp1.features.notification.data.repository.NotificationRepositoryImpl
import com.calyrsoft.ucbp1.features.profile.data.ProfileRepositoryImpl
import com.calyrsoft.ucbp1.features.profile.domain.repository.ProfileRepository
import com.calyrsoft.ucbp1.features.profile.domain.usecase.GetProfileUseCase
import com.calyrsoft.ucbp1.features.profile.presentation.ProfileViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val appModule = module {
    single { AuthManager(get()) }
    // OkHttpClient
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // GithubService
    single<GithubService> {
        get<Retrofit>().create(GithubService::class.java)
    }

    // MovieDB Service (base URL diferente)
    single<MovieService> {
        get<Retrofit>().newBuilder()
            .baseUrl("https://api.themoviedb.org/3/")
            .build()
            .create(MovieService::class.java)
    }

    // Repositories
    single<LoginRepository> { LoginRepositoryImpl() }
    single { com.calyrsoft.ucbp1.features.dollar.data.datasource.RealTimeRemoteDataSource() }
    single<ProfileRepository> { ProfileRepositoryImpl() }
    single<IDollarRepository> { DollarRepositoryImpl(get(), get()) }
    single { GithubRemoteDataSource(get()) }
    single<IGithubRepository> { GithubRepository(get()) }
    single { MovieRemoteDataSource(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single { AppRoomDatabase.getDatabase(get()) }
    single { get<AppRoomDatabase>().dollarDao() }
    single { DollarLocalDataSource(get()) }
    single { NotificationRepositoryImpl() as NotificationRepository }


    // UseCases
    factory { LoginUseCase(get()) }
    factory { FetchDollarUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { FindByNickNameUseCase(get()) }
    factory { GetPopularMoviesUseCase(get()) }


    // ViewModels
    viewModel { LoginViewModel(get(),get ()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { DollarViewModel(get(), get()) }
    viewModel { GithubViewModel(get(),get ()) }
    viewModel { MoviesViewModel(get()) }
    viewModel { DollarHistoryViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
}
