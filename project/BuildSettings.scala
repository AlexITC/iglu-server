/*
 * Copyright (c) 2019-2022 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and
 * limitations there under.
 */

import sbt._
import Keys._

import sbtassembly.AssemblyPlugin.autoImport._
import com.typesafe.sbt.packager.Keys.maintainer
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

object BuildSettings {

  lazy val dockerSettings = Seq(
    dockerBaseImage := "eclipse-temurin:11-jre-focal",
    Docker / maintainer := "Snowplow Analytics Ltd. <support@snowplowanalytics.com>",
    dockerUpdateLatest := true,
    dockerRepository := Some("snowplow"),
    Docker / daemonUserUid := None,
    Docker / daemonUser := "daemon",
    Docker / defaultLinuxInstallLocation := "/opt/snowplow",
    dockerCmd := Seq("--help")
  )

  lazy val assemblySettings = Seq(
    assembly / assemblyJarName := { s"${name.value}-${version.value}.jar" },
    assembly / assemblyMergeStrategy := {
      case x if x.endsWith("module-info.class") => MergeStrategy.first
      case x if x.endsWith("nowarn.class") => MergeStrategy.first
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
}
