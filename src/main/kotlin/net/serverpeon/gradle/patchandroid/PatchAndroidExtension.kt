package net.serverpeon.gradle.patchandroid

import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import kotlin.reflect.KProperty

open class PatchAndroidExtension {
    private val actions = mutableListOf<Action<BaseExtension>>()
    private var consumed: Boolean = false

    internal fun actions(): Sequence<Action<BaseExtension>> {
        consumed = true // Disallow additional patches after this point
        return this.actions.asSequence()
    }

    // Kotlin DSL Helpers
    @get:JvmName("patch")
    val patch by AndroidFilter.ALL

    @get:JvmName("patchFeature")
    val patchFeature by AndroidFilter.FEATURE

    @get:JvmName("patchApplication")
    val patchApplication by AndroidFilter.APPLICATION

    @get:JvmName("patchInstantApp")
    val patchInstantApp by AndroidFilter.INSTANT_APP

    @get:JvmName("patchLibrary")
    val patchLibrary by AndroidFilter.LIBRARY

    @get:JvmName("patchTestable")
    val patchTestable by AndroidFilter.TESTABLE

    @get:JvmName("patchTest")
    val patchTest by AndroidFilter.TEST

    fun <T : BaseExtension> patch(filter: AndroidFilter<T>, configuration: Action<T>) {
        if (consumed) {
            throw GradleException("Attempting to add a patch after configuration has ended: $configuration")
        }

        this.actions.add(FilteredAction(filter, configuration))
    }

    class PatchSpec<T : BaseExtension>(
        private val extension: PatchAndroidExtension,
        private val filter: AndroidFilter<T>
    ) {
        @JvmName("registerAction")
        operator fun invoke(configuration: T.() -> Unit) {
            this.extension.patch(filter, Action(configuration))
        }
    }

    private class FilteredAction<T : BaseExtension>(
        private val filter: AndroidFilter<T>,
        private val configuration: Action<T>
    ) : Action<BaseExtension> {
        override fun execute(t: BaseExtension) {
            filter.apply(t, configuration)
        }
    }

    private operator fun <T : BaseExtension> AndroidFilter<T>.getValue(
        thisRef: PatchAndroidExtension,
        property: KProperty<*>
    ): PatchSpec<T> = PatchSpec(thisRef, this)
}