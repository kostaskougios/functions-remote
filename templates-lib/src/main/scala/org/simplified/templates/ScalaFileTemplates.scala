package org.simplified.templates

import org.apache.commons.lang3.StringUtils
import org.simplified.templates.model.Params

class ScalaFileTemplates:
  private val ForEach                            = "// = foreach "
  private val End                                = "// = end "
  def apply(code: String, vals: Product): String =
    val lines = applyToBlock(StringUtils.split(code, '\n').toList, vals :: Nil)
    lines.mkString("\n")

  private def applyToBlock(lines: List[String], vals: List[Product]): List[String] =
    lines match
      case Nil                                   => Nil
      case line :: l if line.startsWith(ForEach) =>
        applyForEach(line, l, vals)
      case line :: l                             =>
        val (search, replace) = searchReplace(vals)
        StringUtils.replaceEach(line, search, replace) :: applyToBlock(l, vals)

  private def applyForEach(forEachLine: String, nextLines: List[String], vals: List[Product]): List[String] =
    val forVal     = StringUtils.substringAfter(forEachLine, ForEach).trim
    val endAt      = End + forVal
    val forBlock   = nextLines.takeWhile(_.trim != endAt)
    val afterBlock = nextLines.dropWhile(_.trim != endAt).drop(1)
    val currVals   = vals.head
    val block      = currVals.productElementNames
      .zip(currVals.productIterator)
      .find(_._1 == forVal)
      .getOrElse(throw new IllegalArgumentException(s"foreach $forVal : $forVal not found in $vals"))
      ._2 match
      case it: Iterable[Product] @unchecked =>
        it.toList.flatMap: forVals =>
          applyToBlock(forBlock, forVals :: vals)
      case x                                => throw new IllegalStateException(s"foreach $forVal : not an iterable value $x")
    block ++ applyToBlock(afterBlock, vals)

  private def searchReplace(vals: List[Product]) =
    val search  = vals.flatMap(_.productElementNames).map(k => s"`$k`").toArray
    val replace = vals
      .flatMap(_.productIterator)
      .map:
        case p: Params => p.toCode
        case v         => v
      .map(_.toString)
      .toArray
    (search, replace)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
