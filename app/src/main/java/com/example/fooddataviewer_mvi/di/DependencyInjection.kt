package com.example.fooddataviewer_mvi.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.fooddetails.FoodDetailsViewModel
import com.example.fooddataviewer_mvi.foodlist.FoodListViewModel
import com.example.fooddataviewer_mvi.model.ProductService
import com.example.fooddataviewer_mvi.model.database.ApplicationDatabase
import com.example.fooddataviewer_mvi.scan.ScanViewModel
import com.example.fooddataviewer_mvi.utils.ActivityService
import com.example.fooddataviewer_mvi.utils.Navigator
import com.google.android.material.tabs.TabLayout
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.*
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.reflect.KClass


@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal  annotation class ViewModelKey(val value: KClass<out ViewModel>)

@MustBeDocumented
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ApiBaseUrl

@Singleton
@Component(modules = [
    ApplicationModule::class,
    ViewModelModule::class,
    ApiModule::class,
    DatabaseModule::class
    ]
)
interface ApplicationComponent {

    fun viewModelFactory():ViewModelProvider.Factory

    fun activityService(): ActivityService

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }
}

@Module
object ApplicationModule {
    @Provides
    @Singleton
    @JvmStatic
    fun viewModels(providers:MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelProvider.Factory {
        return ViewModelFactory(providers)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun activityService(): ActivityService = ActivityService()

    @Provides
    @Singleton
    @JvmStatic
    fun navigator(activityService: ActivityService): Navigator {
        return Navigator(R.id.navigationHostFragment, activityService)
    }

    @Provides
    @JvmStatic
    @ApiBaseUrl
    fun apiBaseUrl(context: Context): String = context.getString(R.string.api_base_url)
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FoodListViewModel::class)
    abstract fun foodListViewModel(viewModel: FoodListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScanViewModel::class)
    abstract fun scanViewModel(viewModel: ScanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoodDetailsViewModel::class)
    abstract fun foodDetailsViewModel(viewModel: FoodDetailsViewModel): ViewModel
}

@Module
object ApiModule {

    @Provides
    @Singleton
    @JvmStatic
    fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(chuckInterceptor(context))
//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun chuckInterceptor(context: Context) = ChuckInterceptor(context)

    @Provides
    @Singleton
    @JvmStatic
    fun retrofit(@ApiBaseUrl apiBaseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun productService(retrofit: Retrofit): ProductService {
       return retrofit.create(ProductService::class.java)
    }
}

@Module
object DatabaseModule {

    @Provides
    @Singleton
    @JvmStatic
    fun applicationDatabase(context: Context): ApplicationDatabase{
        return Room.databaseBuilder(context, ApplicationDatabase::class.java, "application")
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun productDao(database: ApplicationDatabase) = database.productDao()
}