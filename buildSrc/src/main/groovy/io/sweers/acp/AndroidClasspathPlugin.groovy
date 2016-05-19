package io.sweers.acp

import com.android.build.gradle.internal.LoggerWrapper
import com.android.build.gradle.internal.SdkHandler
import com.android.utils.ILogger
import com.google.common.collect.ImmutableList
import org.gradle.api.Plugin
import org.gradle.api.Project

public class AndroidClasspathPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {

    project.extensions.add('acp', AndroidClasspathExtension)

    project.configurations.create("providedAar")

    if (project.plugins.hasPlugin('com.android.application')
        || project.plugins.hasPlugin('com.android.library')
        || project.plugins.hasPlugin('com.android.test')) {
      throw new IllegalStateException('ACP Plugin is unnecessary on Android projects')
    }

    // TODO Try this first but fall back to manual searching otherwise
    ILogger logger = new LoggerWrapper(project.logger)
    SdkHandler sdkHandler = new SdkHandler(project, logger)
    ImmutableList<File> repositories;
    String sdkLocation;
    try {
      sdkHandler.getAndCheckSdkFolder()
      repositories = sdkHandler.sdkLoader.repositories;
      sdkLocation = sdkHandler.getSdkFolder().absolutePath;
    } catch (RuntimeException ignored) {
      // TODO Fall back to manual attempt to find
      repositories = ImmutableList.of();
      sdkLocation = project.extensions.acp.sdkPath;
    }
    for (File file : repositories) {
      project.repositories.maven {
        url = file.toURI()
      }
    }

    project.convention.plugins.androidApi =
        new AndroidApiConvention(project, sdkLocation)

    project.afterEvaluate {
      File buildDir = project.buildDir
      File acpJars = new File(buildDir, "acp-exploded-aars")
      if (!acpJars.exists()) {
        acpJars.mkdir()
      }
      project.configurations.providedAar.resolutionStrategy {

      }
      project.configurations.providedAar.each { File aarDep ->
        File dest = new File(acpJars, aarDep.name + '.jar')
        if (dest.exists()) {
          dest.delete()
        }
        project.copy {
          from project.zipTree(aarDep)
          include 'classes.jar'
          into acpJars
          rename { String fileName ->
            fileName.replace('classes.jar', dest.name)
          }
        }
      }
      project.dependencies.add("provided", project.fileTree(dir: acpJars.absolutePath, include: '*.jar'))
    }
  }
}
