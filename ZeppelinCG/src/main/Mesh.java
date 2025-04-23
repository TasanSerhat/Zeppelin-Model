package main;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL46.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import static org.lwjgl.stb.STBImage.*;

public class Mesh {

    public float[] vertices;
    public float[] normals;
    public int[] indices;
    public float[] texture;

    public int vaoId;
    public int vboId1;
    public int vboId2;
    public int vboId3;
    public int inId;
    public int programId;
    public int textureId;

    IntBuffer indBuffer = null;
    Matrix4f model = null;
    Matrix4f rot = null;
    float[] lightPos = new float[4];

    int loc1;
    int loc2;
    int loc3;
    int loc4;
    int loc5;
    int loc6;

    FloatBuffer projBuffer;
    FloatBuffer modelBuffer;
    FloatBuffer cameraBuffer;
    FloatBuffer rotBuffer;

    public void SetUniforms() {
        glLinkProgram(programId);
        glUseProgram(programId);

        loc1 = glGetUniformLocation(programId, "text_samp");
        glUniform1i(loc1, 0);
        loc2 = glGetUniformLocation(programId, "proj");
        projBuffer = BufferUtils.createFloatBuffer(16);
        var mat1 = Global.currentCam.Projection();
        mat1.get(projBuffer);
        glUniformMatrix4fv(loc2, false, projBuffer);

        loc3 = glGetUniformLocation(programId, "model");
        modelBuffer = BufferUtils.createFloatBuffer(16);
        model.get(modelBuffer);
        glUniformMatrix4fv(loc3, false, modelBuffer);

        loc4 = glGetUniformLocation(programId, "camera");
        cameraBuffer = BufferUtils.createFloatBuffer(16);
        var mat2 = Global.currentCam.Camera();
        mat2.get(cameraBuffer);
        glUniformMatrix4fv(loc4, false, cameraBuffer);

        if (rot == null) {
            rot = new Matrix4f().identity();
        }

        loc5 = glGetUniformLocation(programId, "rot");
        rotBuffer = BufferUtils.createFloatBuffer(16);
        rot.get(rotBuffer);
        glUniformMatrix4fv(loc5, false, rotBuffer);
    }

    public void UpdateUniforms() {
        glLinkProgram(programId);
        glUseProgram(programId);

        glUniform1i(loc1, 0);
        var mat1 = Global.currentCam.Projection();
        mat1.get(projBuffer);
        glUniformMatrix4fv(loc2, false, projBuffer);

        modelBuffer = BufferUtils.createFloatBuffer(16);
        model.get(modelBuffer);
        glUniformMatrix4fv(loc3, false, modelBuffer);

        var mat2 = Global.currentCam.Camera();
        mat2.get(cameraBuffer);
        glUniformMatrix4fv(loc4, false, cameraBuffer);

        rotBuffer = BufferUtils.createFloatBuffer(16);
        rot.get(rotBuffer);  // Rotasyonu burada güncellediğimizden emin ol
        glUniformMatrix4fv(loc5, false, rotBuffer);

        lightPos[0] = Global.currentLight.position.x;
        lightPos[1] = Global.currentLight.position.y;
        lightPos[2] = Global.currentLight.position.z;
        lightPos[3] = 1;
        glUniform4fv(loc6, lightPos);
    }

    public void SetPosition(Vector3f pos) {
        model.translate(pos);
    }

    public void SetLight() {
        loc6 = glGetUniformLocation(programId, "lPos");
        lightPos[0] = Global.currentLight.position.x;
        lightPos[1] = Global.currentLight.position.y;
        lightPos[2] = Global.currentLight.position.z;
        lightPos[3] = 1;
        glUniform4fv(loc6, lightPos);
    }

    public void Rotate(float deltaAngle) {
        // Y ekseni etrafında döndürme matrisi oluşturuluyor
        Matrix4f rotationMatrix = new Matrix4f().identity();

        // Çember etrafında dönme için Y ekseni etrafında dönüş matrisini oluşturuyoruz
        rotationMatrix.rotateY((float) Math.toRadians(deltaAngle));

        // objenin dönüşünü uygula
        rot = rotationMatrix.mul(rot);
    }

    public Mesh(String modelPath, String shaderPath, Vector3f pos) {
        programId = glCreateProgram();

        int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        try {
            String vert = new String(Files.readAllBytes(Paths.get(shaderPath + ".vs")));
            String frag = new String(Files.readAllBytes(Paths.get(shaderPath + ".fs")));

            glShaderSource(vertexShaderId, vert);
            glCompileShader(vertexShaderId);

            glShaderSource(fragmentShaderId, frag);
            glCompileShader(fragmentShaderId);

            glAttachShader(programId, vertexShaderId);
            glAttachShader(programId, fragmentShaderId);
        } catch (Exception e) {
            System.out.println(e);
        }

        AIScene scene = Assimp.aiImportFile(modelPath + ".obj", 0);
        PointerBuffer meshes = scene.mMeshes();

        for (int i = 0; i < meshes.limit(); i++) {
            AIMesh aiMesh = AIMesh.create(meshes.get(i));
            var vertexBuffer = aiMesh.mVertices();
            vertices = new float[vertexBuffer.limit() * 3];
            int j = 0;

            while (vertexBuffer.remaining() > 0) {
                var v = vertexBuffer.get();
                vertices[j++] = v.x();
                vertices[j++] = v.y();
                vertices[j++] = v.z();
            }

            var _normals = aiMesh.mNormals();
            normals = new float[_normals.limit() * 3];
            j = 0;

            for (int k = 0; k < _normals.limit(); k++) {
                var n = _normals.get(k);
                normals[j++] = n.x();
                normals[j++] = n.y();
                normals[j++] = n.z();
            }

            List<Integer> _indices = new ArrayList<>();
            int numFaces = aiMesh.mNumFaces();
            AIFace.Buffer aiFaces = aiMesh.mFaces();

            for (j = 0; j < numFaces; j++) {
                AIFace aiFace = aiFaces.get(j);
                IntBuffer buffer = aiFace.mIndices();

                while (buffer.remaining() > 0) {
                    _indices.add(buffer.get());
                }
            }

            indices = _indices.stream().mapToInt(Integer::intValue).toArray();

            var buffer = aiMesh.mTextureCoords(0);

            texture = new float[buffer.remaining() * 2];
            j = 0;

            while (buffer.remaining() > 0) {
                var textCoord = buffer.get();
                texture[j++] = textCoord.x();
                texture[j++] = 1 - textCoord.y();
            }
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        w.put(0, 1024);
        h.put(0, 1024);
        channels.put(0, 4);

        ByteBuffer buff = stbi_load(modelPath + ".jpg", w, h, channels, 4);
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buff);
        glGenerateMipmap(GL_TEXTURE_2D);

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        FloatBuffer positionsBuffer = BufferUtils.createFloatBuffer(vertices.length);
        FloatBuffer textBuffer = BufferUtils.createFloatBuffer(texture.length);
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.length);
        IntBuffer indBuffer = BufferUtils.createIntBuffer(indices.length);

        indBuffer.put(0, indices);
        positionsBuffer.put(0, vertices);
        textBuffer.put(0, texture);
        normalBuffer.put(0, normals);

        vboId1 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId1);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        vboId2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ARRAY_BUFFER, textBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        vboId3 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId3);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        inId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, inId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer, GL_STATIC_DRAW);

        model = new Matrix4f().identity();
        model.setTranslation(pos.x(), pos.y(), pos.z());

        SetUniforms();
    }

    public void Draw() {
        //glLinkProgram(programId);
        //glUseProgram(programId);
        UpdateUniforms();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glActiveTexture(GL_TEXTURE0);

        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }
}
