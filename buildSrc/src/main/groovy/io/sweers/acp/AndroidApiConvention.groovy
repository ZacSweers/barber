package io.sweers.acp

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class AndroidApiConvention {

  public final String sdkLocation

  private Project project

  public AndroidApiConvention(Project project, String sdkLocation) {
    this.project = project
    this.sdkLocation = sdkLocation
  }

  // TODO Alternative approach of resolving jars for aars?
  // provided aar('something')
//  public FileCollection aar(File aarDep) {
//    File buildDir = project.buildDir
//    File acpJars = new File(buildDir, "acp-exploded-aars")
//    if (!acpJars.exists()) {
//      acpJars.mkdir()
//    }
//    File dest = new File(acpJars, aarDep.name + '.jar')
//    if (dest.exists()) {
//      dest.delete()
//    }
//    project.copy {
//      from project.zipTree(aarDep)
//      include 'classes.jar'
//      into acpJars
//      rename { String fileName ->
//        fileName.replace('classes.jar', dest.name)
//      }
//    }
//
//    return project.files(dest)
//  }

  public FileCollection androidApi(int apiLevel) {
    String jarLocation = "${sdkLocation}/platforms/android-${apiLevel}/android.jar"

    // TODO This doesn't seem to do anything to help it work with compileOnly
//    project.tasks.withType(JavaCompile) {
//      doFirst {
//        options.bootClasspath += jarLocation.tokenize(File.pathSeparator)
//      }
//    }
    return project.files(jarLocation)
  }
}
