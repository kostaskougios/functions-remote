package endtoend.tests

import endtoend.tests.model.{Param1, Param2, Return1}

/** End to end tests for non-primitive parameters
  *
  * Mark it as exported: //> exported
  */
trait NestedTypeParamsFunctions:
  def oneParam(p1: Param1): Return1
  def twoParams(p1: Param1, p2: Param2): Return1
