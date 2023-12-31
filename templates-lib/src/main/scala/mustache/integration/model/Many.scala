package mustache.integration.model

import java.util
import scala.jdk.CollectionConverters.IteratorHasAsJava

case class Many[A](it: Iterable[A]) extends java.lang.Iterable[A]:
  override def iterator(): util.Iterator[A] = it.iterator.asJava
  def isEmpty: Boolean                      = !iterator().hasNext

object Many:
  def apply[A](items: A*): Many[A]            = apply(items.toSeq)
  given [A]: Conversion[Iterable[A], Many[A]] = set => Many(set)
