package httpclient

import play.api.http.HttpVerbs._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.Future

abstract class AbstractHttpClient(ws: WSClient) {

  protected def provideAuth: (String, String, WSAuthScheme)

  protected def provideRequestFilters: Seq[WSRequestFilter]

  def get(url: String,
          params: Seq[(String, String)],
          extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    execute(url = url, method = GET, params = params, requestBody = None, extraHeaders = extraHeaders)

  private def execute(url: String,
                      method: String,
                      params: Seq[(String, String)],
                      requestBody: Option[JsValue],
                      extraHeaders: Seq[(String, String)]): Future[WSResponse] = {

    var wsRequest = ws.url(url).withMethod(method)
    params.foreach(p => wsRequest = wsRequest.withQueryStringParameters(p))
    requestBody.foreach(body => wsRequest = wsRequest.withBody(body))
    extraHeaders.foreach(h => wsRequest = wsRequest.addHttpHeaders(h))

    val (username, password, authScheme) = provideAuth
    wsRequest = wsRequest.withAuth(username, password, authScheme)
    provideRequestFilters.foreach(filter => wsRequest = wsRequest.withRequestFilter(filter))
    wsRequest.execute()
  }

}