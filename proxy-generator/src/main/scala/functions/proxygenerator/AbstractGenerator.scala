package functions.proxygenerator

import functions.model.GeneratorConfig
import functions.proxygenerator.codegenerators.GenericTypeGenerator
import functions.tastyextractor.StructureExtractor

abstract class AbstractGenerator(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]):
  def generate(targetDir: String, exportDependency: String): Unit =
    val structureExtractor = StructureExtractor()
    val packages           = structureExtractor.forDependency(generatorConfig, exportDependency)
    if packages.isEmpty then throw new IllegalStateException("No exported trait found, did you marked it with //> exported ?")
    val codes              = generators.flatMap(_(packages))
    println(s"Will write generated files under $targetDir")
    for c <- codes do
      println(s"Creating ${c.file}")
      c.writeTo(targetDir)
