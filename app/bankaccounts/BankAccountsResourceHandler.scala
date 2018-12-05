package bankaccounts

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying post information.
  */
case class BankAccountsResource(id: String, link: String, number: String, name: String, creditcard: Boolean, synthetic: Boolean, balance: Double)
//case class PostResource(id: String, link: String, title: String, body: String)

object BankAccountsResource {

  /**
    * Mapping to write a BankAccountsResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[BankAccountsResource] {
    def writes(post: BankAccountsResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "number" -> post.number,
        "name" -> post.name,
        "creditcard" -> post.creditcard,
        "synthetic" -> post.synthetic,
        "balance" -> post.balance
      )
    }
  }
}

//  implicit val implicitWrites = new Writes[PostResource] {
//    def writes(post: PostResource): JsValue = {
//      Json.obj(
//        "id" -> post.id,
//        "link" -> post.link,
//        "title" -> post.title,
//        "body" -> post.body
//      )
//    }
//  }
//}

/**
  * Controls access to the backend data, returning [[BankAccountsResource]]
  */
class BankAccountsResourceHandler @Inject()(
                                             routerProvider: Provider[BankAccountsRouter],
                                             bankAccountsRepository: BankAccountsRepository)(implicit ec: ExecutionContext) {

  def create(bankAccountsFormInput: BankAccountsFormInput)(implicit mc: MarkerContext): Future[BankAccountsResource] = {
    val data = BankAccountsData(PostId("999"), bankAccountsFormInput.number, bankAccountsFormInput.name,
      bankAccountsFormInput.creditcard, bankAccountsFormInput.synthetic, bankAccountsFormInput.balance)
    // We don't actually create the post, so return what we have
    bankAccountsRepository.create(data).map { id =>
      createPostResource(data)
    }
  }

  def lookup(id: String)(implicit mc: MarkerContext): Future[Option[BankAccountsResource]] = {
    val postFuture = bankAccountsRepository.get(PostId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createPostResource(postData)
      }
    }
  }

  def default()(implicit mc: MarkerContext): Future[Option[BankAccountsResource]] = {
    val postFuture = bankAccountsRepository.get(PostId("1"))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createPostResource(postData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[BankAccountsResource]] = {
    bankAccountsRepository.list().map { postDataList =>
      postDataList.map(postData => createPostResource(postData))
    }
  }

//  private def createPostResource(p: BankAccountsData): PostResource = {
//    PostResource(p.id.toString, routerProvider.get.link(p.id), p.number, p.name, p.creditcard, p.synthetic, p.balance)
//  }

  private def createPostResource(p: BankAccountsData): BankAccountsResource = {
    BankAccountsResource(p.id.toString, routerProvider.get.link(p.id), p.number, p.name, p.creditcard, p.synthetic, p.balance)
  }

}

