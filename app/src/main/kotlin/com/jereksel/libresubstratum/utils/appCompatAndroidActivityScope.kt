package com.jereksel.libresubstratum.utils

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.android.AndroidScope
import com.github.salomonbrys.kodein.bindings.AutoScope
import com.github.salomonbrys.kodein.bindings.ScopeRegistry
import java.util.*

object appCompatAndroidActivityScope : AndroidScope<AppCompatActivity>, AutoScope<AppCompatActivity> {

    private val _contextScopes = WeakHashMap<EmptyViewModel, ScopeRegistry>()

    /**
     * The last activity that was displayed to the screen. Used when this scope is used as an auto-scope.
     */
    private var _currentActivity: AppCompatActivity? = null


    /**
     * Get a registry for a given activity. Will always return the same registry for the same activity.
     *
     * @param context The activity associated with the returned registry.
     * @return The registry associated with the given activity.
     */
    override fun getRegistry(context: AppCompatActivity): ScopeRegistry
            = synchronized(_contextScopes) { _contextScopes.getOrPut(ViewModelProviders.of(context).get(EmptyViewModel::class.java)) { ScopeRegistry() } }

    /**
     * Allows for cleaning up after an activity has been destroyed
     */
    override fun removeFromScope(context: AppCompatActivity) = _contextScopes.remove(ViewModelProviders.of(context).get(EmptyViewModel::class.java))

    /**
     * @return The last activity that was displayed to the screen..
     */
    override fun getContext()
            = _currentActivity ?: throw IllegalStateException("There are no current activity. This can either mean that you forgot to register the androidActivityScope.lifecycleManager in your application or that there is currently no activity in the foreground.")


    /**
     * If you use `autoScopedSingleton(androidActivityScope)`, you **must** register this lifecycle manager in your application's oncreate:
     *
     * ```kotlin
     * class MyActivity : Activity {
     *     override fun onCreate() {
     *         registerActivityLifecycleCallbacks(androidActivityScope.lifecycleManager)
     *     }
     * }
     * ```
     */
    object lifecycleManager : Application.ActivityLifecycleCallbacks {
        /** @suppress */
        override fun onActivityCreated(act: Activity?, b: Bundle?) { _currentActivity = act as AppCompatActivity
        }
        /** @suppress */
        override fun onActivityStarted(act: Activity)              { _currentActivity = act as AppCompatActivity }
        /** @suppress */
        override fun onActivityResumed(act: Activity)              { _currentActivity = act as AppCompatActivity }

        /** @suppress */
        override fun onActivityPaused(act: Activity) = Unit
        /** @suppress */
        override fun onActivityStopped(act: Activity) = Unit
        /** @suppress */
        override fun onActivityDestroyed(act: Activity) { if (act == _currentActivity) _currentActivity = null }
        /** @suppress */
        override fun onActivitySaveInstanceState(act: Activity, b: Bundle?) = Unit
    }

    class EmptyViewModel: ViewModel()

}