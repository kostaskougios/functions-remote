package endtoend.tests
import endtoend.tests.model.{Param1, Param2, Return1}

class NestedTypeParamsFunctionsImpl extends NestedTypeParamsFunctions:
  override def oneParam(p1: Param1): Return1 = Return1(p1.a)

  override def twoParams(p1: Param1, p2: Param2): Return1 = Return1(p2.x.toInt)
