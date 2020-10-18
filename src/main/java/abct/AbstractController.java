package abct;

public class AbstractController {
    //Prevents user from clicking other stages
    public static Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }
}
