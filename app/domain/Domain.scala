package scalarss.domain

import play.api.libs.json.{JsValue, Json, Writes}

case class Item(val guid: String, val description: String, val title: String, val origin: String, val pubDate: String, val content: String = "")

case class Channel(val title: String, val origin: String)

object ChannelImplicit{
  implicit val writes: Writes[Channel] = new Writes[Channel] {
    def writes(c: Channel): JsValue = {
      Json.obj("title" -> c.title, "origin" -> c.origin, "link" -> ("http://localhost:9000/channel/"+c.title))
    }
  }
}
