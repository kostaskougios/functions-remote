package functions.kafka

import endtoend.tests.kafka.model.Person
import endtoend.tests.kafka.{KafkaFunctions, KafkaFunctionsCallerFactory, KafkaFunctionsReceiverFactory}
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.*
import scala.util.Using

class KafkaEndToEndSuite extends AnyFunSuite with EmbeddedKafka with BeforeAndAfterAll:

  val props = new Properties
  props.put("bootstrap.servers", "localhost:7101")
  props.put("linger.ms", 1)
  props.setProperty("group.id", "test")
  props.setProperty("enable.auto.commit", "true")
  props.setProperty("auto.commit.interval.ms", "1000")
  props.put("auto.offset.reset", "earliest")

  override protected def afterAll() = EmbeddedKafka.stop()

  override protected def beforeAll() =
    val config = EmbeddedKafkaConfig(kafkaPort = 7101, zooKeeperPort = 7102)
    EmbeddedKafka.start()(config)

  test("kafka") {
    val p1 = Person(1, "p1")
    val p2 = Person(2, "p2")
    Using.resource(KafkaTransport("person", props)): transport =>
      val f = KafkaFunctionsCallerFactory.newAvroKafkaFunctions(transport.transportFunction)
      f.addPerson("person1")(500, p1)
      f.removePerson("person2")(p2)

    val consumer  = new KafkaConsumer(props, new ByteArrayDeserializer, new ByteArrayDeserializer)
    val impl      = new TestKafkaFunctionsImpl
    val invokeMap = KafkaFunctionsReceiverFactory.invokerMap(impl)
    Using.resource(KafkaPoller(consumer, invokeMap)): poller =>
      consumer.subscribe(Seq("person").asJava)
      poller.poll(Duration.ofSeconds(10))

    impl.received should be(
      Set(
        AddPerson("person1", 500, p1),
        RemovePerson("person2", p2)
      )
    )
  }

  case class AddPerson(key: String, ttl: Long, p: Person)
  case class RemovePerson(key: String, p: Person)
  class TestKafkaFunctionsImpl extends KafkaFunctions:
    var received                                                    = Set.empty[Any]
    override def addPerson(key: String)(ttl: Long, p: Person): Unit =
      synchronized:
        received = received + AddPerson(key, ttl, p)

    override def removePerson(key: String)(p: Person): Unit =
      synchronized:
        received = received + RemovePerson(key, p)
