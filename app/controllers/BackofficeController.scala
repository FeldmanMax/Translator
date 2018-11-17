package controllers

import akka.actor.ActorSystem
import javax.inject._
import model.db.{AddLanguage, DeleteLanguage, GetAllLanguages}
import play.api.i18n.Lang
import play.api.mvc._
import repositories.LanguageRepository
import scala.concurrent.ExecutionContext

@Singleton
class BackofficeController @Inject()(
  cc: ControllerComponents,
  actorSystem: ActorSystem,
  langRepository: LanguageRepository
)(implicit exec: ExecutionContext) extends PlayControllerHelper(cc) {

  def addLang(code: String): Action[AnyContent] = async (AddLanguage, _ =>
    for {
      addResult <- langRepository.insert(Lang(code))
    } yield {
      resulting[Lang](addResult, result => if(result.code == code)    Created  else  InternalServerError("Not Added"))
    }
  )

  def deleteLang(code: String): Action[AnyContent] = async (DeleteLanguage, _ =>
    for {
      deleteResult <- langRepository.delete(Lang(code))
    } yield {
      resulting[Lang](deleteResult, result => if(result.code == code) Ok      else  InternalServerError("Not deleted"))
    }
  )

  def getAll: Action[AnyContent] = async (GetAllLanguages, _ =>
    for {
      list <- langRepository.getAll
    } yield {
      resulting[Vector[Lang]](list, languages => Ok(views.html.language.langView(languages)))
    }
  )
}
