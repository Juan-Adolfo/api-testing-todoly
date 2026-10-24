package runner;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Runner {

    @Before
    public void beforeCucumber(Scenario scenario){
        System.out.println("Before Cucumber: "+scenario.getName()+" "+scenario.getSourceTagNames());
    }

    @After
    public void afterCucumber(Scenario scenario){
        System.out.println("After Cucumber: "+scenario.getName()+" -> "+scenario.getStatus());
    }
}
