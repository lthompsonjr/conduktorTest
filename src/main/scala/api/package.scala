import service.KafkaConsumerService
import service.PersonService
import zio.http.Response
import zio.http.Routes

package object api {

  object ServiceRoutes {
    val people: Routes[KafkaConsumerService with PersonService, Response] =
      GetPeopleRoute.route
  }

}
