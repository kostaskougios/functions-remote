package functions.tastyextractor.model

import org.apache.commons.lang3.StringUtils

case class EParam(name: String, `type`: String, code: String):
  def typeUnqualified = if (`type`.contains('.')) StringUtils.substringAfterLast(`type`, ".") else `type`
