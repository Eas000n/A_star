import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Collection;

class Node implements Comparable<Node> {
    int x, y;
    double g; // cost from start to this node
    double h; // heuristic cost to the end
    Node father;

    Node(int x, int y, double g, double h, Node father) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.father = father;
    }

    double f;

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.f, other.f);
    }

    java.util.List<Node> getNeighbors(int[][] grid, int endx, int endy) {
        java.util.List<Node> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int x2 = this.x + dx[i];
            int y2 = this.y + dy[i];
            if (x2 >= 0 && x2 < grid.length && y2 >= 0 && y2 < grid[0].length && grid[x2][y2] == 0) {
                double g2 = this.g + 1; // 1 for straight
                double h2 = Math.abs(endx - x2) + Math.abs(endy - y2);
                neighbors.add(new Node(x2, y2, g2, h2, this));
            }
        }
        return neighbors;
    }

    boolean hasNode(Collection<Node> nodes) {
        for (Node node : nodes) {
            if (this.x == node.x && this.y == node.y) {
                return true;
            }
        }
        return false;
    }

    void changeG(Collection<Node> nodes) {
        for (Node node : nodes) {
            if (this.x == node.x && this.y == node.y) {
                if (this.g < node.g) {
                    node.g = this.g;
                    node.f = this.f;
                    node.father = this.father;
                }
            }
        }
    }
}

class WorkMap {
    int[][] data;
    int startx, starty, endx, endy;

    WorkMap(int[][] data, int startx, int starty, int endx, int endy) {
        this.data = data;
        this.startx = startx;
        this.starty = starty;
        this.endx = endx;
        this.endy = endy;
    }
}

public class AStar extends JPanel {

    private final int[][] grid;
    private final java.util.List<int[]> path;

    public AStar(int[][] grid, java.util.List<int[]> path) {
        this.grid = grid;
        this.path = path;
    }

    public static java.util.List<int[]> astar(WorkMap workMap) {
        int startx = workMap.startx;
        int starty = workMap.starty;
        int endx = workMap.endx;
        int endy = workMap.endy;
        Node startNode = new Node(startx, starty, 0, 0, null);
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closeList = new HashSet<>();
        openList.add(startNode);

        Node currNode = null;
        while (!openList.isEmpty()) {
            currNode = openList.poll();
            if (currNode.x == endx && currNode.y == endy) {
                break;
            }
            closeList.add(currNode);
            java.util.List<Node> workList = currNode.getNeighbors(workMap.data, endx, endy);
            for (Node neighbor : workList) {
                if (!closeList.contains(neighbor) && !openList.contains(neighbor)) {
                    openList.add(neighbor);
                } else if (openList.contains(neighbor)) {
                    neighbor.changeG(openList);
                }
            }
        }

        if (currNode == null || currNode.x != endx || currNode.y != endy) {
            return Collections.emptyList(); // No path found
        }

        java.util.List<int[]> result = new ArrayList<>();
        while (currNode != null) {
            result.add(new int[]{currNode.x, currNode.y});
            currNode = currNode.father;
        }
        Collections.reverse(result); // To get path from start to end
        return result;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tileSize = 40;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                g.setColor(Color.GRAY);
                g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }
        }
        g.setColor(Color.RED);
        for (int[] p : path) {
            g.fillRect(p[1] * tileSize, p[0] * tileSize, tileSize, tileSize);
        }
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0, 1, 0, 0, 0},
                {0, 1, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0}
        };
        WorkMap workMap = new WorkMap(grid, 0, 0, 4, 4);
        java.util.List<int[]> path = astar(workMap);

        JFrame frame = new JFrame("A* Pathfinding Visualization");
        AStar panel = new AStar(grid, path);
        frame.add(panel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
