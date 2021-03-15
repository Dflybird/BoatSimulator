package sim;

import ams.agent.Agent;
import ams.agent.CubeAgent;
import ams.agent.USVAgent;
import ams.msg.SteerMessage;
import conf.Constant;
import environment.Ocean;
import conf.Config;
import ams.AgentManager;
import engine.GameEngine;
import engine.GameLogic;
import gui.*;
import gui.graphic.light.DirectionalLight;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.obj.Model;
import gui.obj.geom.CubeObj;
import gui.obj.usv.BoatObj;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.buoy.BuoyHelper;
import physics.PhysicsEngine;
import physics.buoy.ModifyBoatMesh;
import physics.entity.Entity;
import physics.entity.geom.CubeEntity;
import physics.entity.usv.BoatEntity;
import state.GUIState;
import util.TimeUtil;

import java.io.File;
import java.lang.Math;

import static conf.Constant.*;
import static conf.Constant.MAX_ANGLE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @Author Gq
 * @Date 2021/1/20 23:27
 * @Version 1.0
 **/
public class SimGUI implements GameLogic {

    private final Logger logger = LoggerFactory.getLogger(SimGUI.class);

    public static void main(String[] args) {
        main(args, new SimGUI());
    }

    public static void main(String[] args, SimGUI sim){
        sim.start();
    }


    public SimGUI() {

    }

    @Override
    public void init(Window window){

    }

    @Override
    public void input(Window window, MouseEvent mouseEvent) {

    }

    @Override
    public void update(double stepTime) {

    }

    @Override
    public void render(double alpha) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void play() {

    }

    public void start(){

    }

}
