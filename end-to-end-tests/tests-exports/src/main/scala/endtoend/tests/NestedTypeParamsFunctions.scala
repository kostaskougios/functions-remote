package endtoend.tests

import endtoend.tests.model.{CombinedReturn, Param1, Param2, Return1}

/** End to end tests for non-primitive parameters
  *
  * Mark it as exported: //> exported
  */
trait NestedTypeParamsFunctions:
  def oneParam(p1: Param1): Return1
  def twoParams(p1: Param1, p2: Param2): Return1

  def seqOfP1(p1l: Seq[Param1]): Seq[Return1]
  def seqOfP1P2(p1l: Seq[Param1], p2l: Seq[Param2]): CombinedReturn
