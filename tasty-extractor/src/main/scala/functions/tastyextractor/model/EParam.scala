package functions.tastyextractor.model

import org.apache.commons.lang3.StringUtils

case class EParam(name: String, `type`: EType, code: String):
  def typeUnqualified = if (`type`.code.contains('.')) StringUtils.substringAfterLast(`type`.code, ".") else `type`.code
