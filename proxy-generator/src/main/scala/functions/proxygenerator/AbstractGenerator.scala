package functions.proxygenerator

import functions.model.GeneratorConfig
import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.tastyextractor.StructureExtractor

abstract class AbstractGenerator(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]):
  def generate(targetDir: String, exportDependency: String): Unit =
    val structureExtractor = StructureExtractor()
    val packages           = structureExtractor.forDependency(generatorConfig, exportDependency)
    val codes              = generators.flatMap(_(packages))
    println(s"Will write generated files under $targetDir")
    for c <- codes do
      println(s"Creating ${c.file}")
      c.writeTo(targetDir)
