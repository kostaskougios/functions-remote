package endtoend.tests.kafka

/** Mark it as exported: //> exported
  */
trait KafkaFunctions:
  def add(key: String)(a: Int, b: Int): Unit
