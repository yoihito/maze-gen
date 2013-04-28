import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;


public class Main extends Applet{

	/**
	 * @param args
	 */
	
	public void init() {
		JButton refresh = new JButton("Refresh");
		final Maze maze = new Maze();
		
		refresh.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				maze.repaint();
			}
		});
		
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setSize(600,600);
		add(maze);
		add(refresh);
		setVisible(true);
		
	}

}

class DSU {
	Vector<Integer> pred;
	Vector<Integer> rank;
	
	DSU(int N) {
		pred = new Vector<Integer>();
		rank = new Vector<Integer>();
		for (int i = 0; i < N; i++) {
			pred.add(i);
			rank.add(0);
		}
	}
	
	Integer getSet(Integer x) {
		if (pred.elementAt(x)==x) return x;
		pred.set(x, this.getSet(pred.elementAt(x)));
		return pred.elementAt(x);
	}
	
	void unionSets(Integer x, Integer y) {
		Integer a = this.getSet(x);
		Integer b = this.getSet(y);
		if (rank.elementAt(a)<rank.elementAt(b)) {
			Integer tmp = a;
			a = b;
			b = tmp;
		}
		pred.set(b,a);
		if (rank.elementAt(a) == rank.elementAt(b)) 
			rank.set(a,rank.elementAt(a)+1);
	}
	
}

@SuppressWarnings("serial")
class Maze extends JPanel{

	Vector<Pair<Integer,Integer>> G;
	final int step[][] = {{0,1},{1,0},{0,-1},{-1,0}};
	final int xCellsCount = 70;
	final int yCellsCount = 70;
	final int cellWidth = 7;
	final int vertexCount = xCellsCount*yCellsCount;
	
	Maze() {
		super();
		this.setBackground(Color.white);
	}
	
	private void generateMaze() {
		Vector<Pair<Integer,Integer>> G = new Vector<Pair<Integer,Integer>>();
		for (int i = 0; i < xCellsCount; i++)
			for (int j = 0; j< yCellsCount; j++) {
				for (int k = 0; k < 4; k++) {
					int cx,cy;
					cx = i + step[k][0];
					cy = j + step[k][1];
					if (cx>=0 && cx<xCellsCount && cy>=0 && cy<yCellsCount) {
						G.add(new Pair<Integer,Integer>(i*xCellsCount+j,cx*xCellsCount+cy));
					}
				}
			}
		Collections.shuffle(G,new Random(System.nanoTime()));
		Vector<Pair<Integer,Integer>> ansG = new Vector<Pair<Integer,Integer>>();
		DSU t = new DSU(vertexCount);
		for (int i = 0; i < G.size(); i++) 
			if (t.getSet(G.elementAt(i).a)!= t.getSet(G.elementAt(i).b)) {
				t.unionSets(G.elementAt(i).a,G.elementAt(i).b);
				ansG.add(G.elementAt(i));
			}
		this.G=ansG;

	}
	
	public void paintComponent(Graphics c) {
		super.paintComponent(c);
		generateMaze();
		Graphics2D c2D = (Graphics2D)c;
		
		for (int i = 0; i < xCellsCount; i++)
			for (int j = 0; j < yCellsCount; j++) c2D.drawRect(i*cellWidth+1, j*cellWidth+1, cellWidth, cellWidth);
		c2D.setColor(Color.white);
		for (int i = 0; i < G.size(); i++) {
			Integer a = G.get(i).a, b = G.get(i).b;
			Integer xa = a/xCellsCount, ya=a%xCellsCount;
			Integer xb = b/xCellsCount, yb=b%xCellsCount;
			if (xa>xb) {int tmp = xa; xa=xb; xb=tmp; }
			if (ya>yb) {int tmp = ya; ya=yb; yb=tmp; }
			if (ya==yb) {
				c2D.drawLine(xb*cellWidth+1, yb*cellWidth+2, (xa+1)*cellWidth+1, (ya+1)*cellWidth);
			} else
			if (xa==xb) {
				c2D.drawLine(xb*cellWidth+2, yb*cellWidth+1, (xa+1)*cellWidth, (ya+1)*cellWidth+1);
			}
		}
	}
}

class Pair<T,V> {
	public T a;
	public V b;
	Pair (T a, V b) {
		this.a=a;
		this.b=b;
	}
}
