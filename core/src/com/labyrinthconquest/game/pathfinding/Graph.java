package com.labyrinthconquest.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.labyrinthconquest.game.data.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the pathfinding graph for player characters
 */
public class Graph implements IndexedGraph<INode> {
    private Array<INode> nodes;
    private Array<Edge> edges;
    /**
     * Heuristic used for pathfinding. Here it is just euclidean distance
     */
    private GraphHeuristic heuristic;
    /**
     * Map all nodes to edges
     */
    private ObjectMap<INode, Array<Connection<INode>>> map;
    private World world;

    /**
     * Callback used in raycast path smoothing algorithm
     */
    private RayCastCallback callback;

    /**
     * Current stored path
     */
    private List<INode> path;

    /**
     * Store current node index. Used for optimization in the pathfinding algorithm
     */
    private int lastNodeIndex = 0;
    private boolean rayHit;

    /**
     * Construct a new graph for a tilemap
     * @param tiledMap Tilemap to make a new graph for
     * @param nodeLayer Layer from which to get the node positions
     * @param world Box2D world
     */
    public Graph(TiledMap tiledMap, String nodeLayer, World world) {
        nodes = new Array<>();
        edges = new Array<>();
        map = new ObjectMap<>();
        heuristic = new GraphHeuristic();
        this.world = world;
        path = new ArrayList<>();
        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                rayHit = true;
                return -1;
            }
        };
        createGraph(tiledMap, nodeLayer);
    }

    /**
     * Create the graph itself (nodes and edges)
     * @param tiledMap Tilemap to make a new graph for
     * @param nodeLayer Layer from which to get the node positions
     */
    private void createGraph(TiledMap tiledMap, String nodeLayer) {
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer)tiledMap.getLayers().get(nodeLayer);
        INode[][] nodeMatrix = new INode[tiledMapTileLayer.getWidth()][tiledMapTileLayer.getHeight()];
        int width = tiledMapTileLayer.getWidth();
        int height = tiledMapTileLayer.getHeight();
        // Create nodes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(x, y);
                if (cell == null) continue;
                Vector2 position = new Vector2(x + 0.5f, y + 0.5f);
                INode newNode = new Node(position.x, position.y);
                addNode(newNode);
                nodeMatrix[x][y] = newNode;
            }
        }
        // Connect nodes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                INode currentNode = nodeMatrix[x][y];
                if (currentNode == null) continue;
                connectNode(nodeMatrix, width, height, x, y, currentNode);
            }
        }
    }

    /**
     * Connect at nodeMatrix[X][Y] to all possible non-null neighbours
     * @param nodeMatrix Node matrix with all current nodes
     * @param width Width of the tilemap layer
     * @param height Height of the tilemap layer
     * @param x X position of node
     * @param y Y position of node
     * @param currentNode The current node itself
     */
    private void connectNode(INode[][] nodeMatrix, int width, int height, int x, int y, INode currentNode) {
        if (x < width - 1 && nodeMatrix[x + 1][y] != null) connectNodes(currentNode, nodeMatrix[x + 1][y]);
        if (x > 0 && nodeMatrix[x - 1][y] != null) connectNodes(currentNode, nodeMatrix[x - 1][y]);
        if (y < height - 1 && nodeMatrix[x][y + 1] != null) connectNodes(currentNode, nodeMatrix[x][y + 1]);
        if (y > 0 && nodeMatrix[x][y - 1] != null) connectNodes(currentNode, nodeMatrix[x][y - 1]);
        if (x < width - 1 && y < height - 1 && nodeMatrix[x + 1][y + 1] != null) connectNodes(currentNode, nodeMatrix[x + 1][y + 1]);
        if (x > 0 && y < height - 1 && nodeMatrix[x - 1][y + 1] != null) connectNodes(currentNode, nodeMatrix[x - 1][y + 1]);
        if (x < width - 1 && y > 0 && nodeMatrix[x + 1][y - 1] != null) connectNodes(currentNode, nodeMatrix[x + 1][y - 1]);
        if (x > 0 && y > 0 && nodeMatrix[x - 1][y - 1] != null) connectNodes(currentNode, nodeMatrix[x - 1][y - 1]);
    }

    /**
     * Add a new node to the graph
     * @param node Node to add
     */
    private void addNode(INode node) {
        node.setIndex(lastNodeIndex++);
        nodes.add(node);
    }

    /**
     * Connect nodes from to (create an edge and store it)
     * @param from From node
     * @param to To node
     */
    private void connectNodes(INode from, INode to){
        Edge edge = new Edge(from, to);
        if(!map.containsKey(from)){
            map.put(from, new Array<Connection<INode>>());
        }
        map.get(from).add(edge);
        edges.add(edge);
    }

    /**
     * Find the closest node to position
     * @param pos Position to query for
     * @return Node closest to position
     */
    public INode queryPosition(Vector2 pos) {
        INode curr = nodes.get(0);
        float dst = Vector2.dst(pos.x, pos.y, curr.getX(), curr.getY());
        for(int i = 1; i < nodes.size; i++) {
            INode node = nodes.get(i);
            float tempDst = Vector2.dst(pos.x, pos.y, node.getX(), node.getY());
            if (tempDst < dst) {
                dst = tempDst;
                curr = node;
            }
        }
        return curr;
    }

    /**
     * Attempt to find a path from node to node, if one exists
     * @param from From node
     * @param to To node
     * @return Path if one exists, else an empty list
     */
    public List<INode> findPath(Vector2 from, Vector2 to) {
        List<INode> newPath = getPath(queryPosition(from), queryPosition(to), from, to);
        if (newPath.isEmpty()) {
            return new ArrayList<>();
        }
        return newPath;
    }

    /**
     * Get path from node to node. Needs original player position and touch position for the smoothing algorithm
     * @param fromN From node
     * @param toN To node
     * @param fromV From vector (position of player)
     * @param toV To vector (position of where we clicked to find a path)
     * @return Path if one exists, else an empty list
     */
    private List<INode> getPath(INode fromN, INode toN, Vector2 fromV, Vector2 toV) {
        GraphPath<INode> newPath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(fromN, toN, heuristic, newPath);
        if (newPath.getCount() == 0) {
            return new ArrayList<>();
        }
        smoothPath(fromV, toV, newPath);
        return path;
    }

    /**
     * Path smoothing algorithm. Attempts to e.g reduce amount of turns in the path to a minimum so it is slightly more natural
     * Uses raycast to check if a node can be excluded from the final path
     * @param fromV From node
     * @param toV To node
     * @param newPath The path to smooth
     */
    private void smoothPath(Vector2 fromV, Vector2 toV, GraphPath<INode> newPath) {
        List<INode> tempPath = new ArrayList<>();
        INode fromNode = new Node(fromV.x, fromV.y);
        tempPath.add(fromNode);
        for(int i = 1; i < newPath.getCount(); i++) tempPath.add(newPath.get(i));
        INode toNode = new Node(toV.x, toV.y);
        tempPath.add(toNode);

        path.clear();
        path.add(fromNode);
        int i = 0;
        while(i < tempPath.size() - 1) {
            INode cur = tempPath.get(i);
            INode next;
            for(int j = i + 1; j < tempPath.size() - 1; j++) {
                next = tempPath.get(j);
                world.rayCast(callback, cur.getX(), cur.getY(), next.getX(), next.getY());
                world.rayCast(callback, cur.getX() + Constants.PATH_SMOOTH_SIDE_OFFSET, cur.getY(),
                        next.getX() + Constants.PATH_SMOOTH_SIDE_OFFSET, next.getY());
                world.rayCast(callback, cur.getX() - Constants.PATH_SMOOTH_SIDE_OFFSET, cur.getY(),
                        next.getX() - Constants.PATH_SMOOTH_SIDE_OFFSET, next.getY());
                if (rayHit) {
                    i = j - 1;
                    path.add(tempPath.get(i));
                    rayHit = false;
                    break;
                }
            }
            i++;
        }
        path.add(toNode);
    }

    @Override
    public int getIndex(INode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    /**
     * Get all connections leaving node from
     * @param from From node
     * @return Array of connections
     */
    @Override
    public Array<Connection<INode>> getConnections(INode from) {
        if(map.containsKey(from)){
            return map.get(from);
        }
        return new Array<>(0);
    }
}
