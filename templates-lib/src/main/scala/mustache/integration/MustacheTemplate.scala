package mustache.integration

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}
import mustache.integration
import org.simplified.templates.model.{FileTemplatesSourceLocation, TemplatesSourceLocation}

import java.io.{StringReader, StringWriter}
import scala.io.Source
import scala.util.Using

class MustacheTemplate(mustache: Mustache):
  def apply(vals: Product): String =
    val w = new StringWriter(8192)
    mustache.execute(w, vals)
    w.toString

object MustacheTemplate:
  private val mf = new DefaultMustacheFactory

  def apply(code: String): MustacheTemplate =
    new MustacheTemplate(mf.compile(new StringReader(code), "t"))

  def apply(templatesSourceLocation: TemplatesSourceLocation, className: String): MustacheTemplate =
    templatesSourceLocation match
      case FileTemplatesSourceLocation(path) =>
        Using.resource(Source.fromFile(s"$path/${className.replace('.', '/')}.scala")): in =>
          apply(in.mkString)
