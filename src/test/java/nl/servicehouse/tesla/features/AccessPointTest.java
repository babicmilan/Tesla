package nl.servicehouse.tesla.features;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;


@CucumberOptions(
        features = "classpath:feature/tesla/accesspoint-creation.feature",
        glue = {
                "nl.servicehouse.tesla.stepdefinitions"
        },
        plugin = { "json:target/cucumber-output/tesla/accesspoint-creation.json" })
@RunWith(EnvironmentProfileCucumber.class)
public class AccessPointTest{ }
