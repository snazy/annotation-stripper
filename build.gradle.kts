/*
 * Copyright (C) 2022 The Authors of this project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.gradle.ext.*

plugins {
  `maven-publish`
  id("org.projectnessie.buildsupport.ide-integration")
  id("org.projectnessie.buildsupport.publishing")
  alias(libs.plugins.nexus.publish)
  `annotation-stripper-conventions`
}

description = "Strips annotations from class files"

mapOf(
    // TODO update the Nessie Gradle plugins to not depend on these properties / move some of the
    //  plugins into the Nessie repository.
    "versionCheckstyle" to libs.versions.checkstyle.get(),
    "versionErrorProneCore" to libs.versions.errorprone.get(),
    "versionErrorProneSlf4j" to libs.versions.errorproneSlf4j.get(),
    "versionGoogleJavaFormat" to libs.versions.googleJavaFormat.get(),
    "versionJandex" to libs.versions.jandex.get()
  )
  .forEach { (k, v) -> extra[k] = v }

tasks.named<Wrapper>("wrapper") { distributionType = Wrapper.DistributionType.ALL }

// Pass environment variables:
//    ORG_GRADLE_PROJECT_sonatypeUsername
//    ORG_GRADLE_PROJECT_sonatypePassword
// OR in ~/.gradle/gradle.properties set
//    sonatypeUsername
//    sonatypePassword
// Call targets:
//    publishToSonatype
//    closeAndReleaseSonatypeStagingRepository
nexusPublishing {
  transitionCheckOptions {
    // default==60 (10 minutes), wait up to 60 minutes
    maxRetries.set(360)
    // default 10s
    delayBetween.set(java.time.Duration.ofSeconds(10))
  }
  repositories { sonatype() }
}

publishingHelper {
  nessieRepoName.set("annotation-stripper")
  inceptionYear.set("2023")
}

if (project.hasProperty("release")) {
  allprojects {
    apply<SigningPlugin>()
    apply<MavenPublishPlugin>()
    plugins.withType<SigningPlugin>().configureEach {
      configure<SigningExtension> {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
      }
    }

    configure<PublishingExtension> {
      publications {
        withType(MavenPublication::class.java).configureEach {
          val mavenPublication = this

          if (
            mavenPublication.name != "pluginMaven" &&
              !mavenPublication.name.endsWith("PluginMarkerMaven")
          ) {
            configure<SigningExtension> { sign(mavenPublication) }
          }
        }
      }
    }
  }
}
