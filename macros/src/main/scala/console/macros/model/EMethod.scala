package console.macros.model

import mustache.integration.model.{Param, Params}

case class EMethod(name: String, paramss: List[List[EParam]], returnType: EType):
  def toParams: Params = Params(paramss.flatten.map(ep => Param(ep.name, ep.typeUnqualified)))
