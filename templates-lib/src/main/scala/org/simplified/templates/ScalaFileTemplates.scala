package org.simplified.templates

import org.apache.commons.lang3.StringUtils

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
        val forVal     = StringUtils.substringAfter(line.trim, ForEach).trim
        val forBlock   = l.takeWhile(_.trim != ForEach + forVal).dropRight(1)
        val afterBlock = l.dropWhile(_.trim != End + forVal).drop(1)
        vals.productElementNames
          .zip(vals.productIterator)
          .find(_._1 == forVal)
          .getOrElse(throw new IllegalArgumentException(s"foreach $forVal : $forVal not found in $vals"))
          ._2 match
          case it: Iterable[Product] =>
            it.toList.flatMap: forVals =>
              val (forSearch, forReplace) = searchReplace(forVals)
              applyToBlock(forBlock, search ++ forSearch, replace ++ forReplace, vals)
          case x                     => throw new IllegalStateException(s"foreach $forVal : not an iterable value $x")

      case line :: l =>
        StringUtils.replaceEach(line, search, replace) :: applyToBlock(l, search, replace, vals)

  private def searchReplace(vals: Product) =
    val search  = vals.productElementNames.map(k => s"`$k`").toArray
    val replace = vals.productIterator.map(_.toString).toArray
    (search, replace)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
