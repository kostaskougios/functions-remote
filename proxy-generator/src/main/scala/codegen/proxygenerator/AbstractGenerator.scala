package codegen.proxygenerator

import codegen.model.GeneratorConfig
import codegen.proxygenerator.codegenerators.GenericTypeGenerator
import codegen.tastyextractor.StructureExtractor

abstract class AbstractGenerator(generatorConfig: GeneratorConfig, generators: Seq[GenericTypeGenerator]):
  def generate(targetDir: String, exportDependency: String): Unit =
    val structureExtractor = StructureExtractor()
    val packages           = structureExtractor.forDependency(generatorConfig, exportDependency)
    val codes              = generators.flatMap(_(packages))
    for c <- codes do c.writeTo(targetDir)
