import mill._, scalalib._, scalajslib._, publish._

import $file.example.compress.build
import $file.example.compress2.build
import $file.example.compress3.build
import $file.example.cookies.build
import $file.example.decorated.build
import $file.example.decorated2.build
import $file.example.endpoints.build
import $file.example.formJsonPost.build
import $file.example.httpMethods.build
import $file.example.minimalApplication.build
import $file.example.minimalApplication2.build
import $file.example.redirectAbort.build
import $file.example.scalatags.build
import $file.example.staticFiles.build
import $file.example.staticFiles2.build
import $file.example.todo.build
import $file.example.todoApi.build
import $file.example.todoDb.build
import $file.example.twirl.build
import $file.example.variableRoutes.build
import $file.example.websockets.build
import $file.example.websockets2.build
import $file.example.websockets3.build
import $file.example.websockets4.build
import $file.ci.upload
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.1.4`
import de.tobiasroeser.mill.vcs.version.VcsVersion

val scala213 = "2.13.8"
val scala212 = "2.12.16"
val scala3 = "3.1.3"
val communityBuildDottyVersion = sys.props.get("dottyVersion").toList

val scalaVersions = scala212 :: scala213 :: scala3 :: communityBuildDottyVersion 

trait CaskModule extends CrossScalaModule with PublishModule{
  def isDotty = crossScalaVersion.startsWith("3")

  def publishVersion = VcsVersion.vcsState().format()

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "com.lihaoyi",
    url = "https://github.com/com-lihaoyi/cask",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("com-lihaoyi", "cask"),
    developers = Seq(
      Developer("lihaoyi", "Li Haoyi","https://github.com/lihaoyi")
    )
  )
}

class CaskMainModule(val crossScalaVersion: String) extends CaskModule {
  def ivyDeps = T{
    Agg(
      ivy"io.undertow:undertow-core:2.2.18.Final",
      ivy"com.lihaoyi::upickle:2.0.0"
    ) ++
    (if(!isDotty) Agg(ivy"org.scala-lang:scala-reflect:${scalaVersion()}") else Agg())
  }
  def compileIvyDeps = T{ if (!isDotty) Agg(ivy"com.lihaoyi::acyclic:0.2.1") else Agg() }
  def scalacOptions = T{ if (!isDotty) Seq("-P:acyclic:force") else Seq() }
  def scalacPluginIvyDeps = T{ if (!isDotty) Agg(ivy"com.lihaoyi::acyclic:0.2.1") else Agg() }

  object test extends Tests with TestModule.Utest {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest::0.8.0",
      ivy"com.lihaoyi::requests::0.7.1"
    )
  }
  def moduleDeps = Seq(cask.util.jvm(crossScalaVersion))
  def artifactName = "cask"
}
object cask extends Cross[CaskMainModule](scalaVersions: _*) {
  object util extends Module {
    trait UtilModule extends CaskModule {
      def artifactName = "cask-util"
      def platformSegment: String
      def millSourcePath = super.millSourcePath / os.up

      def sources = T.sources(
        millSourcePath / "src",
        millSourcePath / s"src-$platformSegment"
      )
      def ivyDeps = Agg(
        ivy"com.lihaoyi::sourcecode:0.2.8",
        ivy"com.lihaoyi::pprint:0.7.3",
        ivy"com.lihaoyi::geny:0.7.1"
      )
    }
    class UtilJvmModule(val crossScalaVersion: String) extends UtilModule {
      def platformSegment = "jvm"
      def ivyDeps = super.ivyDeps() ++ Agg(
        ivy"com.lihaoyi::castor::0.2.1",
        ivy"org.java-websocket:Java-WebSocket:1.5.3"
      )
    }
    object jvm extends Cross[UtilJvmModule](scalaVersions: _*)

    class UtilJsModule(val crossScalaVersion: String) extends UtilModule with ScalaJSModule {
      def platformSegment = "js"
      def scalaJSVersion = "1.10.1"
      def ivyDeps = super.ivyDeps() ++ Agg(
        ivy"com.lihaoyi::castor::0.2.1",
        ivy"org.scala-js::scalajs-dom::2.2.0"
      )
    }
    object js extends Cross[UtilJsModule](scala213)

  }
}

object example extends Module{
  trait LocalModule extends CrossScalaModule{
    override def millSourcePath = super.millSourcePath / "app"
    def moduleDeps = Seq(cask(crossScalaVersion))
  }

  class CompressModule(val crossScalaVersion: String) extends $file.example.compress.build.AppModule with LocalModule
  object compress extends Cross[CompressModule](scalaVersions: _*)

  class Compress2Module(val crossScalaVersion: String) extends $file.example.compress2.build.AppModule with LocalModule
  object compress2 extends Cross[Compress2Module](scalaVersions: _*)

  class Compress3Module(val crossScalaVersion: String) extends $file.example.compress3.build.AppModule with LocalModule
  object compress3 extends Cross[Compress3Module](scalaVersions: _*)

  class CookiesModule(val crossScalaVersion: String) extends $file.example.cookies.build.AppModule with LocalModule
  object cookies extends Cross[CookiesModule](scalaVersions: _*)

  class DecoratedModule(val crossScalaVersion: String) extends $file.example.decorated.build.AppModule with LocalModule
  object decorated extends Cross[DecoratedModule](scalaVersions: _*)

  class Decorated2Module(val crossScalaVersion: String) extends $file.example.decorated2.build.AppModule with LocalModule
  object decorated2 extends Cross[Decorated2Module](scalaVersions: _*)

  class EndpointsModule(val crossScalaVersion: String) extends $file.example.endpoints.build.AppModule with LocalModule
  object endpoints extends Cross[EndpointsModule](scalaVersions: _*)

  class FormJsonPostModule(val crossScalaVersion: String) extends $file.example.formJsonPost.build.AppModule with LocalModule
  object formJsonPost extends Cross[FormJsonPostModule](scalaVersions: _*)

  class HttpMethodsModule(val crossScalaVersion: String) extends $file.example.httpMethods.build.AppModule with LocalModule
  object httpMethods extends Cross[HttpMethodsModule](scalaVersions: _*)

  class MinimalApplicationModule(val crossScalaVersion: String) extends $file.example.minimalApplication.build.AppModule with LocalModule
  object minimalApplication extends Cross[MinimalApplicationModule](scalaVersions: _*)

  class MinimalApplication2Module(val crossScalaVersion: String) extends $file.example.minimalApplication2.build.AppModule with LocalModule
  object minimalApplication2 extends Cross[MinimalApplication2Module](scalaVersions: _*)

  class RedirectAbortModule(val crossScalaVersion: String) extends $file.example.redirectAbort.build.AppModule with LocalModule
  object redirectAbort extends Cross[RedirectAbortModule](scalaVersions: _*)

  // java.lang.NoSuchMethodError: 'void geny.Writable.$init$(geny.Writable)' - geny mismatch, need to upgrade
  class ScalatagsModule(val crossScalaVersion: String) extends $file.example.scalatags.build.AppModule with LocalModule
  object scalatags extends Cross[ScalatagsModule](scala212, scala213)

  class StaticFilesModule(val crossScalaVersion: String) extends $file.example.staticFiles.build.AppModule with LocalModule
  object staticFiles extends Cross[StaticFilesModule](scalaVersions: _*)

  class StaticFiles2Module(val crossScalaVersion: String) extends $file.example.staticFiles2.build.AppModule with LocalModule
  object staticFiles2 extends Cross[StaticFiles2Module](scalaVersions: _*)

  class TodoModule(val crossScalaVersion: String) extends $file.example.todo.build.AppModule with LocalModule
  object todo extends Cross[TodoModule](scala212, scala213) // uses quill, can't enable for Dotty yet

  class TodoApiModule(val crossScalaVersion: String) extends $file.example.todoApi.build.AppModule with LocalModule
  object todoApi extends Cross[TodoApiModule](scalaVersions: _*)

  class TodoDbModule(val crossScalaVersion: String) extends $file.example.todoDb.build.AppModule with LocalModule
  object todoDb extends Cross[TodoDbModule](scala212, scala213) // uses quill, can't enable for Dotty yet

  class TwirlModule(val crossScalaVersion: String) extends $file.example.twirl.build.AppModule with LocalModule
  object twirl extends Cross[TwirlModule](scalaVersions: _*)

  class VariableRoutesModule(val crossScalaVersion: String) extends $file.example.variableRoutes.build.AppModule with LocalModule
  object variableRoutes extends Cross[VariableRoutesModule](scalaVersions: _*)

  class WebsocketsModule(val crossScalaVersion: String) extends $file.example.websockets.build.AppModule with LocalModule
  object websockets extends Cross[WebsocketsModule](scalaVersions: _*)

  class Websockets2Module(val crossScalaVersion: String) extends $file.example.websockets2.build.AppModule with LocalModule
  object websockets2 extends Cross[Websockets2Module](scalaVersions: _*)

  class Websockets3Module(val crossScalaVersion: String) extends $file.example.websockets3.build.AppModule with LocalModule
  object websockets3 extends Cross[Websockets3Module](scalaVersions: _*)

  class Websockets4Module(val crossScalaVersion: String) extends $file.example.websockets4.build.AppModule with LocalModule
  object websockets4 extends Cross[Websockets4Module](scalaVersions: _*)

}



def uploadToGithub() = T.command{
  val vcsState = VcsVersion.vcsState()

  val authKey = T.env.apply("AMMONITE_BOT_AUTH_TOKEN")
  val releaseTag = vcsState.lastTag.getOrElse("")
  val label = vcsState.format()
  if (releaseTag == label){
    requests.post(
      "https://api.github.com/repos/com-lihaoyi/cask/releases",
      data = ujson.write(
        ujson.Obj(
          "tag_name" -> releaseTag,
          "name" -> releaseTag
        )
      ),
      headers = Seq("Authorization" -> s"token $authKey")
    )
  }

  val examples = Seq(
    $file.example.compress.build.millSourcePath,
    $file.example.compress2.build.millSourcePath,
    $file.example.compress3.build.millSourcePath,
    $file.example.cookies.build.millSourcePath,
    $file.example.decorated.build.millSourcePath,
    $file.example.decorated2.build.millSourcePath,
    $file.example.endpoints.build.millSourcePath,
    $file.example.formJsonPost.build.millSourcePath,
    $file.example.httpMethods.build.millSourcePath,
    $file.example.minimalApplication.build.millSourcePath,
    $file.example.minimalApplication2.build.millSourcePath,
    $file.example.redirectAbort.build.millSourcePath,
    $file.example.scalatags.build.millSourcePath,
    $file.example.staticFiles.build.millSourcePath,
    $file.example.staticFiles2.build.millSourcePath,
    $file.example.todo.build.millSourcePath,
    $file.example.todoApi.build.millSourcePath,
    $file.example.todoDb.build.millSourcePath,
    $file.example.twirl.build.millSourcePath,
    $file.example.variableRoutes.build.millSourcePath,
    $file.example.websockets.build.millSourcePath,
    $file.example.websockets2.build.millSourcePath,
    $file.example.websockets3.build.millSourcePath,
    $file.example.websockets4.build.millSourcePath,
  )
  for(example <- examples){
    val f = T.ctx().dest
    val last = example.last + "-" + label
    os.copy(example, f / last)
    os.write.over(
      f / last / "mill",
      os.read(os.pwd / "mill")
    )
    os.proc("chmod", "+x", f/last/"mill").call(f/last)
    os.write.over(
      f / last / "build.sc",
      os.read(f / last / "build.sc")
        .replaceFirst(
          "trait AppModule extends CrossScalaModule\\s*\\{",
          s"object app extends ScalaModule \\{\n  def scalaVersion = \"${scala213}\"")
        .replaceFirst(
          "def ivyDeps = Agg\\[Dep\\]\\(",
          "def ivyDeps = Agg(\n    ivy\"com.lihaoyi::cask:" + releaseTag + "\","
        )
    )

    os.remove.all(f / "out.zip")
    os.proc("zip", "-r", f / "out.zip", last).call(f)
    upload.apply(f / "out.zip", releaseTag, last + ".zip", authKey)
  }
}
