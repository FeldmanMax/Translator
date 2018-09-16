package contollers

import akka.actor.ActorSystem
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.ControllerComponents
import repositories.DBLanguageRepository

trait ControllerMocks {
  private val injector = new GuiceApplicationBuilder().injector()

  val controllerComponents = injector.instanceOf[ControllerComponents]
  val actorSystem = injector.instanceOf[ActorSystem]
  val readLang = injector.instanceOf[DBLanguageRepository]
}
