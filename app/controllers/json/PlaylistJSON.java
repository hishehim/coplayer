package controllers.json;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import controllers.Application;
import models.Playlist;
import models.Users;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


/**
 * Created by linmh on 4/20/2016.
 */
public class PlaylistJSON extends Controller {

    public Result getPlaylist(String uid) {

        Playlist playlist = Playlist.find.where().eq("uid", uid).findUnique();

        if (playlist == null) {
            JSONObject emptyResult = new JSONObject();
            try {
                emptyResult.put("error", "does not exist");
            } catch (JSONException ex) {
                Logger.error("Error with JSONObject.put(constants)", ex);
            }
            return ok(Json.toJson(emptyResult));
        }
        return ok(Json.toJson(playlist));
    }

    /**
     * Query and find all playlist that are public and returns json
     * */
    public Result getPublicPlaylist() {
        /* Manual query required */
        String sql = "select u.username as owner, u.id as ownerid, p.title, p.uid as id, p.size from playlist p " +
                " inner join users u on u.id = p.owner_id " +
                " where p.is_private = :is_private";
        List<SqlRow> sqlResult = Ebean.createSqlQuery(sql)
                .setParameter("is_private", false)
                .findList();
        return ok(Json.toJson(sqlResult));
    }

    public Result getUsrPublicList(String userID) {
        long ownerId;
        if (userID == null) {
            ownerId = Application.getSessionUsrId();
            if (ownerId < 0) {
                return badRequest();
            }
        } else {
            try {
                ownerId = Long.parseLong(userID);
            } catch (NumberFormatException e) {
                return badRequest();
            }
        }
        if (Users.find.byId(ownerId) == null) {
            return notFound();
        }
        return ok(Json.toJson(getList(ownerId, false)));
    }


    public Result getUsrPrivateList() {
        long id = Application.getSessionUsrId();
        if (id < 0) {
            return forbidden();
        }
        return ok(Json.toJson(getList(id, true)));
    }

    private List<SqlRow> getList(long userID, boolean isPrivate) {
        String sql = "select p.title, p.uid as id, p.size from playlist p " +
                " where p.is_private = :is_private and p.owner_id = :owner_id";
        return Ebean.createSqlQuery(sql)
                .setParameter("is_private", isPrivate)
                .setParameter("owner_id", userID)
                .findList();
    }
}