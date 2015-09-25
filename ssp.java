import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.*;


/*This is the main class which encompasses the Graph, Fibonacci and Node as its subclasses.  Basically to implement the Shortest path algorithm
we need these three main classes which implement the whole algorithm*/

public class ssp{

	/* Graph class is basically required for creating the Adjacency list representation from given input file*/
	public class Graph{
		private int no_vertices;
		private int no_edges;
		private ArrayList<LinkedList<edge>> adj_list;
		
		/*Class edge is used to define edges in terms of start vertex, end vertex and the weight of the edge that connects them*/
		public class edge {
			private int vertex1;
			private int vertex2;
			private int weight;
			
			public edge (int vertex1, int vertex2, int weight){
				this.vertex1 = vertex1;
				this.vertex2 = vertex2;
				this.weight = weight;
			}
		}
		
		/* Method used to access the number of vertices in the graph given*/
		public int getnoofvertices (){
			return no_vertices;
		}
		
		/*Method used to get number of edges in the graph given*/
		public int getnoedges () {
			return no_edges;
		}
			
		/*tryfucntion is method used to parse the input text graph text file and then create the adjancey list bases on the scans performed over the 
		input file*/
		public void tryfunction (String fromfile){
			try {
					Scanner inputfile = new Scanner(new File (fromfile));
					no_vertices = inputfile.nextInt();
					//System.out.print(no_vertices);
					no_edges = inputfile.nextInt();
					//System.out.print(" "+no_edges);
					adj_list = new ArrayList<LinkedList <edge>> (no_vertices);
					for (int i = 0; i < no_vertices; i++){
						adj_list.add(new LinkedList<edge>());
					}
						
					while (inputfile.hasNextInt()){
						int firstvertex = inputfile.nextInt();
						int secondvertex = inputfile.nextInt();
						int weight = inputfile.nextInt();
						
						putedgeinadjlist (firstvertex, secondvertex, weight);
					
					}
				
					inputfile.close();
			}
			catch (FileNotFoundException e) {
				System.out.println("Not found, file with name");
			}
		}
		
		/* This function puts edge into the Adjacency list */
		public void putedgeinadjlist (int vertex1, int vertex2, int weight){
			edge e1 = new edge (vertex1, vertex2, weight);
			getvertext(vertex1).add(e1);
			edge e2 = new edge (vertex2, vertex1, weight);
			getvertext(vertex2).add(e2);
		}
		
		/*Get the neighbours of any particular vertex say 0 or 1 etc.*/
		public LinkedList<edge> getvertext (int vertex){
				return adj_list.get(vertex);
		}
		
		/*Method used to print the Adjacency list, not required in general but good for verification*/
		public void print(){
			for (int i=0; i< no_vertices; i++) {
				for (edge edge1 : getvertext(i)) {
					//System.out.print(edge1.vertex2+","+edge1.weight);
					//System.out.print(" ");
				}
				//System.out.println();
			}			
		}

	}


	/* Node class is used to represent nodes which are basically used to insert into Fibonacci heap, It basically implements doubly linked circular list*/

	public class node{
		//Value contains the node identity 
		int value;
		//Parent of this node
		node parent;
		//Child of this node
		node child;
		//Who is the right child
		node right;
		//Who is the left child
		node left;
		//Key contains the distance values required for Dijkstra
		int key;
		int degree;
		//Child cut represents if any child node has been cut or not
		boolean child_cut;
		
		/*This method is basically a constructor to initialize a node with the its node identity*/
		public node(int i){
			value = i;
			left = this;
			right = this;
			key = Integer.MAX_VALUE;
			child_cut = false;	
		}
	}

	/* FibonnaciHeap is the class which is used to implement the priority queue for the Dijkstra algorithm*/
	public class FibonnaciHeap{
		int no_nodes;
		node min_node;
		
		/*Initialize the heap with zero nodes and minimum node set to null as no elements present*/
		public void initialize () {
			no_nodes = 0;
			min_node = null;
		}
		
		/*Constructor call for creating FibonacciHeap object*/
		public FibonnaciHeap(){
			initialize();
		}
		
		/*Check to see if the heap is empty or not*/
		public boolean is_empty(){
			if (min_node == null){
				return true;
			}
			else {
				return false;
			}
		}
		
		/*Get the minimum node of the FibonacciHeap*/
		public node getminnode (){
			return min_node;
		}
		
		/*Insert into Fibonacci: Whenever a node is to be inserted into FibonacciHeap this function is called. 
		It create a new singleton tree. It is added to root list; minimum node pointer is updated if necessary.*/
		public void insert(node x, int y){
			x.key = y;
			//
			if (!is_empty()){
			
				//Insert element between minimum element and the element right to it
				//Doubly linked list so pointers change accordingly
				
				x.left = min_node;
				x.right = min_node.right;
				
				min_node.right = x;
				x.right.left = x;
				
				//check if the key inserted is less than minimum key	
				if (x.key < min_node.key){
					min_node = x;
				}
				
			}
			
			else {
				//When list is empty prior to insertion the node inserted is made the minimum node
				min_node = x;		
			}
			
			no_nodes++;
		}
		
		/* Decrease key : 
		Case1: When the property of FibonacciHeap that key of parent is less than children is not violated.
			a)Decrease key of x
			b)Change heap minimum pointer (if necessary).
			
		Case2: When the property of FibonacciHeap that key of parent is less than children is violated.
			a)Decrease key of x.
			b)Cut the tree rooted at x. Then meld into root list, and mark its child cut as false.
			c)If parent of node x has child cut value false, mark it as true.
			d)If parent of node x has child cut value true. Cut parent  meld into root list, and mark its child cut false (and do so recursively for all ancestors that lose a second child).
		*/
		public void decrease_key(node x, int k){
			//k is the amount by which key is to be decreased
			
			//Error notification if key decrease value higher than key, this is because the key values cannot be negative
			if ( k > x.key) {
				System.out.println(" Decrease key by such magnitude is not possible");
			}
			
			else {
				
				x.key = x.key - k;
				node node_parent = x.parent;
				
				//Checking parent of the node cut
				if ((node_parent != null) && (x.key < node_parent.key)) {
					//Call to function cut
					cut(x, node_parent);
					//Call to cascading cut function
					cascadingcut(node_parent);
				}
				
				//Set the minimum node pointer if decrease key results in a new minimum
				if (min_node != null){
					if (x.key < min_node.key){
						min_node = x;
					}
				}
			}
		}
		
		/*	Child cut is used to removed the node from sibling list at the level at which it is parent, Also the same node is inserted into the top 
		level root list and minimum is updated if necessay , Child_cut value for the node is set to false*/
		public void cut (node n_child, node n_parent){
			n_child.left.right = n_child.right;
			n_child.right.left = n_child.left;
			
			n_parent.degree --;
			
			//if the parent uses the node removed as the min pointer to its doubly linked list of children, update it
			
			if (n_parent.child == n_child){
				n_parent.child = n_child.right;
			}
			
			if (n_parent.degree == 0){
				n_parent.child = null;
			}
		
			//Making the cut node a part of root list 
			
			n_child.left = min_node;
			n_child.right = min_node.right;
			
			min_node.right = n_child;
			n_child.right.left = n_child;
			
			n_child.child_cut = false;
			n_child.parent = null;
			
		}
			
		/*Cascading cut is used to check if the node being cut has ancestors whose child_cut value is true, if this is the case then cut and 
		cascading cut are called for those nodes*/	
		public void cascadingcut(node n_parent){
		
			node n_grandparent = n_parent.parent;
			
			if (n_grandparent != null){
				//No child cut for parent
				if (n_parent.child_cut == false){
					n_parent.child_cut = true;
				}
				//Parent has child cut true, so propagate the cut one level up
				else {
					cut(n_parent, n_grandparent);
					cascadingcut(n_grandparent);
				}
			}
		}
			
			
		/*This is one of the main and important functions for the FibonacciHeap. It removes the minimum node element from the heap. If the node
		being removed has children all are sent to the root list of FibonacciHeap and then consolidation is called */
		public node removemin (){
			
			node z = min_node;
			//System.out.println("Min node is "+min_node.value + " weight is " + min_node.key);
			
		
			if (z != null){
				node a = z.child;
				int no_child = z.degree;
				node temp;
				
				//Till there are children, each of them is removed from the child list of the node being removed and inserted at root list level
				while (no_child > 0){
					temp = a.right;
					
					
					//deleting node from child list
					a.right.left = a.left;
					a.left.right = a.right;
					
					//adding node to root list
					a.left = min_node;
					a.right = min_node.right;
					min_node.right = a;
					a.right.left = a;				
					
					
					a.parent = null;
					a = temp;
				 
					no_child -- ;
									
				}
				
				// Removing minimum from the root list
				z.right.left = z.left;
				z.left.right = z.right;				
				
				//Check if the list is empty after removal of minimum node
				if (z.right == z){
					min_node = null;
				}
				else {
					//Call consolidate when the Heap is not empty after removal of the minimum node
					min_node = z.right;
					consolidate();
					
				}	
				no_nodes --;
				
			}
			//Remove Min function returns the node being removed
			return z;
		}
			
			
		/*Consolidate is called in FibonacciHeap to pair wise combine all the node based on the degree of nodes such that after consolidation functions 
		no two nodes in the FibonacciHeap have the same degree*/
		public void consolidate (){
		
			double oneOverLogPhi = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);
			int arraySize = ((int) Math.floor(Math.log(no_nodes) * oneOverLogPhi)) + 1;
			/*To maintain nodes in a ArrayList in which only one node of a particular degree is stored*/
			ArrayList<node> array =new ArrayList<node>(arraySize);
			 
			for (int i = 0; i < arraySize; i++) {
					array.add(null);		
			}

			node current_node = min_node;
			//How many elements are present in the FibonacciHeap root level to combine is kept track using consol_count variable
			int consol_count = 0;
			
			if (current_node != null) {
				consol_count++;
				current_node = current_node.right;
				while (current_node != min_node) {
					consol_count++;
					current_node = current_node.right;
				}
			}
			
			/*Looping to iterate over the root list nodes and combine based on their degree. When two nodes of same degree are present,
			one is made child of other using Link function*/
			while (consol_count > 0) {
                int deg_current = current_node.degree;
				node next_node = current_node.right;
                while (array.get(deg_current) != null) {
                    node in_array_node = array.get(deg_current);
                    if (current_node.key > in_array_node.key) {
                        //Exchange required so that every time in_array_node contains the child and current_node the parent 
						node temp = current_node;
                        current_node = in_array_node;
                        in_array_node = temp;
                    }
					//Make in_array_node child of current node
                    link(in_array_node, current_node);
                    array.set(deg_current,null);
                    deg_current++;
                }
                array.set(deg_current,current_node);
                current_node = next_node;
                consol_count--;
            }
				
			min_node = null;
			
			//Now combining all the nodes at root level which are not of the same degree and then setting the minimum node properly
			for (int i = 0; i < arraySize ; i++){
				
				node y = array.get(i);
				if (y != null) {
					if (min_node != null){			
						y.left.right = y.right;
						y.right.left = y.left;
						
						//Add the node in the doubly linked list
						y.left= min_node; 
						y.right= min_node.right;	
						
						min_node.right = y;
						y.right.left = y;
						
						if (y.key < min_node.key){
							min_node = y;
						
						}
							
		            }	
					else {
							min_node = y;
					}				
		         }
			}      
		}
			
		//Link is called to to make a same degree node child of another
		public void link(node chd, node par){
			//Removing the child from its present location
			chd.right.left = chd.left; 
			chd.left.right = chd.right;
			//Setting new parent for the child
			chd.parent = par;	
			if (par.child == null){
				//Insert into parent's child list which is empty
				par.child = chd;
				chd.right = chd;
				chd.left = chd;	
			}
			
			else {
				//Insert into parent's child list which is not empty
				node existing_child = par.child;
				chd.right = existing_child.right;
				chd.left = existing_child;
				existing_child.right = chd;
				chd.right.left = chd;
			}
			//Update parents degree and set its child cut to false.	
			par.degree ++;
			chd.child_cut = false;
		}	
			
	}


	
	/*Dijkstra algorithm: This is main algorithm which implements the shortest path for a given source and destination based on the adjacency list and 
	using Fibonacci heap as means to select the minimum distance next hop vertex and then repeatedly perform this till we reach the destination */
	
	public void ssp (Graph g1, int src, int dest){

		int num_vertices = g1.getnoofvertices();
		FibonnaciHeap fibo = new FibonnaciHeap();
		
		//Nodes which are to be inserted into the FibonacciHeap
		ArrayList<node> node_array = new ArrayList<node>(num_vertices);
		//To keep track of the vertices in the graph which have been visited
		ArrayList<Boolean> visited = new ArrayList<Boolean>(num_vertices);
		//To keep track of the current parent of each vertex based on index 		
		ArrayList<Integer> parent = new ArrayList<Integer>(num_vertices);
		//To print the path from source to destination
		ArrayList<Integer> finalvertices = new ArrayList<Integer>();
		//Keeps track the cost of path from source to all the vertices in the graph
		int[] pathcost = new int[num_vertices];
			
		//Initially no vertices are traversed	
		for (int i = 0; i < num_vertices; i++){
			parent.add(-1);
		}
			 
		//Path cost from source to each vertex to other is zero 
		for(int i=0 ; i< pathcost.length;i++){
			pathcost[i]=0;
		}
			
		//Distance of source to source is set to 0, and the distance values of all other vertices is set to infinity	
		for (int i = 0; i < num_vertices; i++){
			if (i == src){
				node h = new node (i);
				node_array.add(h);
				fibo.insert(node_array.get(i), 0);
				visited.add(false);
			}
			else {
				node h = new node (i);
				node_array.add(h);
				//node_array.get(i).value = i;
				fibo.insert(node_array.get(i), Integer.MAX_VALUE);
				visited.add(false);
			}
		}
			
		/* Till there exists nodes in FibonacciHeap, continue removing one by one, updating distance values each time for the nodes adjacent to the node
		being removed*/	
		while (!fibo.is_empty()){
			node h = fibo.removemin();
			int key1 = h.key;
			int retval = h.value;
			//Mark the node as visited
			visited.set(retval,true);
				
			/*Check if the vertices adjacent to vertex being removed have more current distance values more than the distance values through the vertex being 
			removed , If yes update the values and set this vertex as parent for those vertices*/
			for (Graph.edge edge1 : g1.getvertext(retval)) {
				if (visited.get(edge1.vertex2) == false){
					int update_value = key1 + edge1.weight;	
					if (node_array.get(edge1.vertex2).key > update_value) {
						int key_change_value = node_array.get(edge1.vertex2).key - update_value;
						fibo.decrease_key(node_array.get(edge1.vertex2), key_change_value);
						parent.set(edge1.vertex2, edge1.vertex1);
						pathcost[edge1.vertex2]= update_value; 
					}
				}
			}
		}
			
		int y = dest;
		while (true){
			if (y == src){
				finalvertices.add(src);
				break;
			}
				
			else {
				finalvertices.add(y);
				y = parent.get(y);
			}
		}
		
		int z = finalvertices.size();
		
		//Final output for the path is printed here
		System.out.println(pathcost[dest]);
	
		for (int i=z-1; i>=0; i--){
				System.out.print(finalvertices.get(i)+" ");
		}
			
		node_array.clear();
		parent.clear();
		visited.clear();
		finalvertices.clear();
		
	}
		
	public static void main(String[] args){
		//Argument passing
		String l = args[0];
		String m = args[1];
		String n = args[2];
		//Intializing of object
		ssp dij = new ssp();
		
		int src = Integer.parseInt(args[1]);
		int dest = Integer.parseInt(args[2]);
		ssp.Graph g1 = dij.new Graph();
		//Create adjency list
		g1.tryfunction (l);
		//carry out ssp algorithm
		dij.ssp(g1, src, dest);
			
	}
}


