package mustache.integration

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}
import mustache.integration
import mustache.integration.model.{Template, TemplatesSourceLocation}

import java.io.{StringReader, StringWriter}
import scala.jdk.CollectionConverters.*

class MustacheTemplate(val template: String, mustache: Mustache, templates: Seq[Template]):
  def apply(vals: Product): String =
    val w  = new StringWriter(8192)
    val tm = templates.map(t => (t.name, t)).toMap.asJava
    mustache.execute(w, List(vals, tm).asJava)
    w.toString

object MustacheTemplate:
  private val mf = new DefaultMustacheFactory

  def inlined(code: String, name: String, templates: Seq[Template] = Nil): MustacheTemplate =
    new MustacheTemplate(name, mf.compile(new StringReader(code), name), templates)

  def apply(templatesSourceLocation: TemplatesSourceLocation, resourceName: String, templates: Seq[Template] = Nil): MustacheTemplate =
    inlined(templatesSourceLocation.load(resourceName), resourceName, templates)
