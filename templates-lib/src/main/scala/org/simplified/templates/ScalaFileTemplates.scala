package org.simplified.templates

import org.apache.commons.lang3.StringUtils
import org.simplified.templates.model.Params

class ScalaFileTemplates:
  private val ForEach                            = "// = foreach "
  private val End                                = "// = end "
  def apply(code: String, vals: Product): String =
    val (search, replace) = searchReplace(vals)
    val lines             = applyToBlock(StringUtils.split(code, '\n').toList, search, replace, vals)
    lines.mkString("\n")

  private def applyToBlock(lines: List[String], search: Array[String], replace: Array[String], vals: Product): List[String] =
    lines match
      case Nil                                   => Nil
      case line :: l if line.startsWith(ForEach) =>
        applyForEach(line, l, search, replace, vals)
      case line :: l                             =>
        StringUtils.replaceEach(line, search, replace) :: applyToBlock(l, search, replace, vals)

  private def applyForEach(forEachLine: String, nextLines: List[String], search: Array[String], replace: Array[String], vals: Product): List[String] =
    val forVal     = StringUtils.substringAfter(forEachLine, ForEach).trim
    val endAt      = End + forVal
    val forBlock   = nextLines.takeWhile(_.trim != endAt)
    val afterBlock = nextLines.dropWhile(_.trim != endAt).drop(1)
    val block      = vals.productElementNames
      .zip(vals.productIterator)
      .find(_._1 == forVal)
      .getOrElse(throw new IllegalArgumentException(s"foreach $forVal : $forVal not found in $vals"))
      ._2 match
      case it: Iterable[Product] @unchecked =>
        it.toList.flatMap: forVals =>
          val (forSearch, forReplace) = searchReplace(forVals)
          applyToBlock(forBlock, forSearch ++ search, forReplace ++ replace, vals)
      case x                                => throw new IllegalStateException(s"foreach $forVal : not an iterable value $x")
    block ++ applyToBlock(afterBlock, search, replace, vals)

  private def searchReplace(vals: Product) =
    val search  = vals.productElementNames.map(k => s"`$k`").toArray
    val replace = vals.productIterator
      .map:
        case p: Params => p.toCode
        case v         => v
      .map(_.toString)
      .toArray
    (search, replace)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
