Feature: Access point and meters to be created in the metering module Tesla as soon as an access point is created in the Billing Engine so it is ready to receive meterreadings and volumes and the module is able to support the business process appropriately

  Scenario: AccessPoint creation on event from BE

    Given An access point is created in the Billing Engine
    When New event access point created is available and EANCode is not known in TESLA
