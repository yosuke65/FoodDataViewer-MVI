package com.example.fooddataviewer_mvi.di


import android.content.Context
import com.example.fooddataviewer_mvi.TestFrameProcessorOnSubscribe
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        ViewModelModule::class,
        ApiModule::class,
        DatabaseModule::class,
        FakeModule::class
    ]
)
interface TestComponent : ApplicationComponent{
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }
}

@Module
object FakeModule {

    @Singleton
    @JvmStatic
    @Provides
    fun frameProcessorOnSubscribe(): FrameProcessorOnSubscribe =
        TestFrameProcessorOnSubscribe()
}
