package paper.yarn.ink;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.api.datastore.Query.FilterPredicate;


@SuppressWarnings("serial")
public class GiftidServlet extends HttpServlet {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static GiftRecord giftRecord = new GiftRecord();
  private static UserRecord userRecord = new UserRecord();
  
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
    resp.setContentType("text/plain");
    System.out.println("Looking up "+extractKeyString(req));
		Entity entity = userRecord.findUserRecord(extractKeyString(req));
		PrintWriter out = resp.getWriter();
		if (entity != null) {
		  System.out.println("FOUND entity");
  		JSONObject obj = userRecord.convertToJsonObj(entity);
  		out.print(obj);
		} else {
		  System.out.println("NOTHING found");
		  out.print("ERROR -- I got null");
		}
    out.close();
	}
	@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws 
  ServletException, 
  IOException {
	  System.out.println("Looking up '"+extractKeyString(req)+"'");
    Entity userEntity = userRecord.findUserRecord(extractKeyString(req));
    if (userEntity == null) {
      Transaction txn = datastore.beginTransaction();
      System.out.println("No entity found-- making a new one");
      userEntity = userRecord.pushDataIntoEntity(req);
      datastore.put(userEntity);
      txn.commit();
    } 
    System.out.println("Creating new gift entry");
    Transaction txn = datastore.beginTransaction();
    Entity giftEntity = giftRecord.makeNewEntity(userEntity, req);//update existing entity
    System.out.println("Saving endity back to DB");
    datastore.put(giftEntity);    // save entity back
    txn.commit();
    PrintWriter out = resp.getWriter();
    JSONObject obj = userRecord.convertToJsonObj(userEntity);
    out.print(obj);
    out.close();
	}
	public String extractKeyString(HttpServletRequest req){
	  String path = req.getPathInfo();
	  if (path != null) 
     return path.substring(1);
	  return "";
	}
	static class UserRecord extends GiftRecord { 
    public static final List<String> fields =  Arrays.asList(new String[]{
        "user1", 
        "gotget",
        "gift",
        "forfrom",
        "user2"});
    public List<String> getFields() {
      return fields;
    }
    public String getType() {
      return "user1";
    }
    public Entity pushDataIntoEntity(HttpServletRequest req){
      Entity entity = new Entity(getType());
      //entity.setProperty("owner", req.getParameter("owner"));
      //entity.setProperty("key", "monkey");
      return entity;
    }
    public JSONObject convertToJsonObj(Entity record) {
      JSONObject obj = super.convertToJsonObj(record);
      Query q = new Query(giftRecord.getType()).setFilter(new FilterPredicate(
          "owner",
          Query.FilterOperator.EQUAL,
          record.getKey().getId()));
      PreparedQuery pq = datastore.prepare(q);
      JSONArray array = new JSONArray();
      System.out.println("Convert To Json Obj");
      for(Entity e : pq.asList(FetchOptions.Builder.withLimit(50))){
        System.out.println(">> Entity found : "+e.getKey().getId() );
        JSONObject g = giftRecord.convertToJsonObj(e);
        array.put(g);
      }
      try {
        obj.put("gifts", array);
        obj.put("key", record.getKey().getId());
      } catch (JSONException e1) { }
      return obj;
    }
	}
	static class GiftRecord {
    public static final List<String> fields = Arrays.asList(new String[]{
        "owner",
        "user1", 
        "gotget",
        "gift",
        "forfrom",
        "user2"});
    public List<String> getFields() {
      return fields;
    }
    public Entity makeNewEntity(Entity entity, HttpServletRequest req) {
      Entity newE = new Entity(getType());
      for(String s : getFields()){ 
        newE.setProperty(s, req.getParameter(s));
      }
      newE.setProperty("owner", entity.getKey().getId());
      return newE;
    }
    public String getType() {
      return "giftid2";
    }
    public Key makeKey(String keyString) {
      if (keyString == null || keyString.length() == 0) {
        return null;
      }
      return KeyFactory.createKey(getType(), Long.valueOf(keyString));
    }
    public JSONObject convertToJsonObj(Entity record) {
      JSONObject obj = new JSONObject();
      try {
        obj.put("key", record.getKey().getId());
        for(String s : getFields()){ 
          obj.put(s, record.getProperty(s));
        }
      } catch (JSONException e) {}
      return obj;
    }
    public Entity findUserRecord(String keyString) {
      try {
        Entity e = datastore.get(makeKey(keyString));
        System.out.println("Record found [get]");
        return e;
      } catch (Exception e) {
        return null;
      }
    }
	}
}
