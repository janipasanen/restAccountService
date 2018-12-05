package bankaccounts

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._


/**
  * Routes and URLs to the BankaccountsResource controller.
  */
class BankAccountsRouter @Inject()(controller: BankAccountsController) extends SimpleRouter {
  val prefix = "/bankaccounts"

  def link(id: PostId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case GET(p"/default") =>
      controller.default

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)
  }

}
