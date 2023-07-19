package org.simplified.templates.model

sealed trait TemplatesSourceLocation
case class FileTemplatesSourceLocation(path: String) extends TemplatesSourceLocation
