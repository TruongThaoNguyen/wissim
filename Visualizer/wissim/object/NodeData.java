/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wissim.object;

import java.util.LinkedList;

/**
 *
 * @author Hoang
 */
public class NodeData {
    public wiNode.State state;
    public LinkedList<wiNode.State> queueState = new LinkedList();
}
