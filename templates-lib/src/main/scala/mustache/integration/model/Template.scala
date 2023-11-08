package mustache.integration.model

import com.github.mustachejava.TemplateFunction

case class Template(code: String) extends TemplateFunction:
  override def apply(s: String) = code

object Template:
  def apply(templatesSourceLocation: TemplatesSourceLocation, resourceName: String): Template =
    apply(templatesSourceLocation.load(resourceName))
