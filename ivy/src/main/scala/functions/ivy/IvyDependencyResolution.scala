package functions.ivy

import java.io.File
import java.net.URL
import org.apache.ivy.Ivy
import org.apache.ivy.core.module.descriptor.{DefaultDependencyDescriptor, DefaultModuleDescriptor, ModuleDescriptor}
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.{FileSystemResolver, IBiblioResolver}

class IvyDependencyResolution:
  private val ivyHome        = sys.props("user.home") + "/.ivy2"
  private val Pattern        = "[organization]/[module]/[revision]/[artifact]-[revision].[ext]"
  private def createResolver =
    val resolver = new IBiblioResolver
    resolver.setM2compatible(true)
    resolver.setRoot("https://repo1.maven.org/maven2/")
    resolver.setName("central")
    resolver

  private def createLocalResolver =
    val localResolver = new FileSystemResolver
    localResolver.setName("local")
    localResolver.addArtifactPattern(s"$ivyHome/local/$Pattern")
    localResolver

  private def prepareIvySettings =
    val ivySettings = new IvySettings
    ivySettings.addResolver(createResolver)
    ivySettings.addResolver(createLocalResolver)
    ivySettings.setDefaultResolver("local")
    ivySettings

  def detectJar(group: String, artifact: String, version: String) =
    val ivy = Ivy.newInstance(prepareIvySettings)

    val dependencyId   = ModuleRevisionId.newInstance(group, artifact, version)
    val md             = DefaultModuleDescriptor.newDefaultInstance(dependencyId)
    val resolveOptions = new ResolveOptions().setConfs(Array("default"))
    md.addDependency(new DefaultDependencyDescriptor(md, dependencyId, false, false, true))

    val report = ivy.resolve(md, resolveOptions)

    if report.hasError then throw new RuntimeException("Dependency resolution failed")

    val retrieveReport = ivy.retrieve(
      report.getModuleDescriptor.getModuleRevisionId,
      new RetrieveOptions().setDestArtifactPattern(s"$ivyHome/cache/$Pattern").setConfs(Array("default"))
    )

@main def tryIvy() =
  val ivy = new IvyDependencyResolution
  ivy.detectJar("org.functions-remote", "proxy-generator_3", "0.1-SNAPSHOT")
