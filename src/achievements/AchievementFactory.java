package achievements;

public class AchievementFactory {
	
	private final String ACHIEVEMENT = "Achievement";
	public AchievementFactory(){
	}
	
	public Achievement genAchievement(String type) throws ClassNotFoundException{
		System.out.println("READ UNDER");
		System.out.println(type);
		Class<?> clazz = Class.forName("achievements." + type + ACHIEVEMENT);
		try{
			return (Achievement) clazz.getDeclaredConstructor().newInstance();
		}
		catch(Exception e){
			return null;
		}
	}
}