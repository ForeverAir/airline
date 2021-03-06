package controllers

import com.patson.data.UserSource
import com.patson.model.UserStatus
import com.patson.model.UserStatus.UserStatus
import controllers.AuthenticationObject.{Authenticated, AuthenticatedAirline}
import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json.{Json, _}


class AdminApplication @Inject()(cc: ControllerComponents) extends AbstractController(cc) {



  def adminAction(action : String, targetUserId : Int) = Authenticated { implicit request =>
    if (request.user.isAdmin) {
      action match {
        case "ban" =>
          changeUserStatus(UserStatus.BANNED, targetUserId)
          Ok(Json.obj("action" -> action))
        case "ban-chat" =>
          changeUserStatus(UserStatus.CHAT_BANNED, targetUserId)
          Ok(Json.obj("action" -> action))
        case "un-ban" =>
          changeUserStatus(UserStatus.ACTIVE, targetUserId)
          Ok(Json.obj("action" -> action))
        case "switch" =>
          if (request.user.isSuperAdmin) {
            Ok(Json.obj("action" -> action)).withSession("userId" -> String.valueOf(targetUserId))
          } else {
            Forbidden("Not a super admin user")
          }
        case _ =>
          println(s"unknown admin action $action")
          BadRequest(Json.obj("action" -> action))
      }

    } else {
      println(s"Non admin ${request.user} tried to access admin operations!!")
      Forbidden("Not an admin user")
    }
  }

  def changeUserStatus(userStatus: UserStatus, targetUserId: Int) = {
    UserSource.loadUserById(targetUserId) match {
      case Some(user) =>
        val updatingUser = user.copy(status = userStatus)
        UserSource.updateUser(updatingUser)
        println(s"ADMIN - updated user status $userStatus on user $updatingUser")
      case None => println(s"Failed to update user status $userStatus on user id $targetUserId, the user is not found!")
    }
  }

}
