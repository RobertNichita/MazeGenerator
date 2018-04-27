import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.io.*;
/*
 *Robert Nichita 
 * Maze Generator
 * 6/15/2017
 */
public class Main extends JPanel implements KeyListener{
	public static int w,h;//width and height of the maze
	public static Cell[][] maze;//grid of cells which are contained in the maze
	public static int count = -30;//counter which determines where the chaser is in the path to the player
	public static boolean [] key = new boolean[270];//keyboard state array
	static Random rand;//random number generator
	static boolean mainMenu = true;//whether or not the user is currently looking at the main menu
	static int Visited = 1;//the number of squares the maze generator has visited
	static int[] exit = new int[2];//the exit square
	static Integer[] chaser = new Integer[2];//the chaser's coordinates
	static int [] currentCell = {0,0};//while the maze is being generated this represents the coordinates of the maze generator, while playing it represents the players coordinates
	static Stack<Integer[]> path = new Stack<Integer[]>();//the path along which the player has travelled, this is followed by the chaser
	static Stack<int[]> branch = new Stack<int[]>();//the current branch of the tree being formed in the maze, allows the maze generator to traverse backwards up the branch so it can find unvisited squares
	public static JFrame frame;//the main frame
	public static Integer[] start = {0,0};//the starting square, for reference purposes
	public Main(){//constructor
		setFocusable(true);//set the window to be focusable
		addKeyListener(this);//add a keylistener
		setVisible(true);//set it to visible
		setBackground(Color.WHITE);//make the background white
	}
	
	public void buildpath(){//this will iteratively add and remove squares from the chaser's path based on the player's movement
		boolean previousVisit = false;//this boolean stores whether the player has previously visited the square it is on
		Integer[] clone = new Integer[2];//clones the player's coordinates to avoid using a reference
		clone[0] = currentCell.clone()[0];//clone x coordinate
		clone[1] = currentCell.clone()[1];//clone y coordinate
		for(int i = 0;i<path.size();i++){//for the entire path
			if(path.elementAt(i)[0] == clone[0] && path.elementAt(i)[1] == clone[1]){//if the player is sitting on this square, and it is already part of the path, this means the player has backtracked
				path.remove(i+1);//remove the previous square the player was on from his path
				previousVisit = true;//the square that the player is on has already been visited
			}
		}
			if(!previousVisit){//if the square has not been visited
			path.push(clone);//add it to the path
			}
	}
	
	public Integer[] Follow(Stack<Integer[]> pathfromstart,int count,Integer[] chaser){//this will return an integer array denoting the coordinates the chaser should be at to follow the player
		if(count>0 && count/2 < path.size()){//if the counter is greater than 0 and the counter divided by 2 is less than the path size(to avoid arrayoutofbounds)
		return pathfromstart.elementAt(count/2);//return the square that the chaser should be at on the path;
		}
		return chaser;//otherwise return the chaser's current coordinates
	}
	
	
	public int[] scale(){//returns an array of integers which contain the x and y scaling factors, respectively
		int[] scales = {(int)(frame.getWidth()/(h+2)),(int)(frame.getHeight()/(w+2))};//calculate scaling factors and store them in an array
		return scales;//return the array
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(!mainMenu){//if we are not in the main menu
			
			if((key[KeyEvent.VK_W] || key[KeyEvent.VK_UP]) && maze[currentCell[0]][currentCell[1]].Top == false){//if UP or W is pressed and there is no top wall in the current cell
				currentCell[1] --;//move the player up
				buildpath();//re-evaluate the path of the chaser
			}
			if((key[KeyEvent.VK_S] || key[KeyEvent.VK_DOWN]) && maze[currentCell[0]][currentCell[1]].Bottom == false){//same for down
				currentCell[1]++;
				buildpath();
			}
			if((key[KeyEvent.VK_A] || key[KeyEvent.VK_LEFT]) && maze[currentCell[0]][currentCell[1]].Left == false){//same for left
				currentCell[0]--;
				buildpath();
			}
			if((key[KeyEvent.VK_D] || key[KeyEvent.VK_RIGHT]) && maze[currentCell[0]][currentCell[1]].Right == false){//same for right
				currentCell[0]++;
				buildpath();
			}
			
			count++;//increment the counter to move the chaser along the path
			chaser = Follow(path,count,chaser);//set the position of the chaser to follow the path based on the counter, and return its own coordinates by default
			
			
			int[] scales = scale();//store the scaling factor
			
			g.setColor(Color.red);//set the color to red
			g.fillOval(scales[0]*Main.currentCell[0], scales[1]*Main.currentCell[1], scales[0], scales[1]);//draw the player at its coordinates based on the scaling factor
			
			if(count>0){//if the counter is greater than 0
			g.setColor(Color.magenta);//set the color to magenta
			g.fillOval(scales[0]*Main.chaser[0], scales[1]*Main.chaser[1], scales[0], scales[1]);//draw the chaser at its current position

			}
			for(int i = 0; i<h;i++){//for the height of the maze
				for(int j= 0; j < w; j++){//for the width of the maze
					if(maze[i][j].col == exit[1] && maze[i][j].row == exit[0]){//if we are at the exit square
						g.setColor(Color.blue);//set the color to blue
						g.fillRect((scales[0] * i)+4,(scales[1]*j)+4,scales[0]-4,scales[1]-4);//draw the exit square smaller than a full square to be able to see the walls around it
						g.setColor(Color.black);//set the color to default
					}
					if(maze[i][j].Top){//if this cell has a top wall
						g.setColor(Color.blue);//draw it in blue
						g.drawLine(i*scales[0], j*scales[1], i*scales[0]+scales[0], j*scales[1]);
						g.setColor(Color.black);
					}
					if(maze[i][j].Bottom){//if this cell has a bottom wall
						g.setColor(Color.green);//draw it in green
						g.drawLine(i*scales[0], j*scales[1]+ scales[1], i*scales[0]+scales[0], j*scales[1]+scales[1]);
						g.setColor(Color.black);
					}
					if(maze[i][j].Left){//if this cell has a left wall
						g.setColor(Color.magenta);//draw it in magenta
						g.drawLine(i*scales[0], j*scales[1], i*scales[0], j*scales[1]+scales[1]);
						g.setColor(Color.black);
					}
					if(maze[i][j].Right){//if this cell has a right wall
						g.setColor(Color.orange);//draw it in orange
						g.drawLine(i*scales[0]+scales[0], j*scales[1], i*scales[0]+scales[0], j*scales[1]+scales[1]);
						g.setColor(Color.black);
					}
				}
			}
			if((currentCell[1] == exit[1] && currentCell[0] == exit[0]) || (chaser[0] == currentCell[0] && chaser[1] == currentCell[1] && count>0)){//if the player has reached the exit or the chaser has reached the player
				mainMenu = true;//we are sent back to the main menu
				Main.Visited=1;//reset all of the variables to their init states to generate a new maze
				Main.maze = new Cell[h][w];//new maze
				for(int i = 0; i < h; i++){//generate new cells
					for(int j = 0; j<w; j++){
						maze[i][j] = new Cell(i,j);
					}
				}
				Main.branch = new Stack<int[]>();//new branch
				Main.currentCell[0]=0;//set the generator to the top left corner
				Main.currentCell[1]=0;
				Main.chaser[0]=0;//set the chaser to the top left corner
				Main.chaser[1]=0;
				for(int i = 0;Main.Visited<w*h;){//generate the new maze
					Main.Generate(Main.maze,Main.Visited,Main.currentCell,Main.branch);
					}
				Main.currentCell[0]=0;//set the player to the top left corner after the maze has been generated
				Main.currentCell[1]=0;
				Main.count = -30;//reset the counter for the chaser
				Main.path = new Stack<Integer[]>();//reset the path of the player
				Main.path.push(start);//add the starting square to the path because the player starts there
				exit[0] = rand.nextInt(h);//randomly generate the exit square
				maze[exit[0]][exit[1]].Bottom = false;//remove the bottom of the exit square
			}
		}
		if(mainMenu){//if we are on the main menu
			g.setColor(Color.white);//set the color to white
			g.fillRect(0,0,getWidth(),getHeight());//fill in the background
			g.setColor(Color.green);//set the color to green
			g.setFont(new Font("Arial",Font.BOLD,24));//main menu font
			g.drawString("Press [Enter] To Play", 200, 300);//draw user prompt
			g.setColor(Color.red);//red 
			g.drawString("Press [Esc] To Quit", 200, 375);//more user prompts
			g.setColor(Color.BLACK);//black
			g.drawString("Maze Generator", 200, 100);//main title text
			if(key[KeyEvent.VK_ENTER]){//if the player presses enter
				g.setColor(Color.blue);//set the color to blue
				g.drawString("Generating...", 0, 500);//generate a new maze
				mainMenu = false;//we are no longer on the main menu
			}
			if(key[KeyEvent.VK_ESCAPE]){//if the plater presses escape
				System.exit(0);//exit the game
			}
			
		}
		try{
			Thread.sleep(100);//100ms delay between each frame of drawing
		}catch(Exception e){
			e.printStackTrace();
		}
		
		repaint();//repaint the window
	}
	
	public static void Generate(Cell[][] maze,int Visited, int[]currentCell,Stack<int[]> branch){//method for generating the maze	
	    if(Visited<w*h){
			
			int[]cellPusher = currentCell.clone();//clone the cells coordinates 
			branch.push(cellPusher);//add them to the branch
			if(!maze[currentCell[0]][currentCell[1]].isVisited){//if the current cell has not been visited
			maze[currentCell[0]][currentCell[1]].isVisited = true;//mark it as such
			Main.Visited++;//increment the counter for visited squares
			}
			ArrayList<Integer> rollable = new ArrayList<Integer>();//list storing the cells which can be randomly selected from to move the generator  
			boolean[] Neighbours = maze[currentCell[0]][currentCell[1]].areNeighboursVisited(maze,w,h);//store array of booleans which stores whether the four neighboring cells have been visited 
			for(int i = 0; i<Neighbours.length;i++){//for the length of the neighbours array
				if(!Neighbours[i]){//if the cell has not been visited
					rollable.add(i);//add it to the list of rollable cells
				}
			}
			
			int nextCell;//the next cell
			if(rollable.size()>1){//if the size of the array is greater than 1
				nextCell = rollable.get(rand.nextInt(rollable.size()));//the next cell is randomly selected from the list of rollable cells
				
				
				switch(nextCell){//switch / case on the nextcell
				case 0://if the cell is above the current cell
					//System.out.println(0); debugging prints
					maze[currentCell[0]][currentCell[1]].Top = false;//remove the top of the current cell
					if(currentCell[1]>=0)//if we are not touching the top edge of the map
					maze[currentCell[0]][currentCell[1]-1].Bottom = false;//the bottom of the cell above us is removed as well
					currentCell[1]-=1;//move the current cell up a cell
					break;//close the switch
				case 1: //same for the cell to the right of the current cell
					//System.out.println(1);
					maze[currentCell[0]][currentCell[1]].Right = false;
					if(currentCell[0]<h)
					maze[currentCell[0]+1][currentCell[1]].Left = false;
					currentCell[0]+=1;
					break;
				case 2://same for the cell below the current cell
				//System.out.println(2);
					maze[currentCell[0]][currentCell[1]].Bottom = false;
					if(currentCell[1]<w)
					maze[currentCell[0]][currentCell[1]+1].Top = false;
					currentCell[1]+=1;
					break;
				case 3: //same for the cell to the left of the current cell
				//System.out.println(3);
					maze[currentCell[0]][currentCell[1]].Left = false;
					if(currentCell[0]>=0)
					maze[currentCell[0]-1][currentCell[1]].Right = false;
					currentCell[0]-=1;
					break;
				}
			}else if(rollable.size() == 1){//if there is only one element in the array
				nextCell = rollable.get(0);//that is the next cell
				
				switch(nextCell){//the same case switch as the above one
				case 0:
					//System.out.println(0);
					maze[currentCell[0]][currentCell[1]].Top = false;
					if(currentCell[0]>=0)
					maze[currentCell[0]][currentCell[1]-1].Bottom = false;
					currentCell[1]-=1;
					break;
				case 1:
					//System.out.println(1);
					maze[currentCell[0]][currentCell[1]].Right = false;
					if(currentCell[0]<w)
					maze[currentCell[0]+1][currentCell[1]].Left = false;
					currentCell[0]+=1;
					break;
				case 2:
					//System.out.println(2);
					maze[currentCell[0]][currentCell[1]].Bottom = false;
					if(currentCell[1]<h)
					maze[currentCell[0]][currentCell[1]+1].Top = false;
					currentCell[1]+=1;
					break;
				case 3:
					//System.out.println(3);
					maze[currentCell[0]][currentCell[1]].Left = false;
					if(currentCell[0]>=0)
					maze[currentCell[0]-1][currentCell[1]].Right = false;
					currentCell[0]-=1;
					break;
				}
			}else{//if there are no elements in the array
				branch.pop();//the past cell is the first cell we remove
				Main.currentCell = branch.pop();//the current cell is returned by removing the next cell in the branch and storing it
				//this will allow the generator to backtrack until there are more cells which are not visited
			}

		}
	}
	
	public static void main(String[] args)throws IOException{//main method
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		rand = new Random();//init randomizer
		path.push(start);//push the starting cell to the chaser path
		chaser = start;//place the chaser at the starting cell
		System.out.println("Please input the...");
		System.out.println("Width of the maze: ");
		w = Integer.parseInt(br.readLine());//set the width of the maze in cells
		System.out.println("Height of the maze: ");
		h = Integer.parseInt(br.readLine());//set the height of the maze in terms of cells
		maze = new Cell[h][w];//make a blank maze
		for(int i = 0; i < h; i++){
			for(int j = 0; j<w; j++){
				maze[i][j] = new Cell(i,j);//init all of the cells in the maze
			}
		}
		for(int i = 0;Main.Visited<w*h;){//generate the maze
		Main.Generate(Main.maze,Main.Visited,Main.currentCell,Main.branch);
		}
		currentCell[0] = 0;//set the player to the top left corner
		currentCell[1] = 0;
		exit[0] = rand.nextInt(h);//randomly generate the exit
		exit[1] = w-1;//at the bottom of the maze
		maze[exit[0]][exit[1]].Bottom = false;//remove the bottom of the exit cell
		frame = new JFrame();//init the frame
		frame.setTitle("Maze Generator");//set the title of the frame
		frame.getContentPane().add(new Main());//add the main JPanel to the frame's content pane
		frame.pack();//pack the content pane
		frame.setResizable(false);//remove the ability to resize the window
		frame.setSize(600,600);//set the size of the window
		frame.setVisible(true);//set the window to be visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//the operation performed when the x is clicked is the window closing
		
	}

	@Override//keylistener
	public void keyPressed(KeyEvent e) {//if a key is pressed
		key[e.getKeyCode()] = true;//set it to true in the keyboard state
		
	}

	@Override
	public void keyReleased(KeyEvent e) {//if the key is released
		key[e.getKeyCode()] = false;//set it to false in the keyboard state
		
	}

	@Override
	public void keyTyped(KeyEvent e) {//blank keypressed
		// TODO Auto-generated method stub
		
	}
}
