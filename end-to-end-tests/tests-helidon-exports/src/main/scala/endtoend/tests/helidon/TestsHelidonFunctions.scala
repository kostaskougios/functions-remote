package endtoend.tests.helidon

import endtoend.tests.helidon.model.Return1

/** mark as exported: //> exported
  */
trait TestsHelidonFunctions:
  /** Use GET method for this: //> HTTP-GET
    */
  def noArgs(): Int

  /** Use GET method for this: //> HTTP-GET
    */
  def noArgsUnitReturnType(): Unit

  def add(a: Int, b: Int): Int
  def unitResult(a: Int, b: Int): Unit
  def addR(a: Int, b: Int): Return1
  def addLR(a: Int, b: Int): List[Return1]
  def divide(a: Int, b: Int): Either[Int, String]
  def alwaysFails(a: Int): String

  /** Use GET method for this: //> HTTP-GET
    */
  def addParamsEmptySecond(a: Int, l: Long, s: String)(): Int

  /** Use POST method for this: //> HTTP-POST
    */
  def addParams(a: Int, l: Long, s: String)(b: Int): Int
