package net.serverpeon.gradle.patchandroid

import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class PatchAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(
            "patchAndroid",
            PatchAndroidExtension::class.java
        )

        target.afterEvaluate(ApplyPatches)
    }

    private object ApplyPatches : Action<Project> {
        override fun execute(t: Project) {
            val androidExtension = t.extensions.findByName("android") as? BaseExtension ?: return

            // Find all applicable patches
            resolvePatches(t).forEach { action ->
                action.execute(androidExtension)
            }
        }

        private fun resolvePatches(subject: Project): Sequence<Action<BaseExtension>> =
            generateSequence(subject, Project::getParent)
                .reversed() // Apply from root down, so more specific projects override their parents if required
                .mapNotNull { it.extensions.findByType(PatchAndroidExtension::class.java) }
                .flatMap { it.actions() }
    }

    companion object {
        private fun <T> Sequence<T>.reversed(): Sequence<T> = Sequence {
            this.toList().asReversed().iterator()
        }
    }
}