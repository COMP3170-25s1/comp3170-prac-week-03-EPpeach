package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;
	
	private Matrix4f modelMatrix = new Matrix4f();  //Add a model matrix

	private Shader shader;

	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
		//========== Week3 Add a Model Matrix =============
		// Just doing this so matrix matches diagrams :P
		modelMatrix = modelMatrix.identity();
		modelMatrix.m22(0);
		System.out.println(modelMatrix);  //IT PRINTS!! Well made library, very nice
		
		// ---- b) Rotate         ----
		//modelMatrix = rotationMatrix((float) (-90*(Math.PI/180)), modelMatrix); //IT'S RADIANS DON'T FORGET
		//System.out.println(modelMatrix); 
		
		// ---- c) Move and scale ----
		//TRS order?
		modelMatrix = translationMatrix(0.5f, -0.5f, modelMatrix);
		modelMatrix = scaleMatrix(0.5f, 0.5f, modelMatrix);
		
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_modelMatrix", modelMatrix);  //Add a Model Matrix
		shader.setAttribute("a_colour", colourBuffer);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
		

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		//dest.identity();   // Wouldn't the identity matrix have a 1 in k as well?

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {		
		//    [cos(angle) -sin(angle) 0 0] x
		// T =[sin(angle)  cos(angle) 0 0] y
		//    [    0           0      0 0] z
		//    [    0           0      0 1] 
		//         i           j      k T
		
		//Live lecture doesn't have dest.idenity() in translation either... hmm
		
		dest.m00((float) Math.cos(angle));  //From the Live lecture, also going in vertical order like JOML does
		dest.m01((float) Math.sin(angle));
		dest.m10((float) Math.sin(-angle));  //Equivalent to -sin(angle)
		dest.m11((float) Math.cos(angle));

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		//    [sx 0  0 0] x
		// T =[0  sy 0 0] y
		//    [0  0  0 0] z
		//    [0  0  0 1] 
        //     i  j  k T]
		
		dest.m00(sx);
		dest.m11(sy); //So this is different to live lecture version bc the calculation is already done
		
		return dest;
	}

}
