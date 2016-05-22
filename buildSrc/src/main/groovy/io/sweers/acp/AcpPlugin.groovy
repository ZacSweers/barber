package io.sweers.acp

import com.android.build.gradle.internal.LoggerWrapper
import com.android.build.gradle.internal.SdkHandler
import com.android.utils.ILogger
import com.google.common.collect.ImmutableList
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.file.FileCollection
import org.gradle.plugins.ide.idea.IdeaPlugin

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

public class AcpPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin('com.android.application')
                || project.plugins.hasPlugin('com.android.library')
                || project.plugins.hasPlugin('com.android.test')) {
            throw new IllegalStateException('ACP Plugin is unnecessary on Android projects')
        }

        AcpExtension acp = project.extensions.create('acp', AcpExtension)
        Configuration acpConfig = project.configurations.create("acp")
        acpConfig.setVisible(false)

        // TODO Try this first but fall back to manual searching otherwise
        ILogger logger = new LoggerWrapper(project.logger)
        SdkHandler sdkHandler = new SdkHandler(project, logger)
        ImmutableList<File> repositories
        String sdkLocation
        try {
            sdkHandler.getAndCheckSdkFolder()
            repositories = sdkHandler.sdkLoader.repositories
            sdkLocation = sdkHandler.getSdkFolder().absolutePath
        } catch (RuntimeException ignored) {
            // TODO Fall back to manual attempt to find
            repositories = ImmutableList.of()
            sdkLocation = acp.sdkPath
        }

        for (File file : repositories) {
            project.repositories.maven {
                url = file.toURI()
            }
        }

        Set<Configuration> configurations = [] as Set
        acp.configurations.each { String config ->
            try {
                configurations.add(project.configurations.getByName(config))
            } catch (UnknownConfigurationException ignored) {
            }
        }

        project.afterEvaluate {
            FileCollection acpDependencies = project.files('build/classes/main')
            configurations.each { Configuration configuration ->
                if (acp.api != null) {
                    acpDependencies += getAndroidJar(project, sdkLocation, acp.api)
                }
                acpDependencies += explodeAars(project, configuration)
            }

            project.compileJava.classpath += acpDependencies
            configureIdeaPlugin(project, acpDependencies)
        }
    }

    private static FileCollection explodeAars(Project project, Configuration configuration) {
        Set<File> jars = [] as Set
        configuration.resolvedConfiguration.resolvedArtifacts.each { ResolvedArtifact artifact ->
            String identifier = artifact.id.componentIdentifier.displayName
            File archive = artifact.file
            if (!identifier.contains(" ") && artifact.extension == "aar") {
                File jar = project.file("${project.rootProject.buildDir}/acp/${FilenameUtils.getBaseName(archive.name)}.jar")
                if (!jar.exists()) {
                    FileUtils.copyInputStreamToFile(getClassesJar(archive), jar)
                }
                jars.add(jar)
            }
        }

        return project.files(jars)
    }

    private static InputStream getClassesJar(File aar) {
        ZipFile zipFile = new ZipFile(aar)
        ZipEntry classesJarEntry = zipFile.entries().find {
            !it.directory && it.name == "classes.jar"
        } as ZipEntry
        if (classesJarEntry != null) {
            return zipFile.getInputStream(classesJarEntry)
        } else {
            return null
        }
    }

    private static FileCollection getAndroidJar(Project project, String sdkLocation, int apiLevel) {
        String jarLocation = "${sdkLocation}/platforms/android-${apiLevel}/android.jar"
        if (!new File(jarLocation).exists()) {
            throw new RuntimeException("Cannot find android api ${apiLevel} installed in ${sdkLocation}")
        }
        return project.files(jarLocation)
    }

    private void configureIdeaPlugin(Project project, FileCollection acpConfiguration) {
        project.dependencies {
            acp acpConfiguration
        }
        project.plugins.withType(IdeaPlugin) {
            project.idea.module {
                scopes.PROVIDED.plus += [project.configurations.getByName("acp")]
            }
        }
    }
}
