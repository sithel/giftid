package paper.yarn.ink;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class DbTestServlet extends HttpServlet {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String path = req.getPathInfo();
    Entity record = null;
    if (path != null) {
      path = path.substring(1);
      System.out.println("Looking up with path : '"+path+"'");
      Key key = KeyFactory.createKey("giftid1", Long.valueOf(path));
      try {
        record = datastore.get(key);
        System.out.println("Record found [get]");
      } catch (EntityNotFoundException e) { }
    }
    if (record == null) {
      System.out.println("No content found");
      resp.getWriter().print("{}");
      return;
    }
    resp.setContentType("text/plain");
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", record.getKey().getId());
      obj.put("user1", record.getProperty("user1"));
      obj.put("gotget", record.getProperty("gotget"));
      obj.put("gift", record.getProperty("gift"));
      obj.put("forfrom", record.getProperty("forfrom"));
      obj.put("user2", record.getProperty("user2"));
      System.out.println(obj.toString());
      System.out.println(record.getProperties().keySet());
      System.out.println(record.getProperties().keySet().size());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    PrintWriter out = resp.getWriter();
    out.print(obj);
    out.close();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws 
	    ServletException, 
	    IOException {
    String path = req.getPathInfo();
    Entity record = null;
    System.out.println("Looking up with path1 : '"+path+"'");
    if (path != null && path.length() > 1) {
      path = path.substring(1);
      System.out.println("Looking up with path2 : '"+path+"'");
      Key key = KeyFactory.createKey("giftid1", Long.valueOf(path));
      try {
        record = datastore.get(key);
        System.out.println("Entry found [post]");
      } catch (EntityNotFoundException e) { }
    }
    if (record == null) {
      record = new Entity("giftid1");
      System.out.println("Creating a new entry with key : "+record.getKey().getId());
    }
    record.setProperty("user1", req.getParameter("user1"));
    record.setProperty("gotget", req.getParameter("gotget"));
    record.setProperty("gift", req.getParameter("gift"));
    record.setProperty("forfrom", req.getParameter("forfrom"));
    record.setProperty("user2", req.getParameter("user2"));
    datastore.put(record);
    System.out.println("Entry saved [post] "+record.getKey().getId());
    resp.setContentType("text/plain");
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", record.getKey().getId());
      obj.put("user1", req.getParameter("user1"));
      obj.put("gotget", req.getParameter("gotget"));
      obj.put("gift", req.getParameter("gift"));
      obj.put("forfrom", req.getParameter("forfrom"));
      obj.put("user2", req.getParameter("user2"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    PrintWriter out = resp.getWriter();
    out.print(obj);
    out.close();
  }
}