package codegen.proxygenerator.codegenerators.model

import codegen.proxygenerator.codegenerators.GenericTypeGenerator.NamingConventions
import codegen.tastyextractor.model.{EMethod, EPackage, EType}

// method parameters converted to this so that we can render it
case class MethodCaseClass(imports: Set[String], caseClass: String, paramsDecl: String)
