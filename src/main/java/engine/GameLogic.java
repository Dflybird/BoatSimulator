package engine;

import gui.Window;

/**
 * @Author Gq
 * @Date 2021/1/28 19:32
 * @Version 1.0
 **/
public interface GameLogic {

    void init(Window window);

    void input();

    void update(double stepTime);

    void render(double alpha);

    void cleanup();
}
