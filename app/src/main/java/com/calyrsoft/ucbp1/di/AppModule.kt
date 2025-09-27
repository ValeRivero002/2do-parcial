package com.calyrsoft.ucbp1.di

import NotificationViewModel
import androidx.room.Room
import com.calyrsoft.ucbp1.core.AuthManager
import com.calyrsoft.ucbp1.features.dollar.data.database.AppRoomDatabase
import com.calyrsoft.ucbp1.features.dollar.data.datasource.DollarLocalDataSource
import com.calyrsoft.ucbp1.features.dollar.data.datasource.RealTimeRemoteDataSource
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
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.calyrsoft.ucbp1.features.dollar.data.database.DollarHistoryDao
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

    // Retrofit base (GitHub)
    single {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // GithubService
    single<GithubService> { get<Retrofit>().create(GithubService::class.java) }

    // MovieDB Service (otra base URL)
    single<MovieService> {
        get<Retrofit>().newBuilder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(get())
            .build()
            .create(MovieService::class.java)
    }

    // ========= Room DB =========
    single {
        Room.databaseBuilder(
            androidContext(),
            AppRoomDatabase::class.java,
            "app_ucb.db"
        )
            .fallbackToDestructiveMigration(true) // <- nuevo overload
            .build()
    }
    single<DollarHistoryDao> { get<AppRoomDatabase>().dollarHistoryDao() }

    // ========= DataSources =========
    single { RealTimeRemoteDataSource() }
    single { DollarLocalDataSource(get()) } // si lo usas en tu VM de historial

    // ========= Repositories =========
    single<LoginRepository> { LoginRepositoryImpl() }
    single<ProfileRepository> { ProfileRepositoryImpl() }
    single<IGithubRepository> { GithubRepository(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single { NotificationRepositoryImpl() }
    single<DollarHistoryDao> { get<AppRoomDatabase>().dollarHistoryDao() }

    // ========= UseCases =========
    factory { LoginUseCase(get()) }
    factory { FetchDollarUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { FindByNickNameUseCase(get()) }
    factory { GetPopularMoviesUseCase(get()) }

    // ========= ViewModels =========
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
    // Ajusta a la firma real de tu VM:
    // - Si DollarViewModel(fetchUseCase)  -> viewModel { DollarViewModel(get()) }
    // - Si DollarViewModel(fetchUseCase, localDataSource) -> deja get(), get()
    viewModel { DollarViewModel(get(), get()) }
    viewModel { GithubViewModel(get(), get()) }
    viewModel { MoviesViewModel(get()) }
    viewModel { DollarHistoryViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
}
