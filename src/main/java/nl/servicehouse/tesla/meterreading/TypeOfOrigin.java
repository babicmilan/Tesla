package nl.servicehouse.tesla.meterreading;

public enum TypeOfOrigin {

    PHYSICAL_READING,//
    P4_READING,//
    CALCULATED,// == estimated
    READING_BY_CUSTOMER,//
    READING_BY_MV,//
    CALCULATED_BY_MV,//
    PHYSICAL_READING_OLD,//
    CALCULATED_BY_MARKET_PARTY,//
    AGREED,//
    FORECASTED,//
    FROM_DEVICE
}
