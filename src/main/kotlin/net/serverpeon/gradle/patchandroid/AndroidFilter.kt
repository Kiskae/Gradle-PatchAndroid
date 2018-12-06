package net.serverpeon.gradle.patchandroid

import com.android.build.gradle.*
import org.gradle.api.Action

sealed class AndroidFilter<T : BaseExtension>(private val extensionType: Class<out T>) {
    protected open fun appliesTo(extension: BaseExtension): Boolean {
        return extensionType.isInstance(extension)
    }

    fun apply(extension: BaseExtension, configuration: Action<T>) {
        if (this.appliesTo(extension)) {
            configuration.execute(extensionType.cast(extension))
        }
    }

    object ALL : AndroidFilter<BaseExtension>(BaseExtension::class.java)

    object TESTABLE : AndroidFilter<TestedExtension>(TestedExtension::class.java)

    /**
     * Filter for 'com.android.test' projects
     */
    object TEST : AndroidFilter<TestExtension>(TestExtension::class.java)

    /**
     * Filter for 'com.android.application' projects
     */
    object APPLICATION : AndroidFilter<AppExtension>(AppExtension::class.java)

    /**
     * Filter for 'com.android.instantapp' projects
     */
    object INSTANT_APP : AndroidFilter<BaseExtension>(INSTANTAPP_CLASS)

    /**
     * Filter for 'com.android.feature' projects
     */
    object FEATURE : AndroidFilter<FeatureExtension>(FeatureExtension::class.java)

    object LIBRARY_LIKE : AndroidFilter<LibraryExtension>(LibraryExtension::class.java)

    /**
     * Filter for 'com.android.library' projects
     */
    object LIBRARY : AndroidFilter<LibraryExtension>(LibraryExtension::class.java) {
        override fun appliesTo(extension: BaseExtension): Boolean =
            super.appliesTo(extension) && (extension !is FeatureExtension)
    }
}