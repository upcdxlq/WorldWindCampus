import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.sqlite.SQLiteConfig;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

public class MyPolylineOnSqlite extends ApplicationTemplate {

	 protected static class AppFrame extends ApplicationTemplate.AppFrame
	    {
	        protected static final String SURFACE_POLYGON_IMAGE_PATH = "gov/nasa/worldwindx/examples/images/georss.png";

	        public AppFrame() throws ClassNotFoundException
	        {
	            // Add a basic dragger to the World Window's select listeners to enable shape dragging.
	            this.getWwd().addSelectListener(new BasicDragger((this.getWwd())));
	            // Create a layer of shapes to drag.   
	            this.makeShapes();
	            JButton refresh = new JButton("刷新");
	            refresh.addMouseListener(new MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						// TODO Auto-generated method stub
						try {
							makeShapes();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
	            this.getContentPane().add(refresh,BorderLayout.BEFORE_FIRST_LINE);
	        }
	        protected void makeShapes() throws ClassNotFoundException
	        {
	        	final WorldWindow wwd = this.getWwd();
	        	RenderableLayer layer = new RenderableLayer();
	            layer.setName("Polyline");
	           
	        	//connect to db
	        	  try {
					Class.forName("org.sqlite.JDBC");
				} catch (ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

	              Connection conn = null;
	              try
	              {
	                  // enabling dynamic extension loading
	                  // absolutely required by SpatiaLite
	                  SQLiteConfig config = new SQLiteConfig();
	                  config.enableLoadExtension(true);

	                  // create a database connection
	                  conn = DriverManager.getConnection("jdbc:sqlite:spatialite-test.sqlite",
	                  config.toProperties());
	                  Statement stmt = conn.createStatement();
	                  stmt.setQueryTimeout(30); // set timeout to 30 sec.

	                  // loading SpatiaLite
	                  stmt.execute("SELECT load_extension('mod_spatialite')");

	                  // checking SQLite and SpatiaLite version + target CPU
	                  String sql = "SELECT sqlite_version(), spatialite_version(), spatialite_target_cpu()";
	                  ResultSet rs = stmt.executeQuery(sql);
	                  while(rs.next()) {
	                      // read the result set
	                      String msg = "SQLite version: ";
	                      msg += rs.getString(1);
	                      System.out.println(msg);
	                      msg = "SpatiaLite version: ";
	                      msg += rs.getString(2);
	                      System.out.println(msg);
	                      msg = "target CPU: ";
	                      msg += rs.getString(3);
	                      System.out.println(msg);
	                  }
	                  
	                  // checking POINTs
	                  sql = "SELECT  ST_GeometryType(geom), ";
	                  sql += "ST_AsText(geom) FROM test_ln";
	                  rs = stmt.executeQuery(sql);
	                  while(rs.next()) {
	                      // read the result set
	                      String msg = " entities of type ";
	                      msg += rs.getString(1);
	                      msg += " SRID=";
	                      msg += rs.getString(2);
	                      System.out.println(msg);
	                      
	                      String geometryString = rs.getString(2);
	                      List<Double> geometryX = new ArrayList<>(); 
	                      List<Double> geometryY = new ArrayList<>();
	                      String geometryType =  ToSplitGeometry.getGeometry(geometryString,geometryX,geometryY);
	                      
	                     List<Position> plist = new ArrayList<>();
	                     for(int i=0;i<geometryX.size();i++){
	                    	 plist.add(Position.fromDegrees(geometryX.get(i), geometryY.get(i)));
	                     }
	                      Iterable<Position> positions = plist;
	                      Polyline polyline = new Polyline(positions);
	      	              layer.addRenderable(polyline);
	                  }
	             }catch(SQLException e1){
	              	System.err.println(e1);
	              }
	            if( wwd.getModel().getLayers().getLayerByName("Polyline")!=null)
	            {wwd.getModel().getLayers().remove( wwd.getModel().getLayers().getLayerByName("Polyline"));}
	            wwd.getModel().getLayers().add(layer);
	        }
	    }

	    public static void main(String[] args)
	    {
	        ApplicationTemplate.start("World Wind Dragging Shapes", AppFrame.class);
	    }
}
