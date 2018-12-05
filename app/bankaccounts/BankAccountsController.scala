package bankaccounts

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class BankAccountsFormInput(number: String, name: String, creditcard: Boolean, synthetic: Boolean, balance: Double)

/**
  * Takes HTTP requests and produces JSON.
  */

class BankAccountsController @Inject()(cc: PostControllerComponents)(implicit ec: ExecutionContext)
  extends PostBaseController(cc) {


  private val logger = Logger(getClass)

  private val form: Form[BankAccountsFormInput] = {
    import play.api.data.Forms._
    import play.api.data.format.Formats._

    Form(
      mapping(
        "number" -> nonEmptyText,
        "name" -> text,
        "creditcard" -> boolean,
        "synthetic" -> boolean,
        "balance" -> of(doubleFormat)
      )(BankAccountsFormInput.apply)(BankAccountsFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("index: ")
    bankAccountsResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def default: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("default: ")
    bankAccountsResourceHandler.default.map { post =>
      Ok(Json.toJson(post))
    }
  }

  def process: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("process: ")
    processJsonPost()
  }

  def show(id: String): Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace(s"show: id = $id")
    bankAccountsResourceHandler.lookup(id).map { post =>
      Ok(Json.toJson(post))
    }
  }

  private def processJsonPost[A]()(implicit request: PostRequest[A]): Future[Result] = {
    def failure(badForm: Form[BankAccountsFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: BankAccountsFormInput) = {
      bankAccountsResourceHandler.create(input).map { post =>
        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
