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
        val (search, replace, params) = searchReplace(vals)
        val newLine                   = replaceParams(line, params)
        StringUtils.replaceEach(newLine, search, replace) :: applyToBlock(l, vals)

  def replaceParams(line: String, params: List[(String, Params)]): String =
    params match {
      case Nil         => line
      case (n, p) :: l =>
        line.indexOf(n) match
          case -1 => replaceParams(line, l)
          case i  =>
            val c       = line.indexOf(',', i + 1)
            val par     = line.indexOf(')', i + 1)
            if (par == -1) throw new IllegalStateException(s"There is no closing parenthesis for param args $n at line `$line`")
            val d       = List(c, par).filter(_ > -1).min
            val newLine = line.substring(0, i) + s"${p.toCode}" + line.substring(d)
            replaceParams(newLine, params)
    }

  private def applyForEach(forEachLine: String, nextLines: List[String], vals: List[Product]): List[String] =
    // find the foreach variable name
    val forVal     = StringUtils.substringAfter(forEachLine, ForEach).trim
    val endAt      = End + forVal
    // find the block that the foreach apply
    val forBlock   = nextLines.takeWhile(_.trim != endAt)
    // find the rest of the lines after the foreach block
    val afterBlock = nextLines.dropWhile(_.trim != endAt).drop(1)
    val currVals   = vals.head
    // find the vals for the foreach var name
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

  private def searchReplace(vals: List[Product]): (Array[String], Array[String], List[(String, Params)]) =
    val kvs    = vals.flatMap(_.productElementNames.map(k => s"`$k`")).zip(vals.flatMap(_.productIterator))
    val params = kvs.collect:
      case (k, v: Params) => (k, v)

    val normal = kvs.filter:
      case (_, _: Params) => false
      case _              => true

    val search  = normal.map(_._1).toArray
    val replace = normal.map(_._2.toString).toArray
    (search, replace, params)

object ScalaFileTemplates:
  def apply() = new ScalaFileTemplates
