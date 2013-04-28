package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._

import scalarss.domain._
import scalarss.service._

import concurrent.ExecutionContext

import scalarss.dao._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  implicit val writeItem = Json.writes[Item]
  implicit val writeChannel = ChannelImplicit.writes
  implicit val readItem = Json.reads[Item]
  implicit val readChannel = Json.reads[Channel]
  def channel(id: String) = Action { implicit request =>
    import ExecutionContext.Implicits.global
    Async {
      STMDao.getItems(id).map( l => Ok(Json.toJson(l)) ).recover{ case _ => NotFound(f"'$id' not found") }
    }
  }

  def newChannel = Action(parse.json) { implicit request =>
    import ExecutionContext.Implicits.global
    request.body.validate[Channel] match {
      case JsSuccess(c, _) =>
        STMDao.addChannel(c)
        Ok
      case JsError(e) => BadRequest(JsError.toFlatJson(e))
    }
  }

  def listChannels = Action { implicit request =>
    Ok(Json.toJson(STMDao.listChannels))
  }

}
