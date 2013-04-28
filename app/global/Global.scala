package scalarss.global

import akka.actor.Props
import play.libs.Akka
import scalarss.dao.UpdaterActor
import play.api.GlobalSettings
import play.api.Application

/**
 * Created with IntelliJ IDEA.
 * User: moot
 * Date: 4/28/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings{
  override def onStart(app: Application){
    implicit val a: Application = app
    val b = Akka.system.actorOf(Props[UpdaterActor])
    UpdaterActor.start(10, b)
  }

}
