package game;

public class LevelSave {
	public static String getFileFromName(String name) {
		return System.getProperty("user.dir") + "/data/level_" + name + ".bin";
	}
}
