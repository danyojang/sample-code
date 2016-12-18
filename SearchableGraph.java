import java.util.*;

/*
/* @aurthor Daniel Jang
/* This class demonstrates the use of three path-searching algorithms: DFS, BFS, and Dijstra's algorithms
/* to solve two problems.
/* 1) six degrees of Kevin Bacon problem https://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon
/* 2) finding the minimum cost path to fly from point A to point B.
/* Depth first search (DFS) to see if a path is reachable.
/* Breath first search (BFS) to construct the shortest path from one actor to the other.
/* Dijstra's algorithm to find the mimnimum cost path of flights to get from point A to point B
/*
/* Assignment #8 in CSE 373 Winter 2013
/* University of Washington
/* Client and testing program is provided by https://courses.cs.washington.edu/courses/cse373/13wi/homework/8/provided/
*/

// Class SearchableGraph is used to search the graph
public class SearchableGraph<V, E> extends AbstractGraph<V, E> {

	// constructs an undirected, unweighted, empty graph
	public SearchableGraph() {
		super();
	}

	// constructs a new empty graph that can be directed/undirected,
	// weighted/unweighted
	public SearchableGraph(boolean directed, boolean weighted) {
		super(directed, weighted);
	}

  // DFS
	// Runtime: O(V+E), E = edges, V = vertices
	// pre  : if either of the vertices passed is null, throws NullPointerException.
	// if either of the vertices is not a part of the graph, throws NullPointerException.
	// post : returns true if there is path that leads from the given starting
	// vertex v1 to the given ending vertex v2
	public boolean isReachable(V v1, V v2) {
		checkAndClearVertices(v1, v2);
		return isReachableHelper(v1, v2);
	}

	private boolean isReachableHelper(V v1, V v2) {
		Vertex<V> info = vertexInfo(v1);
		info.setVisited(true);
		if (v1.equals(v2)) {
			return true;
		}
		for (V neighbor : super.neighbors(v1)) {
			Vertex<V> vi = vertexInfo(neighbor);
			if (!vi.visited()) { // unvisited neighbor
				if (isReachableHelper(neighbor, v2)) {
					return true;
				}
			}
		}
		return false;
	}
	// BFS
	// Runtime: O(V+E), E = edges, V = vertices
	// pre  : if either of the vertices passed is null, throws NullPointerException.
	// if either of the vertices is not a part of the graph, throws NullPointerException.
	// post : returns a list of the vertices that make up the shortest path from the
	// given starting vertex v1 to the given ending vertex v2.
	public List<V> shortestPath(V v1, V v2) {
		checkAndClearVertices(v1, v2);
		List<V> list = new LinkedList<V>();
		list.add(v1);
		Vertex<V> info = vertexInfo(v1);
		info.setVisited(true);
		boolean found = false;
		while (!list.isEmpty() && !found) {

			V v = list.remove(0);

			if (v.equals(v2)) {
				found = true;
			} else {
				for (V neighbor : super.neighbors(v)) {
					Vertex<V> vi = vertexInfo(neighbor);
					if (!vi.visited()) { // unvisited neighbor
						vi.setVisited(true);
						vi.setPrevious(v);
						list.add(neighbor);
					}
				}
			}
		}

		// found or list is empty
		if (found) {
			list = new LinkedList<V>();
			Vertex<V> v2Info = vertexInfo(v2);
			list.add(0, v2);
			while (v2Info.previous() != null) {
				list.add(0, v2Info.previous());
				v2Info = vertexInfo(v2Info.previous());
			}
			return list;
		} else {
			return null;
		}
	}

  // Dijkstra's algorithm
	// Runtime: O(E log V), E = edges, V = vertices
	// pre  : if either of the vertices passed is null, throws NullPointerException.
	// if either of the vertices is not a part of the graph, throws NullPointerException.
	// post : returns a list of the vertices that make up the minimum weighted path from
	// the given starting vertex v1 to the given ending vertex v2.
	public List<V> minimumWeightPath(V v1, V v2) {
		checkAndClearVertices(v1, v2);
		Queue<Vertex<V>> pqueue = new PriorityQueue<Vertex<V>>(100, new VertexComparator());

		// initialize: set initial distance from source to vertex to infinite(max cost)
		for (V v : super.vertices()) {
			Vertex<V> vInfo = vertexInfo(v);
			vInfo.setCost(Vertex.MAX_COST);
			pqueue.add(vInfo);
		}
		// update initial node to 0
		updateCost(pqueue, vertexInfo(v1), 0);
		while (!pqueue.isEmpty()) {
			Vertex<V> v = pqueue.remove();
			v.setVisited();
			for (V n : super.neighbors(v.vertex())) {
				Vertex<V> nInfo = vertexInfo(n);
				if (!nInfo.visited()) { // unvisited neighbor
					int cost = v.cost() + super.edgeWeight(v.vertex(), n);
					if (cost < nInfo.cost()) {
						updateCost(pqueue, nInfo, cost);
						nInfo.setPrevious(v.vertex());
					}
				}
			}
		}

		
		List<V> list = new LinkedList<V>();
		Vertex<V> v2Info = vertexInfo(v2);
		list.add(0, v2);
		boolean found = (v2 == v1);
		while (v2Info.previous() != null && !found) {
			V vPrevious = v2Info.previous();
			if (vPrevious.equals(v1)) {
				found = true;
			}
			list.add(0, v2Info.previous());
			v2Info = vertexInfo(v2Info.previous());

		}
		// found or not found
		if (found) {
			return list;
		} else {
			return null;
		}


	}



	// helper function: updates the priority queue with new costs
	private void updateCost(Queue<Vertex<V>> pqueue, Vertex<V> v, int cost) {
		v.setCost(cost);
		pqueue.remove(v);
		pqueue.add(v);
	}
	// if either of the vertices passed is null, throws NullPointerException.
	// if either of the vertices is not a part of the graph, throws NullPointerException.
	// clears the all Vertex Objects information
	private void checkAndClearVertices(V v1, V v2) {
		checkVertices(v1, v2);
		clearVertexInfo();
	}

	// compares vertices by their costs
	private class VertexComparator implements Comparator<Vertex<V>> {


		// returns an int comparing vertices, negative if v1's cost is less
		// than v2's cost, positive if v1's cost is greater than v2's cost,
		// and 0 if they are the same
		public int compare(Vertex<V> v1, Vertex<V> v2) {

			return v1.cost() - v2.cost();
		}
	}


}
