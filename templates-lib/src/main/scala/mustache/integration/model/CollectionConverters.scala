package mustache.integration.model

import com.github.mustachejava.util.DecoratedCollection

import scala.jdk.CollectionConverters.*

object CollectionConverters:
  given [A]: Conversion[Seq[A], DecoratedCollection[A]] = (l: Seq[A]) => new DecoratedCollection(l.asJava)
