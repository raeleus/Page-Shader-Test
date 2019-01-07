package com.ray3k.shaders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
 *
 *  Based on ShadersLW.java by Alexey Smovzh.
 *  https://bitbucket.org/alexey_smovzh/shaderslw/src/master/
 *  
 *  Modified by Raymond Buckley to simplify the class, bind two textures, and
 *  pass mouse coordinates.
 *
 *  Copyright 2017 Alexey Smovzh (alexey.smovzh@gmail.com)
 *  Modifications Copyright (C) 2019 Raymond Buckley
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
public class Core extends ApplicationAdapter {
    private ShaderProgram shader;
    private float time;
    private float speed;
    private float size;
    private Viewport viewport;
    private Texture texture1, texture2;
    private Mesh mesh;
    private Vector2 lastClick = new Vector2();

    @Override
    public void create() {
        speed = 1.0f;
        size = 1;
        //Defining the following prevents it from crashing if we forget a variable
        ShaderProgram.pedantic = false;

        /*The vertex shader is not manipulated by ShaderToy shaders. Use the 
        same one for whichever shader you pull from the site.*/
        String vertexShader = Gdx.files.internal("shaders/vertexShader.vs").readString();
        String fragmentShader = Gdx.files.internal("shaders/page.fs").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
        System.out.println(shader.isCompiled());
        System.out.println(shader.getLog());
        
        //create a viewport, not really used to its full potential here
        viewport = new ScreenViewport();
        viewport.apply();
        
        /*The mesh is used to render the shader. The textures are passed to the
        shader and represent the different pages. These can instead by generated
        from FBO's instead for your typical game.*/
        texture1 = new Texture(Gdx.files.local("badlogic.jpg"));
        texture2 = new Texture(Gdx.files.local("badlogic.jpg"));
        mesh = setupMesh();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //time in seconds since the beginning of the animation. Not used in this example
        time += Gdx.graphics.getDeltaTime();
        //record the last clicked position
        if (Gdx.input.justTouched()) {
            lastClick.set(Gdx.input.getX(), Gdx.input.getY());
        }

        viewport.apply();
        shader.begin();
        
        //pass all the app related details to the shader
        shader.setUniformMatrix("u_projTrans", viewport.getCamera().combined);
        shader.setUniformf("u_time", time);
        shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader.setUniformf("u_speed", speed);
        shader.setUniformf("u_size", size);
        shader.setUniformf("u_mouse", Gdx.input.getX(), Gdx.input.getY(), lastClick.x, lastClick.y);
        
        //pass the textures to the shader
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1);
        texture2.bind(1);
        shader.setUniformi("u_texture1", 1);
        
        Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
        texture1.bind(0);
        shader.setUniformi("u_texture1", 0);
        
        mesh.render(shader, GL20.GL_TRIANGLES);

        shader.end();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        mesh.dispose();
        mesh = setupMesh();
    }

    @Override
    public void dispose() {
        shader.dispose();
        mesh.dispose();
    }
    
    private Mesh setupMesh() {
        Mesh mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));

        //drawing bounds of mesh
        mesh.setVertices(new float[]{
            Gdx.graphics.getWidth(), 0f, 0f,
            0f, 0f, 0f,
            0f, Gdx.graphics.getHeight(), 0f,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f});

        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});

        return mesh;

    }
}
