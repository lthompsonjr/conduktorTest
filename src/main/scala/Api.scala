import api.ServiceRoutes
import service.KafkaConsumerServiceLive
import service.PersonService
import zio.Scope
import zio.ZIO
import zio.ZIOAppArgs
import zio.ZIOAppDefault
import zio.http.Server

object Api extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = Server.serve(ServiceRoutes.people).provide(Server.defaultWithPort(8090), KafkaConsumerServiceLive.layer, PersonService.layer)
}
