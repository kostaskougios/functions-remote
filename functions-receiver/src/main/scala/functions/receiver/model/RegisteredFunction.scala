package functions.receiver.model

import scala.reflect.{ClassTag, classTag}

case class RegisteredFunction[A](
    className: String,
    function: A
)

object RegisteredFunction:
  def apply[A: ClassTag](instance: A): RegisteredFunction[A] =
    RegisteredFunction(classTag[A].runtimeClass.getName, instance)
