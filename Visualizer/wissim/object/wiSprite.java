package wissim.object;

import java.util.ArrayList;

import org.graphstream.ui.spriteManager.Sprite;

public class wiSprite extends Sprite {

    wiSpriteManager man;
    Sprite sprite;

    public wiSprite(Sprite s, wiSpriteManager m) {
        sprite = s;
        man = m;
        sprite.addAttribute("data", new SpriteData());
    }

    protected wiSpriteManager getManager() {
        return man;
    }

    @Override
    public void attachToEdge(String id) {
        sprite.attachToEdge(id);
        man.getWiGraph().getwiEdge(id).getData().sprites.add(getId());
        getData().edgeId = id;
    }
    
    public SpriteData getData() {
        return sprite.getAttribute("data");
    }

    @Override
    public void detach() {
        sprite.detach();
        man.getWiGraph().getwiEdge(id).getData().sprites.remove(getId());
    }

    public void destroy() {
    }

    @Override
    public boolean attached() {
        return sprite.attached();
    }
}
