package codegen.proxygenerator.utils

import org.apache.commons.lang3.StringUtils

import scala.io.AnsiColor

object Cleanup:
  private val Colours          = Array(AnsiColor.MAGENTA, AnsiColor.RESET)
  private val Empty            = Array("", "")
  def removeColours(s: String) = StringUtils.replaceEach(s, Colours, Empty)
