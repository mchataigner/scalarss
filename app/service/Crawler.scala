package scalarss.service

import play.api.libs.ws._
import concurrent.Future
import xml._

object Crawler{
  def fetchItems(link: String): Future[List[Node]] = {
    val r = WS.url(link).get
    import concurrent.ExecutionContext.Implicits.global
    r.flatMap(
      resp => {
        if(resp.status == 200) Future.successful( (resp.xml \ "channel" \ "item").toList) else Future.successful(List.empty)
      }
    )
  }
}
