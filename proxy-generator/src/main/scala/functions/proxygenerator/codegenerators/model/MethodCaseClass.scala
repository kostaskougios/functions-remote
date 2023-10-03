package functions.proxygenerator.codegenerators.model

// method parameters converted to this so that we can render it
case class MethodCaseClass(imports: Set[String], caseClass: String, paramsDecl: String)
