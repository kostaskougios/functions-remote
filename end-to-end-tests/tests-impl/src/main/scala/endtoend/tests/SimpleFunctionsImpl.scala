package endtoend.tests

class SimpleFunctionsImpl extends SimpleFunctions:
  override def noArg(): Int                            = 10
  override def add(a: Int, b: Int): Int                = a + b
  override def multiply(a: Int, b: Int): Int           = a * b
  override def toList(from: Int, upTo: Int): List[Int] = (from to upTo).toList
