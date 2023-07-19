package console.macros.model

import org.simplified.templates.model.{Param, Params}

case class EMethod(name: String, paramss: List[List[EParam]], returnType: EType):
  def toParams: Params = Params(paramss.flatten.map(ep => Param(ep.name, ep.typeUnqualified)))
