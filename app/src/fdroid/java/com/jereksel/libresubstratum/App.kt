package com.jereksel.libresubstratum

import android.app.Application
import android.content.Context
import android.support.annotation.VisibleForTesting

import com.jereksel.libresubstratum.activities.ErrorActivity
import com.jereksel.libresubstratum.dagger.components.AppComponent
import com.jereksel.libresubstratum.dagger.components.DaggerAppComponent
import com.jereksel.libresubstratum.dagger.modules.AppModule

import cat.ereza.customactivityoncrash.config.CaocConfig

import cat.ereza.customactivityoncrash.config.CaocConfig.BACKGROUND_MODE_CRASH
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidActivityScope
import com.github.salomonbrys.kodein.android.autoAndroidModule
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.installed.InstalledPresenter
import com.jereksel.libresubstratum.domain.*
import com.jereksel.libresubstratum.utils.appCompatAndroidActivityScope

open class App : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(autoAndroidModule(this@App))
        bind<Context>() with singleton { this@App }
        bind<OverlayService>() with provider { OverlayServiceFactory.getOverlayService(instance()) }
        bind<IPackageManager>() with provider { AppPackageManager(instance()) }
        bind<IActivityProxy>() with provider { ActivityProxy(instance()) }
        bind<Metrics>() with provider { FdroidMetrics() }

        bind<InstalledContract.Presenter>() with autoScopedSingleton(appCompatAndroidActivityScope) { InstalledPresenter(instance(), instance(), instance(), instance()) }
    }

    private var component: AppComponent? = null

    protected open val appModule: AppModule
        get() = AppModule(this)

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(appCompatAndroidActivityScope.lifecycleManager)

        CaocConfig.Builder.create()
                .backgroundMode(BACKGROUND_MODE_CRASH)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                .errorActivity(ErrorActivity::class.java)
                .apply()
    }

    @VisibleForTesting
    protected fun createComponent(): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(appModule)
                .build()
    }

    fun getAppComponent(context: Context): AppComponent {
        val app = context.applicationContext as App
        if (app.component == null) {
            app.component = app.createComponent()
        }
        return app.component!!
    }

}
