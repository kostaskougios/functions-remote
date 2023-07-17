package console.macros.model

case class EMethod(name: String, paramss: List[List[EParam]], returnType: EType):
  /** @return
    *   params as code i.e. "(path : String, lsOptions : ls.model.LsOptions)"
    */
  def paramsCode: String = paramss.map(_.map(_.code)).map(_.mkString(", ")).mkString("(", "", ")")

  /** @return
    *   params as code with types unqualified, i.e. "(path:String, lsOptions:LsOptions)"
    */
  def paramsCodeUnqualified: String = paramss.map(_.map(p => p.name + ":" + p.typeUnqualified)).map(_.mkString(", ")).mkString("(", "", ")")

  /** @return
    *   the params as args i.e. "(path, lsOptions)"
    */
  def paramsAsArgs: String = paramss.map(_.map(_.name).mkString(", ")).mkString("(", "", ")")
