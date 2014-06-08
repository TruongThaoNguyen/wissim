package wissim.object;

import java.util.ArrayList;
import java.util.Hashtable;

import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.InvalidSpriteIDException;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class wiSpriteManager extends SpriteManager {

    public wiSpriteManager(WiGraph graph) throws InvalidSpriteIDException {
        super(graph);
        // TODO Auto-generated constructor stub
    }
    @Deprecated
    @Override
    public Sprite addSprite(String identifier) throws InvalidSpriteIDException {
        return super.addSprite(identifier);
    }

    @Deprecated
    @Override
    public Sprite getSprite(String identifier) {
        return super.getSprite(identifier);
    }

    public wiSprite getWiSprite(String id) {
        Sprite s = addSprite(id);
        if (s != null) {
            return new wiSprite(s, this);
        } else {
            return null;
        }
    }

    public WiGraph getWiGraph(){
        return (WiGraph)graph;
    }
    public wiSprite AddWiSprite(String id) {
        Sprite s = addSprite(id);
        if (s != null) {
            return new wiSprite(s, this);
        } else {
            return null;
        }
    }

    @Override
    public void removeSprite(String identifier) {
        super.removeSprite(identifier);

    }
    public void removeAllSprites(){
        ArrayList<String> ids = new ArrayList<>();
        for (Sprite s : sprites.values()) {
            ids.add(s.getId());
        }
        for (String string : ids) {
            removeSprite(string);
        }
    }
}
