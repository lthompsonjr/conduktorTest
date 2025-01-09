import service.{KafkaConsumerService, PersonService}
import zio.http.{Response, Routes}

package object api {

  object ServiceRoutes {
    val people: Routes[KafkaConsumerService with PersonService, Response] = GetPeopleRoute.route
  }

}
