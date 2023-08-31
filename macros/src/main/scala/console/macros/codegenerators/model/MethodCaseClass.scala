package console.macros.codegenerators.model

import console.macros.codegenerators.MethodToCaseClassGenerator.NamingConventions
import console.macros.model.{EMethod, EPackage, EType}

// method parameters converted to this so that we can render it
case class MethodCaseClass(imports: Set[String], caseClass: String, paramsDecl: String)

object MethodCaseClass:
  /** @param `package`
    *   the package where the method is declared
    * @param `type`
    *   the type where the method is declared
    * @param method
    *   the method itself to be converted to case class
    * @return
    */
  def toCaseClass(namingConventions: NamingConventions, `package`: EPackage, `type`: EType, method: EMethod): MethodCaseClass =
    val params  = method.toParams
    val n       = namingConventions.methodArgsCaseClassName(`type`, method)
    val imports = `type`.typesInMethods.toSet

    MethodCaseClass(imports, n, params.toMethodDeclArguments)
