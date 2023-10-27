package endtoend.tests

/** End to end tests for very simple functions
  *
  * Mark it as exported: //> exported
  */
trait SimpleFunctions:
  def noArg(): Int
  def add(a: Int, b: Int): Int
  def multiply(a: Int, b: Int): Int
  def toList(from: Int, to: Int): List[Int]
  def listParam(from: List[Int]): Int
  def failure(i: Int): Int
