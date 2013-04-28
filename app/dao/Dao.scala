package scalarss.dao

import scalarss.domain._
import concurrent.Future

trait Dao{
  def addChannel(channel: Channel): Future[Unit]
  def markRead(channel: Channel, items: Seq[String]): Future[Unit]
}

class ChannelNotFoundException(val message: String) extends Exception(message)
