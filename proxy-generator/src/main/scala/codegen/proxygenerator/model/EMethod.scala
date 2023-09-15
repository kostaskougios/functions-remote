package codegen.proxygenerator.model

import mustache.integration.model.{Param, Params}

case class EMethod(name: String, paramss: List[List[EParam]], returnType: EType):
  def toParams: Params = {
    val paramsFlat = paramss.flatten
    val last       = paramsFlat.last
    Params(paramsFlat.map(ep => Param(ep.name, ep.typeUnqualified, ep eq last)))
  }
