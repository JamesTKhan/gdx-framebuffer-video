package com.jamestkhan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.*;

/**
 * @author JamesTKhan
 * @version January 31, 2023
 */
public class BaseScreen extends ScreenAdapter
{
    public PerspectiveCamera cam;
    public FirstPersonCameraController inputController;
    public ModelBatch modelBatch;
    public Model model;
    public Array<ModelInstance> instances;
    public Environment environment;
    private final SpriteBatch spriteBatch;

    private FrameBuffer fbo;

    public BaseScreen() {
        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_NONE;

        modelBatch = new ModelBatch(new DefaultShaderProvider(config));
        spriteBatch = new SpriteBatch();

        instances = new Array<>();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(20f, 10f, 20f);
        cam.lookAt(0, 0, 0);
        cam.up.set(Vector3.Y);
        cam.near = .05f;
        cam.far = 50f;
        cam.update();

        Gdx.input.setInputProcessor(new InputMultiplexer(inputController = new FirstPersonCameraController(cam)));
        inputController.setVelocity(5f);

        // Load Forest obj model
        loadModel();
    }

    private void buildFBO(int width, int height) {
        if (fbo != null) fbo.dispose();

        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(width, height);
        frameBufferBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888);

        // Enhanced precision, only needed for 3D scenes
        frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24);
        fbo = frameBufferBuilder.build();
    }

    @Override
    public void resize(int width, int height) {
        spriteBatch.getProjectionMatrix().setToOrtho2D(0,0, width, height);
        buildFBO(width, height);
    }

    @Override
    public void render(float delta) {
        inputController.update();

        fbo.begin();
        ScreenUtils.clear(Color.SKY, true);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
        fbo.end();

        // Get the color texture from the fbo
        Texture fboTex = fbo.getColorBufferTexture();

        spriteBatch.begin();
        spriteBatch.draw(fboTex, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0,0,1,1);
        spriteBatch.end();
    }


    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    private void loadModel() {
        ObjLoader objLoader = new ObjLoader();
        Model model = objLoader.loadModel(Gdx.files.internal("models/Low poly House.obj"));
        ModelInstance forest = new ModelInstance(model);
        forest.transform.rotate(Vector3.Y, 180);

        // Add some color to the materials
        forest.materials.get(0).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(1).set(ColorAttribute.createDiffuse(Color.TAN));
        forest.materials.get(2).set(ColorAttribute.createDiffuse(Color.FIREBRICK));
        forest.materials.get(3).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(4).set(ColorAttribute.createDiffuse(Color.TAN));
        forest.materials.get(5).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(6).set(ColorAttribute.createDiffuse(Color.FOREST));
        forest.materials.get(7).set(ColorAttribute.createDiffuse(Color.YELLOW));
        forest.materials.get(8).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        forest.materials.get(9).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(10).set(ColorAttribute.createDiffuse(Color.GREEN));
        forest.materials.get(11).set(ColorAttribute.createDiffuse(Color.SKY));
        forest.materials.get(12).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        forest.materials.get(13).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        forest.materials.get(14).set(ColorAttribute.createDiffuse(Color.GREEN));
        forest.materials.get(15).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(16).set(ColorAttribute.createDiffuse(Color.BROWN));
        forest.materials.get(17).set(ColorAttribute.createDiffuse(Color.FOREST));
        instances.add(forest);

        // Water opacity
        forest.materials.get(11).set(new BlendingAttribute(true, 0.75f));
    }
}
