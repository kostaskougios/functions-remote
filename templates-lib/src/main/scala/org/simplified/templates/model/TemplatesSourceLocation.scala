package org.simplified.templates.model

import scala.io.Source
import scala.util.Using

sealed trait TemplatesSourceLocation:
  def load(name: String): String

case class FileTemplatesSourceLocation(path: String) extends TemplatesSourceLocation:
  override def load(name: String): String =
    Using.resource(Source.fromFile(s"$path/${name.replace('.', '/')}.scala")): in =>
      in.mkString

case object ResourceTemplatesSourceLocation extends TemplatesSourceLocation:
  override def load(name: String): String =
    Using.resource(Source.fromResource(s"${name.replace('.', '/')}.scala")): in =>
      in.mkString
