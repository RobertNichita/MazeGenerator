/*
 * Robert Nichita
 * 
 * 
 */
public class Cell {//cell class
	boolean isVisited = false;//stores whether this cell has been visited or not
	boolean Top = true,Bottom = true,Left = true,Right = true;//stores whether the top, bottom, right, or left walls of this cell are present
	int row, col;//stores the row and column of this cell
	public Cell(int row, int col){//constructor for cell
		this.row = row;//the row passed in arguments is the row of this cell
		this.col = col;//the column passed in arguments is the column of this cell
	}
	
	boolean[] areNeighboursVisited(Cell[][] maze,int w, int h)//returns whether or not the neighbors of this cell are visited
	{
		boolean[]Visited = new boolean[4];//boolean array storing visited cells
		
		if(this.col == 0){//if the column is 0
		Visited[0] = true;//then the cell above us is considered visited because there is no cell there
		}else{
		Visited[0] = maze[this.row][this.col-1].isVisited;//otherwise the cell will be denoted visited based on its state
		}
		
		if(this.col == w-1){//if we are at the right wall of the maze
		Visited[2] = true;//the the cell to the right of us is considered visited 
		}else{
		Visited[2] = maze[this.row][this.col+1].isVisited;//get the state of the cell to the right
		}
		
		if(this.row == 0){//if we are at the top wall of the maze
		Visited[3] = true;//the cell above us is considered visited
		}else{
		Visited[3] = maze[this.row-1][this.col].isVisited;//get the state of the cell above us
		}
		
		if(this.row == h-1){//if we are at the bottom wall of the maze
		Visited[1] = true;//the cell below is considered visited
		
		}else{
		Visited[1] = maze[this.row+1][this.col].isVisited;//get the state of the cell below
		}
		return Visited;//return the array of the states of the surrounding 4 cells
	}

}
