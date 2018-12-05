package v1.bank

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class PostData2(id: PostId, title: String, body: String)

class PostId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object PostId {
  def apply(raw: String): PostId = {
    require(raw != null)
    new PostId(Integer.parseInt(raw))
  }
}


class BankExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait BankRepository {
  def create(data: PostData2)(implicit mc: MarkerContext): Future[PostId]

  def list()(implicit mc: MarkerContext): Future[Iterable[PostData2]]

  def get(id: PostId)(implicit mc: MarkerContext): Future[Option[PostData2]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class BankRepositoryImpl @Inject()()(implicit ec: BankExecutionContext) extends BankRepository {

  private val logger = Logger(this.getClass)

  private val postList = List(
    PostData2(PostId("1"), "title 3_1", "blog post 3_1"),
    PostData2(PostId("2"), "title 3_2", "blog post 3_2"),
    PostData2(PostId("3"), "title 3_3", "blog post 3_3"),
    PostData2(PostId("4"), "title 3_4", "blog post 3_4"),
    PostData2(PostId("5"), "title 3_5", "blog post 3_5")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[PostData2]] = {
    Future {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: PostId)(implicit mc: MarkerContext): Future[Option[PostData2]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def create(data: PostData2)(implicit mc: MarkerContext): Future[PostId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
