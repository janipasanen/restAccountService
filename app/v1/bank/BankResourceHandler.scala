package v1.bank

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying post information.
  */
case class PostResource(id: String, link: String, title: String, body: String)

object PostResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[PostResource] {
    def writes(post: PostResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "link" -> post.link,
        "title" -> post.title,
        "body" -> post.body
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[PostResource]]
  */
class PostResourceHandler @Inject()(
                                     routerProvider: Provider[BankRouter],
                                     postRepository: BankRepository)(implicit ec: ExecutionContext) {

  def create(postInput: PostFormInput)(implicit mc: MarkerContext): Future[PostResource] = {
    val data = PostData2(PostId("999"), postInput.title, postInput.body)
    // We don't actually create the post, so return what we have
    postRepository.create(data).map { id =>
      createPostResource(data)
    }
  }

  def lookup(id: String)(implicit mc: MarkerContext): Future[Option[PostResource]] = {
    val postFuture = postRepository.get(PostId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createPostResource(postData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[PostResource]] = {
    postRepository.list().map { postDataList =>
      postDataList.map(postData => createPostResource(postData))
    }
  }

  private def createPostResource(p: PostData2): PostResource = {
    PostResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }

}
