import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
/*
 * Ding Lin
 */

public class GG5680 extends Application {
	List<Circle> circles = new ArrayList<Circle>();
	List<Line> lines = new ArrayList<>();
	List<Edge> edges = new ArrayList<>();
	List<Text> texts = new ArrayList<>();

	int selectedIndex = -1;
	VBox left;
	Pane right;
	public static void main(String[]args){

		launch(args);
	}


	@Override
	public void start(Stage window) throws Exception{
		Button bt1,bt2,bt3,bt4;
		RadioButton rb1,rb2,rb3,rb4,rb5;
		Scene scene1;
		TextField textField = new TextField();
		textField.setMaxSize(275, 300);
		Stage window1 = new Stage();
		window1.setTitle("Graph GUI");



		BorderPane bp = new BorderPane();
		left = new VBox(20);
		right = new Pane();
		right.relocate(250, 75);
		right.setPrefSize(725,  200);
		right.setStyle("-fx-border-color: black;");
		bp.setLeft(left);
		bp.setRight(right);


		rb1 = new RadioButton("Add vertex");
		rb2 = new RadioButton("Add Edge");
		rb3 = new RadioButton("Move vertex");
		rb4 = new RadioButton("Shortest Path");
		rb5 = new RadioButton("Change a weight to:");
		bt1 = new Button("Add All Edge");
		bt2 = new Button("Random Weights");
		bt3 = new Button("Minimal Spanning Tree");
		bt4 = new Button("Help");


		ToggleGroup tg = new ToggleGroup();
		tg.selectedToggleProperty().addListener((observable, oldVal, newVal) -> System.out.println(newVal + " was selected"));

		rb1.setToggleGroup(tg);
		rb2.setToggleGroup(tg);
		rb3.setToggleGroup(tg);
		rb4.setToggleGroup(tg);
		rb5.setToggleGroup(tg);

		bt1.setMaxSize(275, 300);
		bt2.setMaxSize(275, 300);
		bt3.setMaxSize(275, 300);
		bt4.setMaxSize(275, 300);

		right.setOnMouseClicked( (MouseEvent me) -> // when right pane been clicked we will do...
		{
			if(rb1.isSelected())// add vertex is selected
			{
				clearSelected();
				Circle circle = new Circle(me.getX(),me.getY(),10);// create a new circle by the right pane is clicked  after rb1 is selected.
				circles.add(circle);// add circle into array list circles.
				circle.setStroke(Color.BLACK);
				circle.setFill(Color.RED);
				right.getChildren().add(circle);
				selectedIndex = -1;
				System.out.println(circles);
			}

			if(rb2.isSelected()) // add edge us selected
			{
				// get the clicked point's coordinates
				double x = me.getX();
				double y = me.getY();
				// find the a circle is close to this point
				for (int i = 0; i < circles.size(); i++) {
					Circle circle = circles.get(i);
					if (distance(x, y, circle.getCenterX(), circle.getCenterY()) < 10) {
						if (selectedIndex == -1) {
							// record the first clicked circle
							selectedIndex = i;
							circle.setFill(Color.GREEN);
						}
						else {
							// second clicked circle
							if (addEdge(selectedIndex, i)) {
								// if there is no exist edge between them, create one
								Line line = new Line();
								Text text = new Text();
								lines.add(line);
								texts.add(text);
								right.getChildren().addAll(line, text);
								drawLines(null);
							}
							clearSelected();
						}
					}
				}
			}

			if (rb3.isSelected()) // move vertex is selected
			{
				double x = me.getX();
				double y = me.getY();

				if (selectedIndex == -1) {
					// find a circle close to the clicked point
					for (int i = 0; i < circles.size(); i++) {
						Circle circle = circles.get(i);
						if (distance(x, y, circle.getCenterX(), circle.getCenterY()) < 10) {
							if (selectedIndex == -1) {
								selectedIndex = i;
								circle.setFill(Color.GREEN);
							}
						}
					}
				}
				else {
					// already selected a circle, move it to new position
					Circle circle = circles.get(selectedIndex);
					circle.setCenterX(x);
					circle.setCenterY(y);
					drawLines(null);
					clearSelected();
				}
			}

			if (rb5.isSelected()) // change weight is selected
			{

				double x = me.getX();
				double y = me.getY();
				// find the circle close to clicked point
				for (int i = 0; i < circles.size(); i++) {
					Circle circle = circles.get(i);
					if (distance(x, y, circle.getCenterX(), circle.getCenterY()) < 10) {
						// find first circle
						if (selectedIndex == -1) {
							selectedIndex = i;
							circle.setFill(Color.GREEN);
						}
						else {
							// find second circle
							// update the weight between them
							updateEdge(selectedIndex, i, Integer.parseInt(textField.getText()));
							drawLines(null);
							clearSelected();
						}
					}
				}
			}

		});
		//	   end for radio button

		// add all edge
		bt1.setOnAction((ActionEvent) -> {
			// clear exist edges
			for (Line line : lines) {
				right.getChildren().remove(line);
			}
			lines.clear();
			edges.clear();
			texts.clear();
			// all edges between all vertices
			for (int i = 0; i < circles.size() - 1; i++) {
				for (int j = i + 1; j < circles.size(); j++) {
					if (addEdge(i, j)) {
						Line line = new Line();
						Text text = new Text();
						lines.add(line);
						texts.add(text);
						right.getChildren().addAll(line, text);
					}
				}
			}
			drawLines(null);
		});

		// assign random weights to all edges
		bt2.setOnAction((ActionEvent) ->{
			Random rnd = new Random();
			for (Edge e : edges) {
				e.weight = rnd.nextInt(100) + 1;
			}
			drawLines(null);
		});

		// calculate the MST
		bt3.setOnAction((ActionEvent) ->{
			if (checkWeight()) {
				Edge[] result = KruskalMST();
				drawLines(result);
			}
		});

		bt4.setOnAction((ActionEvent) ->{
			Stage popUpWindow = new Stage();
			popUpWindow.initModality(Modality.APPLICATION_MODAL);
			popUpWindow.setTitle("Instruction");
			popUpWindow.setMinWidth(300);

			Label label = new Label("Just clicks the buttons and create your own pathes!");
			Button  closeButton = new Button("Close the Window");
			closeButton.setOnAction(e-> popUpWindow.close());

			VBox layoutPop =  new VBox(15);
			layoutPop.getChildren().addAll(label,closeButton);
			layoutPop.setAlignment(Pos.CENTER);

			Scene scene2 = new Scene(layoutPop);
			popUpWindow.setScene(scene2);
			popUpWindow.show();
		});

		//Layout1, children lay out in Vertical column
		left.getChildren().addAll(rb1,rb2,rb3,rb4,rb5,textField,bt1,bt2,bt3,bt4);
		scene1 = new Scene(bp, 900,406);
		window1.setScene(scene1);
		window1.show();
	}

	// calculate the distance between two points
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	// clear selected point
	private void clearSelected() {
		for (Circle c : circles) {
			c.setFill(Color.RED);
			selectedIndex = -1;
		}
	}

	// add an edge to edge list
	private boolean addEdge(int src, int dest) {
		for (Edge e : edges) {
			if ((e.src == src && e.dest == dest) ||(e.src == dest && e.dest == src)) {
				return false;
			}
		}
		edges.add(new Edge(src, dest));
		return true;
	}

	// find the index of an edge with given information
	private int findEdge(int src, int dest) {
		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			if ((e.src == src && e.dest == dest) ||(e.src == dest && e.dest == src)) {
				return i;
			}
		}
		return -1;
	}

	// update the edge's weight between to vertices
	private void updateEdge(int src, int dest, int weight) {
		for (Edge e : edges) {
			if ((e.src == src && e.dest == dest) ||(e.src == dest && e.dest == src)) {
				e.weight = weight;
			}
		}
	}

	// check if all edges have weight
	private boolean checkWeight() {
		for (Edge e : edges) {
			if (e.weight == Integer.MIN_VALUE) {
				return false;
			}
		}
		return true;
	}

	// draw all lines, if some lines are highlighted, use another color
	private void drawLines(Edge[] highlights) {
		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			Line line = lines.get(i);
			line.setStartX(circles.get(e.src).getCenterX());
			line.setStartY(circles.get(e.src).getCenterY());
			line.setEndX(circles.get(e.dest).getCenterX());
			line.setEndY(circles.get(e.dest).getCenterY());
			line.setStrokeWidth(4);
			line.setStroke(Color.BLUE);

			Text text = texts.get(i);
			text.setX((circles.get(e.src).getCenterX() + circles.get(e.dest).getCenterX()) / 2);
			text.setY((circles.get(e.src).getCenterY() + circles.get(e.dest).getCenterY()) / 2);
			if (e.weight != Integer.MIN_VALUE) {
				text.setText(String.valueOf(e.weight));
			}
			else {
				text.setText("");
			}
			text.setStyle("-fx-font-size: 24");
			text.setFill(Color.BLUE);
		}

		if (highlights != null) {
			for (Edge e : highlights) {
				Line line = lines.get(findEdge(e.src, e.dest));
				line.setStroke(Color.GREEN);

				Text text = texts.get(findEdge(e.src, e.dest));
				text.setFill(Color.GREEN);
			}
		}
	}

	class Edge implements Comparable<Edge> {
		int src, dest, weight;
		Edge(int src, int dest) {
			this.src = src;
			this.dest = dest;
			this.weight = Integer.MIN_VALUE;
		}
		public int compareTo(Edge compareEdge)
		{
			return this.weight-compareEdge.weight;
		}
	}
	class subset
	{
		int parent, rank;
	}
	// A utility function to find set of an element i
	// (uses path compression technique)
	private int find(subset subsets[], int i)
	{
		// find root and make root as parent of i (path compression)
		if (subsets[i].parent != i)
			subsets[i].parent = find(subsets, subsets[i].parent);

		return subsets[i].parent;
	}

	// A function that does union of two sets of x and y
	// (uses union by rank)
	private void Union(subset subsets[], int x, int y)
	{
		int xroot = find(subsets, x);
		int yroot = find(subsets, y);

		// Attach smaller rank tree under root of high rank tree
		// (Union by Rank)
		if (subsets[xroot].rank < subsets[yroot].rank)
			subsets[xroot].parent = yroot;
		else if (subsets[xroot].rank > subsets[yroot].rank)
			subsets[yroot].parent = xroot;

			// If ranks are same, then make one as root and increment
			// its rank by one
		else
		{
			subsets[yroot].parent = xroot;
			subsets[xroot].rank++;
		}
	}

	// The main function to construct MST using Kruskal's algorithm
	private Edge[] KruskalMST()
	{
		Edge result[] = new Edge[circles.size() - 1];  // This will store the resultant MST
		int e = 0;  // An index variable, used for result[]
		int i = 0;  // An index variable, used for sorted edges
		for (i=0; i<result.length; ++i)
			result[i] = new Edge(0, 0);

		// Step 1:  Sort all the edges in non-decreasing order of their
		// weight.  If we are not allowed to change the given graph, we
		// can create a copy of array of edges
		Collections.sort(edges);

		// Allocate memory for creating V ssubsets
		subset subsets[] = new subset[circles.size()];
		for(i=0; i<subsets.length; ++i)
			subsets[i]=new subset();

		// Create V subsets with single elements
		for (int v = 0; v < circles.size(); ++v)
		{
			subsets[v].parent = v;
			subsets[v].rank = 0;
		}

		i = 0;  // Index used to pick next edge

		// Number of edges to be taken is equal to V-1
		while (e < circles.size() - 1)
		{
			// Step 2: Pick the smallest edge. And increment
			// the index for next iteration
			Edge next_edge = edges.get(i++);

			int x = find(subsets, next_edge.src);
			int y = find(subsets, next_edge.dest);

			// If including this edge does't cause cycle,
			// include it in result and increment the index
			// of result for next edge
			if (x != y)
			{
				result[e++] = next_edge;
				Union(subsets, x, y);
			}
			// Else discard the next_edge
		}

		return result;
	}

}