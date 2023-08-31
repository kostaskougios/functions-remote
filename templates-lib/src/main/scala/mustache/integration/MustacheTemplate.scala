package mustache.integration

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}
import mustache.integration
import org.simplified.templates.model.TemplatesSourceLocation

import java.io.{StringReader, StringWriter}

class MustacheTemplate(mustache: Mustache):
  def apply(vals: Product): String =
    val w = new StringWriter(8192)
    mustache.execute(w, vals)
    w.toString

object MustacheTemplate:
  private val mf = new DefaultMustacheFactory

  def apply(code: String, name: String): MustacheTemplate =
    new MustacheTemplate(mf.compile(new StringReader(code), name))

  def apply(templatesSourceLocation: TemplatesSourceLocation, className: String): MustacheTemplate =
    apply(templatesSourceLocation.load(className), className)
