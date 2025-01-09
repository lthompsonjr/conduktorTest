import api.ServiceRoutes
import service.{KafkaConsumerServiceLive, PersonService}
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Api extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = Server.serve(ServiceRoutes.people).provide(Server.defaultWithPort(8090), KafkaConsumerServiceLive.layer, PersonService.layer)
}

