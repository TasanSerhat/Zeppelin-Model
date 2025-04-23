package main;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {

    public Vector3f position;
    public Mesh lightMesh = null;

    public Light(String obj, String shader, Vector3f pos) {
        position = pos;
        lightMesh = new Mesh(obj, shader, position);
    }

    public void Move(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
        lightMesh.SetPosition(new Vector3f(x, y, z));
    }

    public void Rotate(float angle) {
        // Y eksenine göre bir dönüş matrisini oluştur
        Matrix4f rotationMatrix = new Matrix4f().rotateY((float) Math.toRadians(angle));

        // lightMesh üzerinde dönüş uygulama
        lightMesh.Rotate(angle);  // Buradaki fonksiyon da açıyı almalı
    }

}
