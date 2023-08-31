package org.simplified.templates

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.simplified.templates.model.{FileTemplatesSourceLocation, Imports, Params, TemplatesSourceLocation}

import java.io.File
import scala.io.Source
import scala.util.Using

class ScalaFileTemplate(code: String):
  private val ForEach = "// foreach "
  private val End     = "// end "

  def apply(vals: Product): String =
    val lines = applyToBlock(code.split('\n').toList, vals :: Nil)
    lines.mkString("\n")

  private def applyToBlock(lines: List[String], vals: List[Product]): List[String] =
    lines match
      case Nil                                        => Nil
      case line :: l if line.trim.startsWith(ForEach) =>
        applyForEach(line, l, vals)
      case line :: l                                  =>
        val eVals   = evalVals(vals)
        val newLine = replaceParams(line, eVals.params)
        StringUtils.replaceEach(newLine, eVals.search, eVals.replace) :: applyToBlock(l, vals)

  private def replaceParams(line: String, params: List[(String, Params)]): String =
    params match {
      case Nil         => line
      case (n, p) :: l =>
        line.indexOf(n) match
          case -1 => replaceParams(line, l)
          case i  =>
            val c            = line.indexOf(',', i + 1)
            val par          = line.indexOf(')', i + 1)
            if (par == -1) throw new IllegalStateException(s"There is no closing parenthesis for param args $n at line `$line`")
            val d            = List(c, par).filter(_ > -1).min
            // we need to find out if the params are used when calling a method like f(a,b) or when declaring a method
            // like def f(a:Int,b:Int)
            val collon       = line.indexOf(':', i + 1)
            val isMethodDecl = collon > i && collon < par
            val code         = if isMethodDecl then p.toMethodDeclArguments else p.toMethodCallArguments

            val newLine = line.substring(0, i) + code + line.substring(d)
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

  case class Vals(search: Array[String], replace: Array[String], params: List[(String, Params)])
  private def evalVals(vals: List[Product]): Vals =
    val kvs    = vals.flatMap(_.productElementNames).zip(vals.flatMap(_.productIterator))
    val params = kvs.collect:
      case (k, v: Params) => (s"`$k`", v)

    val normal = kvs.filter:
      case (_, _: Params) => false
      case _              => true

    val search     = normal.map(_._1).map(k => s"`$k`").toArray
    val replace    = normal
      .map:
        case (_, Imports(imports)) => imports.map(i => s"import $i").mkString("\n")
        case (_, v)                => v.toString
      .toArray
    val replSearch = normal.map(_._1).map(k => s"/*=$k*/").toArray
    Vals(search ++ replSearch, replace ++ replace, params)

object ScalaFileTemplate:

  def apply(code: String): ScalaFileTemplate = new ScalaFileTemplate(code)

  def apply(templatesSourceLocation: TemplatesSourceLocation, className: String): ScalaFileTemplate =
    new ScalaFileTemplate(templatesSourceLocation.load(className))
