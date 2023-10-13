package functions.tastyextractor.model

import org.apache.commons.lang3.StringUtils

case class EType(name: String, code: String, scalaDocs: Option[String], methods: Seq[EMethod]):
  def asImport: String                      = if code.contains('[') then StringUtils.substringBefore(code, "[") else code
  def importsForTypesInMethods: Seq[String] =
    methods
      .flatMap(m => m.paramss.flatMap(_.map(_.`type`) :+ m.returnType))
      .filterNot(_.isAlwaysImported)
      .map(_.asImport)
      .distinct

  def typeUnqualified = code

  def isAlwaysImported: Boolean = asImport.startsWith("scala.") && !asImport.substring(7).contains('.')

object EType:
  def code(name: String, code: String) = EType(name, code, None, Nil)
