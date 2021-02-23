package gui;

import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author Gq
 * @Date 2020/12/13 22:18
 * @Version 1.0
 **/
public class MouseEvent {
    private final Vector2d previousPos;
    private final Vector2d currentPos;

    //移动方向向量
    private final Vector2f displVec;

    private boolean inWindow = false;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;

    private boolean leftButtonRelease = false;

    public MouseEvent() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {
        //监听鼠标当前坐标
        glfwSetCursorPosCallback(window.getWindowID(), (windowNum, x, y) -> {
            currentPos.x = x;
            currentPos.y = y;
        });

        //监听鼠标是否进入窗口
        glfwSetCursorEnterCallback(window.getWindowID(), (windowNum, entered) -> {
            inWindow = entered;
        });

        //监听鼠标点击事件
        glfwSetMouseButtonCallback(window.getWindowID(), (windowNum, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            leftButtonRelease = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE;
        });
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        //窗口左下角坐标为(0,0)
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displVec.y = (float) deltaX;
            }
            if (rotateY) {
                displVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isLeftButtonRelease() {
        return leftButtonRelease;
    }
}
