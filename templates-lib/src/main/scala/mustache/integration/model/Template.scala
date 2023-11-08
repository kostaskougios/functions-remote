package mustache.integration.model

import com.github.mustachejava.TemplateFunction

case class Template(name: String, code: String) extends TemplateFunction:
  override def apply(s: String) = code

object Template:
  def apply(name: String, templatesSourceLocation: TemplatesSourceLocation, resourceName: String): Template =
    apply(name, templatesSourceLocation.load(resourceName))
