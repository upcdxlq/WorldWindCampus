import javax.media.opengl.GL2;

import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;

public class DrawCubeLayer extends RenderableLayer{

	@Override
	protected void doRender(DrawContext dc) {
		// TODO Auto-generated method stub
	        this.beginDrawing(dc);
	        try
	        {
	            GL2 gl = dc.getGL().getGL2();
	            gl.glScaled(1000, 1000, 1000);
	            this.drawUnitCube(dc);
	        }
	        finally
	        {
	            this.endDrawing(dc);
	        }
		
	}
	
	 protected void beginDrawing(DrawContext dc)
	    {
	        GL2 gl = dc.getGL().getGL2();
	        //save sate
	        gl.glPushAttrib(GL2.GL_DEPTH_BUFFER_BIT
	                | GL2.GL_COLOR_BUFFER_BIT
	                | GL2.GL_ENABLE_BIT
	                | GL2.GL_TRANSFORM_BIT
	                | GL2.GL_VIEWPORT_BIT
	                | GL2.GL_CURRENT_BIT);
            // set Color
	        gl.glColor4d(0,1,0,1);
	        //save matrix
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glPushMatrix();
	        // set matrix
	        Matrix matrix = dc.getGlobe().computeSurfaceOrientationAtPosition(Position.fromDegrees(35.0, -120.0, 3000));
	        matrix = dc.getView().getModelviewMatrix().multiply(matrix);
	        double[] matrixArray = new double[16];
	        matrix.toArray(matrixArray, 0, false);
	        gl.glLoadMatrixd(matrixArray, 0);
	    }
	    protected void endDrawing(DrawContext dc)
	    {
	        GL2 gl = dc.getGL().getGL2();

	        // recover state
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glPopMatrix();
	        gl.glPopAttrib();
	    }
	    protected void drawUnitCube(DrawContext dc)
	    {
	        // Vertices of a unit cube, centered on the origin.
	        float[][] v = {{-0.5f, 0.5f, -0.5f}, {-0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, -0.5f}, {-0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, -0.5f}, {-0.5f, -0.5f, -0.5f}};

	        // Array to group vertices into faces
	        int[][] faces = {{0, 1, 2, 3}, {2, 5, 6, 3}, {1, 4, 5, 2}, {0, 7, 4, 1}, {0, 7, 6, 3}, {4, 7, 6, 5}};

	        // Normal vectors for each face
	        float[][] n = {{0, 1, 0}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {0, -1, 0}};

	        GL2 gl = dc.getGL().getGL2();
	        // Use OpenGL immediate mode for simplicity. Real applications should use
	        // vertex arrays or vertex buffer objects for best performance.
	        //see current color
	        double[] cc = new double[4];
	        gl.glGetDoublev(GL2.GL_CURRENT_COLOR,cc,1);
	        //gl.glGetDoublei_vEXT(GL2.GL_COLOR_CLEAR_VALUE, 0, cc, 0);
	        System.out.println(cc[0]+","+cc[1]+","+cc[2]+","+cc[3]);
	        gl.glBegin(GL2.GL_QUADS);
	        try
	        { 
	            for (int i = 0; i < faces.length; i++)
	            {
	                //gl.glNormal3f(n[i][0], n[i][1], n[i][2]);

	                for (int j = 0; j < faces[0].length; j++)
	                {  
	                    gl.glVertex3f(v[faces[i][j]][0], v[faces[i][j]][1], v[faces[i][j]][2]);
	                }
	            }
	        }
	        finally
	        {
	            gl.glEnd();           
	        }
	    }
}
