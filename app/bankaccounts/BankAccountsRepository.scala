package bankaccounts

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import scala.collection.mutable.ListBuffer

import scala.concurrent.Future

final case class BankAccountsData(id: PostId, number: String, name: String, creditcard: Boolean, synthetic: Boolean, balance: Double)


// TODO: Why use a custom data type when you could use int for the id??
class PostId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

// TODO: WHY CONVERT A STRING TO INT AND THEN CREATE AN OBJECT OF IT??
object PostId {
  def apply(raw: String): PostId = {
    require(raw != null)
    new PostId(Integer.parseInt(raw))
  }
}


class BankAccountExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the BankAccountRepository.
  */
trait BankAccountsRepository {
  def create(data: BankAccountsData)(implicit mc: MarkerContext): Future[PostId]

  def list()(implicit mc: MarkerContext): Future[Iterable[BankAccountsData]]

  def get(id: PostId)(implicit mc: MarkerContext): Future[Option[BankAccountsData]]
}

/**
  * A trivial implementation for the BankAccounts Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class BankAccountsRepositoryImpl @Inject()()(implicit ec: BankAccountExecutionContext) extends BankAccountsRepository {

  private val logger = Logger(this.getClass)

  private val postList = List(
    BankAccountsData(PostId("1"), "1357756", "Personal account", false, false, 1202.14),
    BankAccountsData(PostId("2"), "2446987", "Business account", false , false, 34057.00),
    BankAccountsData(PostId("3"), "9981644", "Credit card", true, false, -10057.00),
    BankAccountsData(PostId("4"), "", "Expense claims", false, true, 0),
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[BankAccountsData]] = {
    Future {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: PostId)(implicit mc: MarkerContext): Future[Option[BankAccountsData]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def default()(implicit mc: MarkerContext): Future[Option[BankAccountsData]] = {
    Future {
      logger.trace(s"default: ")
      //postList.find(post => post.id == id)


      /**
        *  Accounts with negative balance can never be chosen as default accounts &
        *  Synthetic bank accounts can never be chosen as default account
        */
      var defaultFilteredList = new ListBuffer[BankAccountsData]
      postList.foreach(post => {
        if (post.balance > 0 && post.synthetic == false) {
          defaultFilteredList += (post)
        }
      })

      /**
        *  If there is only a single bank account, return the id of that account.
        */
       if(defaultFilteredList.length == 1) {
        defaultFilteredList.toList.find(post => post.id == post.id)
       }

       /**
         * If there is a bank account with a positive balance that is at least twice as high as all other bank accounts, return the id of that account.
         */        
       else if (defaultFilteredList.length > 1){
        var defaultPositiveBalanceTwiceAsHighFilteredList = new ListBuffer[BankAccountsData]
        defaultFilteredList.foreach(dflPost => {
          postList.foreach(post => {
            if (dflPost.balance > post.balance*2) {
              defaultPositiveBalanceTwiceAsHighFilteredList += (dflPost)
            }
          })
        })
         if (defaultPositiveBalanceTwiceAsHighFilteredList.length == 1) {
           defaultPositiveBalanceTwiceAsHighFilteredList.toList.find(post => post.id == post.id)
         }
         else {
           null
         }

       }
       else {
        null
       }


    }
  }

  def create(data: BankAccountsData)(implicit mc: MarkerContext): Future[PostId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
