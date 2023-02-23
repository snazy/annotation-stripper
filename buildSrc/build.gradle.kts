/*
 * Copyright (C) 2021 The Authors of this project
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

plugins { `kotlin-dsl` }

repositories {
  mavenCentral()
  gradlePluginPortal()
  if (System.getProperty("withMavenLocal").toBoolean()) {
    mavenLocal()
  }
}

dependencies {
  implementation(gradleKotlinDsl())
  val ver = libs.versions
  implementation("com.diffplug.spotless:spotless-plugin-gradle:${ver.spotlessPlugin.get()}")
  val nessieVer = ver.nessieBuildPlugins.get()
  implementation("org.projectnessie.buildsupport:checkstyle:$nessieVer")
  implementation("org.projectnessie.buildsupport:errorprone:$nessieVer")
  implementation("org.projectnessie.buildsupport:ide-integration:$nessieVer")
  implementation("org.projectnessie.buildsupport:jacoco:$nessieVer")
  implementation("org.projectnessie.buildsupport:jandex:$nessieVer")
  implementation("org.projectnessie.buildsupport:publishing:$nessieVer")
  implementation("org.projectnessie.buildsupport:spotless:$nessieVer")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin { jvmToolchain(11) }