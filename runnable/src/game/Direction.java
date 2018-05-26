package game;

public enum Direction {
	NORTH(0),
	EAST(1),
	SOUTH(2),
	WEST(3),
	IN(4);
	
	int val;
	Direction(int val){
		this.val = val;
	}
	Direction opposite() {
		if(this == IN) {
			return this;
		}
		return getDirection(val+2);
	}
	
	static Direction getDirection(int d) {
		d %= 4;
		switch(d) {
			case 0: return NORTH;
			case 1: return EAST;
			case 2: return SOUTH;
			case 3: return WEST;
		}
		return null;
	}
}