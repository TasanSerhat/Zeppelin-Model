
package main;

import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera 
{
    private Matrix4f projection = new Matrix4f();
    private Matrix4f camera = new Matrix4f();
    private Vector3f center = new Vector3f();
    private double fov = 0;
    private float focalLength = 0;
    
    public Camera(double fov, float focalLength, Vector3f pos)
    {
        this.fov = fov;
        this.focalLength = focalLength;
        center = pos;
        projection.setPerspective((float) fov, 800/(float)600, 0.001f, 1000f);
        center.set(pos.x, pos.y, pos.z);
        camera = camera.identity();
        camera.translate(pos);
    }
    
    public void Move(Vector3f vec)
    {
        camera.translate(vec);
    }

    public Matrix4f Projection()
    {
        return projection;
    }
    
    public Matrix4f Camera()
    {
        return camera;
    }
}
