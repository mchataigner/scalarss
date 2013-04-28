package scalarss.dao

import scala.concurrent.stm._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scalarss.domain._
import scalarss.service.Crawler
import akka.actor.{ActorRef, Actor}
import play.libs.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Application

object STMDao extends Dao{
  val channels = Ref[Map[Channel, List[Item]]](Map.empty)

  def addChannel(channel: Channel): Future[Unit] = {
    atomic { implicit txn =>
      channels() = channels() + (channel -> List.empty)
      //updateItems
    }
    Future.successful()
  }

  def getItems(channelId: String): Future[List[Item]] = {
    val c = channels.single().keys.find( i => i.title == channelId )
    c match {
      case Some(channel) => Future.successful(channels.single()(channel))
      case None => Future.failed(new ChannelNotFoundException(channelId))// List.empty
    }
  }

  def markRead(channel: Channel, items: Seq[String]): Future[Unit] =
    Future.successful()

  def listChannels = channels.single().keys

  def updateItems: Future[Unit] = {
    import concurrent.ExecutionContext.Implicits.global
    atomic { implicit txn =>
      val newchannels = channels().keys.map( c => {
        val items = Crawler.fetchItems(c.origin).map( _.map(
        i => {
            Item(
            (i \ "guid").text,
            (i \ "description").text,
            (i \ "title").text,
              (i \ "link").text,
              (i \ "pubDate").text,
                (i \ "encoded").text
            )
          }
        ))
        items.map( l => (c -> l))
      })
      channels() = Await.result(Future.sequence(newchannels).map( _.toMap ), 1.second)
    }
    Future.successful()
  }

}

class UpdaterActor extends Actor{
  val dao = STMDao
  def receive = {
    case (n: String) if n == "update" => {
      dao.updateItems
    }
  }
}

object UpdaterActor {
  def start(time: Long, beater: ActorRef)(implicit app: Application) = {
    Akka.system.scheduler.schedule(0 milliseconds, time seconds,beater,"update")
  }
}
