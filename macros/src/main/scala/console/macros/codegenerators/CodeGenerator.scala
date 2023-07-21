package console.macros.codegenerators

import console.macros.model.{Code, EPackage}

trait CodeGenerator:
  def apply(packages: Seq[EPackage]): Seq[Code]
