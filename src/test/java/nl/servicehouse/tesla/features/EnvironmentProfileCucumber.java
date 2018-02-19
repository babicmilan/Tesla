package nl.servicehouse.tesla.features;

import java.io.IOException;

import org.junit.runners.model.InitializationError;

import net.serenitybdd.cucumber.CucumberWithSerenity;

public class EnvironmentProfileCucumber extends CucumberWithSerenity {

    public EnvironmentProfileCucumber(Class clazz) throws InitializationError, IOException {
        super(clazz);
    }
}
